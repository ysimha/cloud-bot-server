package ys.cloud.sbot.logic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import ys.cloud.sbot.exchange.TradeRecord;
import ys.cloud.sbot.exchange.binance.enums.OrderStatus;
import ys.cloud.sbot.model.instance.BotInstance;
import ys.cloud.sbot.model.instance.BotInstanceMongoOps;
import ys.cloud.sbot.model.instance.BotInstanceRepository;

import java.util.function.Consumer;
import java.util.function.Function;

@Service
@Slf4j
public class BotMaintenance {

    @Autowired BotInstanceRepository botInstanceRepository;
    @Autowired BotInstanceMongoOps botInstanceMongoOps;
    @Autowired BotOperationsService botOperationsService;
    @Autowired BotTerminator        botTerminator;

    public Consumer<? super BotInstance> maintenance() {

        return (BotInstance botInstance) -> {

            BotInstance.fromContext()//.log()

//                    .doOnNext(this::updateLastTicker)
                    .flatMap(botInstanceMongoOps::saveState)
//                    .doOnNext(r->log.warn("save result: "+r))

                    .flatMap(r-> BotInstance.fromContext())

                    //handle full sell order
                    .flatMap(this::checkFullSellOrder) //return empty if order is not full
                    .flatMap(botOperationsService::handleSellTrade)

                    .flatMap(botInstanceMongoOps::saveState)
                    .flatMap(r-> BotInstance.fromContext())

                    .flatMap(bot->{
                        if ( bot.getState().getPosition().isDone() ){
                            return botTerminator.recordAndEndSession();
                        }else{
                           return botOperationsService.placeLimitSellOrder(bot);
                        }
                    })

                    .flatMap(r-> BotInstance.fromContext().flatMap(botInstanceRepository::save))

                    //release method always
                    .doFinally( event->  botInstanceMongoOps.releaseFormMethod(botInstance.getId()).subscribe() )

                    .subscriberContext(initContext( botInstance ))

                    .subscribe(
                            bot -> log.debug("bot: " + bot.getProfileId() + ", maintenance successful.  instance: " + bot.getProfileId()),
                            err -> {
                                log.error("bot: " + botInstance.getProfileId() + ",ERROR while maintenance .  instance: " + botInstance.getProfileId(), err);
                            },
                            () -> log.debug("**** bot: " + botInstance.getProfileId() + ", maintenance complete. ")
                    );
        };
    }

    private Mono<TradeRecord> checkFullSellOrder(BotInstance botInstance) {

        return BotInstance.fromContext()
                .flatMap(botOperationsService::checkSellOrder)
                .flatMap(order -> {
                    if (order.getStatus().equals(OrderStatus.FILLED.name())) {
                        log.debug("sell order " + order.getOrderId() + ", is filled. " + order + botInstance.profileId());
                        return botOperationsService.tradeForOrder(order.getOrderId());
                    } else {
                        return Mono.empty();
                    }
                });
    }

//    private void updateLastTicker(BotInstance botInstance) {
//        log.warn("updateLastTicker");
//        State state = botInstance.getState();
//        Position position = state.getPosition();
//        Ticker ticker = tickerService.getTicker(state.getSymbol().getSymbol());
//        position.updateTicker(ticker);
//    }

//    private String df(double d) {
//        return Double.toString(d);
//    }

    private Function<Context, Context> initContext(BotInstance botInstance) {
        //FIXME add also account balances to save api call while checking base and quote balance
        return context->
                context.putAll(botInstance.subscriberContext(context));

    }
}
