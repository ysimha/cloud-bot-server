package ys.cloud.sbot.logic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;
import ys.cloud.sbot.exchange.Ticker;
import ys.cloud.sbot.exchange.binance.BinanceExchangeInfoService;
import ys.cloud.sbot.exchange.binance.BinanceTickerService;
import ys.cloud.sbot.exchange.binance.model.Symbol;
import ys.cloud.sbot.model.Position;
import ys.cloud.sbot.model.State;
import ys.cloud.sbot.model.instance.BotInstanceMongoOps;
import ys.cloud.sbot.signals.Signal;

@Service
@Slf4j
public class ActiveBotsService {
	

	@Autowired BinanceExchangeInfoService 	binanceSymbolInfoService;
	@Autowired BotInstanceMongoOps 			botInstanceMongoOps;
	@Autowired BotInitializer 				botInitializer;
	@Autowired BotMaintenance				botMaintenance;
	@Autowired BotStopLoss					botStopLoss;
	@Autowired BotTerminator                botTerminator;
	@Autowired BinanceTickerService 		tickerService;

	public void onSignal(Signal signal) {
		
		log.info("on signal : "+ signal);

		if ( !signal.getQuoteAsset().toUpperCase().equals("BTC") ){
			log.error("only BTC quote asset is supported ");
			return;
		}

		Symbol symbol = binanceSymbolInfoService.resolveSymbol( signal.getBaseAsset(),signal.getQuoteAsset());
		if( symbol==null ) {
			log.error(signal.getQuoteAsset()+"-"+signal.getBaseAsset()+" not found...");
			return;
		}

		log.info("on signal symbol: "+ symbol.getSymbol());

		botInstanceMongoOps.findStandbyIds()
		
		.publishOn(Schedulers.parallel())
		.flatMap( id-> botInstanceMongoOps.takeForMethod("onSignal", id))
		.subscribe(
					botInitializer.initialize(signal, symbol),
					
					err->log.error("error initializing bot instances on signal",err),
					
					()->log.info("initializing bot instances complete")
				);
		
		log.info("on signal exit, symbol: "+ symbol.getSymbol());
	}

	//TODO FIXME add error field to bot instance and recover from errors here
	@Scheduled(fixedRate = 30000)
	public void fixMethod(){
		int seconds = 60;
		botInstanceMongoOps.findForMaintenanceIds(seconds)
				.doOnNext(id->log.error("found bot instance ["+id+"] locked for method for at list "+seconds+" seconds, releasing from method"))
				.flatMap(botInstanceMongoOps::releaseFormMethod)
				.subscribe(System.out::println);
	}


	@Scheduled(fixedRate = 10000)
	public void botLoop(){

		botInstanceMongoOps.findActiveIds()//.log()

				.publishOn(Schedulers.parallel())

				.flatMap( id-> botInstanceMongoOps.takeForMethod("botLoop", id))

				.subscribe(
				        bot-> {
				        	log.warn("\n\n>>>> Start BOT Loop <<<<  processing bot: "+bot.profileId());

							State state = bot.getState();
							updateLastTicker(state);

							Position position = state.getPosition();
							if( position.getStoploss() > position.getLastTicker().getBid() ){
								botStopLoss.checkStoploss().accept(bot);

							}else if( bot.getState().getPosition().isDone() ) {
                                botTerminator.endSession().accept(bot);

                            }else {
                            	log.warn("botMaintenance.maintenance");
                                botMaintenance.maintenance().accept(bot);
                            }
							log.warn(">>>> END BOT Loop <<<<  bot: "+bot.profileId());
						},
						err->log.error(">>>> error  bots loop ",err),

						()->log.info(">>>> bot loop  complete")
				);
	}

	private void updateLastTicker(State state) {
		log.warn("updateLastTicker");
		Position position = state.getPosition();
		Ticker ticker = tickerService.getTicker(state.getSymbol().getSymbol());
		position.updateTicker(ticker);
	}
}

