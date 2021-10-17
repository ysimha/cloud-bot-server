package ys.cloud.sbot.logic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import ys.cloud.sbot.exceptions.BuyOrderException;
import ys.cloud.sbot.exchange.AccountService;
import ys.cloud.sbot.exchange.binance.enums.OrderStatus;
import ys.cloud.sbot.model.State;
import ys.cloud.sbot.model.instance.BotInstance;
import ys.cloud.sbot.model.instance.BotInstanceMongoOps;
import ys.cloud.sbot.model.instance.BotInstanceRepository;
import ys.cloud.sbot.signals.Signal;

import java.util.function.Consumer;
import java.util.function.Function;

@Service
@Slf4j
public class BotInitializer {
	
	@Autowired AccountService accountService;
	@Autowired BotOperationsService botOperationsService;
	@Autowired BotInstanceRepository botInstanceRepository;
	@Autowired BotInstanceMongoOps botInstanceMongoOps;

	public Consumer<? super BotInstance> initialize(Signal signal, String symbol) {

		return (BotInstance botInstance) -> {

			log.debug("@ Start initialize :" + botInstance.profileId());

			BotInstance.fromContext()
					.doOnNext( bot -> {
						bot.setState(createState(signal, symbol));
					})
					//TODO FIXME maybe its better to try order and receive error , than buying according to balance
					.flatMap( this::quoteAssetBalance )
					.doOnError(err ->
							log.error("error trying to get account balance, " + err.getMessage() + botInstance.profileId())
					)
//					log.error("error trying to get account balance, retry ones if binance error -1021, "+err.getMessage()+botInstance.profileId()))
					//TODO not tested if its a timeout issue
					//FIXME removed retry, find a new API instead to do retry on error
//			.retry(1,err->
//						 err instanceof BinanceApiException && ((BinanceApiException)err).getError().code.equals(-1021))
//			.doOnError(err->
//							log.error("error trying to get account balance AFTER retry ones."+err.getMessage()+botInstance.profileId()))
					.flatMap( this::setCurrentAmount )
					.flatMap( botOperationsService::placeMarketBuy )
					.then(BotInstance.fromContext()
							.flatMap( botInstanceMongoOps::saveState )
							.flatMap( r -> BotInstance.fromContext())

					).doOnNext( bot -> {
						if (!bot.getState().getOpenBuyOrder().getStatus().equals(OrderStatus.FILLED.name())) {
							throw new BuyOrderException("market order return not " + OrderStatus.FILLED.name() + bot.getProfileId());
						}
					})

					.flatMap(botOperationsService::buyOrderFilled)
					.flatMap(botOperationsService::buyComplete)
					.flatMap(botInstanceMongoOps::saveState)
					.flatMap(r -> BotInstance.fromContext())

					//open sell order for first target
					.flatMap(botOperationsService::placeLimitSellOrder)
					.then(BotInstance.fromContext().flatMap(botInstanceRepository::save))
					//release method always
					.doFinally(event -> botInstanceMongoOps.releaseFormMethod(botInstance.getId()).subscribe())
					.contextWrite( initContext(botInstance))
					.subscribe(
							bot -> log.debug("bot: " + bot.getProfileId() + ", initialize successfully.  instance: " + bot.getProfileId()),
							err -> {
								log.error("bot: " + botInstance.getProfileId() + ",ERROR while initialize .  instance: " + botInstance.getProfileId(), err);
							},
							() -> log.debug("++++ bot: " + botInstance.getProfileId() + ", initialize complete. ")
					);
		};
	}

	private State createState(Signal signal, String symbol) {
		State state = new State();
		state.setSymbol(symbol);
		state.setSignal(signal);
		return state;
	}

	private Function<Context, Context> initContext(BotInstance botInstance) {
		//FIXME add also account balances to save api call while checking base and quote balance
		return context->
			context.putAll(botInstance.subscriberContext(context));
	}

	private Mono<Double> quoteAssetBalance(BotInstance bot) {
		return  accountService.getAvailableBalance(bot.getExchangeAccount(), bot.getState().getSignal().getQuoteAsset());
	}

	private Mono<BotInstance> setCurrentAmount(final Double amount ) {
		return BotInstance.fromContext()
				.doOnNext(bot-> bot.getState().setCurrentAmount(Math.max(amount, bot.getDefaultAmount())));
	};
}


