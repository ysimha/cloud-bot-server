package ys.cloud.sbot.logic;

import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import ys.cloud.sbot.exceptions.SellOrderException;
import ys.cloud.sbot.exchange.binance.enums.OrderStatus;
import ys.cloud.sbot.model.Position;
import ys.cloud.sbot.model.State;
import ys.cloud.sbot.model.instance.BotInstance;
import ys.cloud.sbot.model.instance.BotInstanceMongoOps;
import ys.cloud.sbot.model.instance.BotInstanceRepository;

import java.util.function.Consumer;
import java.util.function.Function;

@Service
@Slf4j
public class BotStopLoss {

    @Autowired
    BotOperationsService botOperationsService;
    @Autowired
    BotInstanceRepository botInstanceRepository;
    @Autowired
    BotInstanceMongoOps botInstanceMongoOps;
    @Autowired
    BotTerminator botTerminator;

    public Consumer<? super BotInstance> checkStoploss() {

        return (BotInstance botInstance) -> {

            State state = botInstance.getState();
            Position position = state.getPosition();
//FIXME check if last stop loss was partial

//            if ( position.getStoploss() > position.getLastTicker().getBid() ) {

            log.debug("** Stop Loss **\n[sell -> price down], [stop lost=" + position.getStoploss() + "], [bid=" + position.getLastTicker().getBid() + "]" + botInstance.profileId());

            position.setUnderStopLoss(true);

            BotInstance.fromContext()

                    .flatMap(this::stopLoss)

                    .flatMap(r -> botTerminator.recordAndEndSession())

                    .then(BotInstance.fromContext().flatMap(botInstanceRepository::save))

                    //no need to release
                    .doFinally(event -> botInstanceMongoOps.releaseFormMethod(botInstance.getId()).subscribe())

                    .subscriberContext(initContext(botInstance))

                    .subscribe(
                            bot -> log.debug("bot: " + bot.getProfileId() + ", maintenance successful.  instance: " + bot.getProfileId()),
                            err -> {
                                log.error("bot: " + botInstance.getProfileId() + ",ERROR while maintenance .  instance: " + botInstance.getProfileId(), err);
                            },
                            () -> log.debug("**** bot: " + botInstance.getProfileId() + ", maintenance complete. ")
                    );

//            }
        };
    }


    public Mono<UpdateResult> stopLoss(BotInstance bot) {

        State state = bot.getState();
        Position position = state.getPosition();

        final double lastPrice = position.getLastTicker().getLast();
        final double quantity = position.totalQuantity();

        log.debug("[stop loss sell order] last price=" + lastPrice + ". quantity: " + quantity + bot.botInfo());

        return botOperationsService.cancelOpenOrders()

                .doOnNext(cancelResponse -> log.debug("canceled open orders response: " + cancelResponse + bot.profileId()))

                .flatMap(c -> BotInstance.fromContext())
                .flatMap(botOperationsService::sellMarketPosition)
                .flatMap(response -> {
                    if ( response.getStatus().equals(OrderStatus.FILLED.name()) ) {
                        log.debug("stop loss, sell order " + response.getOrderId() + ", is filled. " + response + bot.profileId());
                        return botOperationsService.tradeForOrder(response.getOrderId());
                    } else {
                        state.setOpenSellOrder(response);
                        return Mono.error(new SellOrderException("stop loss - sell position, sell market order return not " + OrderStatus.FILLED.name() + bot.botInfo()));
                    }
                })
                .flatMap(botOperationsService::handleSellTrade)
                .flatMap(botInstanceMongoOps::saveState)
                ;


    }

    private Function<Context, Context> initContext(BotInstance botInstance) {
        //FIXME add also account balances to context, to save api call while checking base and quote balance
        return context ->
                context.putAll(botInstance.subscriberContext(context));
    }

}
