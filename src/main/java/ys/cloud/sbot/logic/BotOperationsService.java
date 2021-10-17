package ys.cloud.sbot.logic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.exceptions.LogicViolationException;
import ys.cloud.sbot.exceptions.SellOrderException;
import ys.cloud.sbot.exchange.AccountService;
import ys.cloud.sbot.exchange.Ticker;
import ys.cloud.sbot.exchange.TradeRecord;
import ys.cloud.sbot.exchange.binance.BinanceExchangeInfoService;
import ys.cloud.sbot.exchange.binance.BinanceTickerService;
import ys.cloud.sbot.exchange.binance.enums.OrderStatus;
import ys.cloud.sbot.exchange.binance.errors.BinanceApiException;
import ys.cloud.sbot.exchange.binance.model.CancelResponse;
import ys.cloud.sbot.exchange.binance.model.GetOrderResponse;
import ys.cloud.sbot.exchange.binance.model.NewOrderResponse;
import ys.cloud.sbot.exchange.binance.model.Symbol;
import ys.cloud.sbot.model.Position;
import ys.cloud.sbot.model.State;
import ys.cloud.sbot.model.instance.BotInstance;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BotOperationsService {

    @Autowired Trader trader;
    @Autowired AccountService accountService;
    @Autowired BinanceTickerService tickerService;
    @Autowired
    BinanceExchangeInfoService binanceExchangeInfoService;

    public Mono<NewOrderResponse> placeMarketBuy(BotInstance bot) {

        State state = bot.getState();
        double buyAmount = calcBuyAmount(bot);
        log.debug("place buy order. amount=" + df(buyAmount) + bot.profileId());
        final double finalByAmount = checkBuyAmount(bot, buyAmount);
        log.debug("final buy order. amount=" + df(buyAmount) + bot.profileId());

        return trader.buyOrderMarket(bot.getExchangeAccount(), state.getSymbol(), finalByAmount)
                .doOnNext(orderResponse -> {
                    state.setOpenBuyOrder(orderResponse);
                    log.debug("buy order response: " + orderResponse);
                });
    }

    public Mono<TradeRecord> buyOrderFilled(BotInstance bot) {
        log.warn("in buyOrderFilled ");
        NewOrderResponse openBuyOrder = bot.getState().getOpenBuyOrder();
        Long orderId = openBuyOrder.getOrderId();
        return tradeForOrder(orderId)
                .doOnNext(trade -> log.debug("Trade for order " + orderId + ". " + trade.toString()))
                .flatMap(trade -> {
                    if ( trade.getPrice() == Double.NaN || trade.getPrice() == 0 ) {
                        return validateTradeResponse(trade, orderId, openBuyOrder.getOrigQty());
                    }
                    return Mono.just(trade);
                }).doOnNext(t -> bot.getState().setOpenBuyOrder(null));
    }

    public Mono<BotInstance> buyComplete(TradeRecord trade) {
        log.warn("in buyComplete");
        return BotInstance.fromContext()
                .doOnNext(
                        bot -> {
                            log.debug("buy complete. executedQty=" + df(trade.getQty()) + ", price=" + df(trade.getPrice()) + bot.profileId());
                            State state = bot.getState();
                            Position position = state.getPosition();
                            if (position == null) {
                                Symbol symbol = binanceExchangeInfoService.resolveSymbol(state.getSymbol());
                                position = new Position(trade.getPrice(), bot.getDefaultStoploss(), state, symbol);
                                state.setPosition(position);
                            }
                            position.getBuyTrades().add(trade);
                            position.setLastTicker(tickerService.getTicker(state.getSymbol()));
                            log.debug("new position " + position + bot.profileId());
                        }
                );
    }

    public Mono<TradeRecord> tradeForOrder(final Long orderId) {
        return BotInstance.fromContext()
                .flatMap(bot -> accountService.myTrades(bot.getExchangeAccount(), bot.getState().getSymbol()))
                .map(trades ->
                        trades.stream().filter(t -> t.getOrderId().equals(orderId)).collect(Collectors.toList())

                ).map(this::consolidateTrades);
    }


    public Mono<NewOrderResponse> placeLimitSellOrder(BotInstance bot) {

        State state = bot.getState();
        Double limit = state.getPosition().nextTarget();
        Double quantity = state.getPosition().nextTargetQuantityToSell();

        log.debug("place sell limit order for "+state.getSymbol()+". limit= " + df(limit) + ", quantity: " + df(quantity) + bot.profileId());

        if ( state.getPosition() != null && state.getOpenSellOrder() == null ) {

            String baseAsset = state.getSignal().getBaseAsset();

            return accountService.getAvailableBalance(bot.getExchangeAccount(), baseAsset)
                    .flatMap(balance -> {
                        if ( balance == 0.0 ) {
                            log.warn("balance is 0 , waiting a second and rechecking balance");
                            return Mono.delay(Duration.ofSeconds(1))
                                    .flatMap(d ->
                                            accountService.getAvailableBalance(bot.getExchangeAccount(), baseAsset)
                                    );
                        }
                        return Mono.just(balance);
                    })
                    .doOnNext(balance -> {
                        log.debug("current free " + baseAsset + " balance is " + balance + bot.profileId());
                    })
                    .flatMap(balance ->
                            trader.sellOrderLimit( bot.getExchangeAccount(), state.getSymbol(), limit, Math.min(quantity, balance))
                    )
                    .doOnNext( response -> {
                        log.debug("new Sell Order response: " + response + bot.profileId());
                        state.setOpenSellOrder(response);
                        log.debug("state openSellOrder = " + state.getOpenSellOrder());
                    });
        } else {
            //return error
            return Mono.error(new SellOrderException("no position or sell order exist"));
        }
    }

    public Mono<GetOrderResponse> checkSellOrder(BotInstance bot) {
        State state = bot.getState();
        final NewOrderResponse openSellOrder = state.getOpenSellOrder();
        String openSellOrder_uuid = openSellOrder.getOrderId().toString();
        return trader.getOrder(bot.getExchangeAccount(), state.getSymbol(), openSellOrder_uuid);
    }

    public Mono<CancelResponse> cancelOpenOrders() {

        return BotInstance.fromContext()
                .flatMap(bot -> {

                    State state = bot.getState();
                    log.warn("[cancel open orders] - [" + state.getSymbol() + "] " + "attempt to cancel open orders" + bot.profileId());

                    if ( state.getOpenBuyOrder() != null ) {
                        log.warn("attempt to cancel buy order: " + state.getOpenBuyOrder());
                        return cancelOrder(bot, state.getSymbol(), state.getOpenBuyOrder().getOrderId().toString())
                                .doOnNext(cr -> state.setOpenBuyOrder(null));
                    }
                    if ( state.getOpenSellOrder() != null ) {
                        log.warn("attempt to cancel sell order: " + state.getOpenSellOrder());
                        return cancelOrder(bot, state.getSymbol(), state.getOpenSellOrder().getOrderId().toString())
                                .doOnNext(cr -> state.setOpenSellOrder(null));
                    }
                    return Mono.error(new LogicViolationException("cancel order, no open orders found. bot should always have at list one open sell order" + bot.getProfileId()));
                });
    }

    private Mono<CancelResponse> cancelOrder(BotInstance bot, String symbol, String orderId) {

        return trader.cancel(bot.getExchangeAccount(), symbol, orderId)
                .onErrorResume(error -> {

                    log.error("Error on cancel, "+error.getMessage()+bot.profileId());
                    if ( error instanceof BinanceApiException ) {

                        log.error("Error on cancel, "+((BinanceApiException)error).getError().toString()+bot.profileId());
//								BinanceApiException: 400 Bad Request, APIError(code=-2011, msg=UNKNOWN_ORDER)
//		                        when getting binance api error.  need to check if order is full and resume

                        return trader.getOrder(bot.getExchangeAccount(), symbol, orderId)
                                .map(orderResponse -> {

                                    log.warn("called get order after cancel order failed, response: "+orderResponse+bot.profileId());
                                    if ( orderResponse.getStatus().equals(OrderStatus.FILLED.name()) ||
                                            orderResponse.getStatus().equals(OrderStatus.CANCELED.name())   ) {

                                        log.error("order: "+orderId+", already FILLED or CANCELED, returning mockup cancel response."+bot.profileId());
                                        //order filled return mock response
                                        return CancelResponse.builder().build();
                                    } else {

                                        throw new LogicViolationException("Cancel order return with error: " + error +
                                                ",  get order for order: " + orderId + " not filled:  response: "
                                                + orderResponse + bot.profileId());
                                    }
                                });
                        //check if order is full
                    } else {
                        return Mono.error(error);
                    }
                });
    }

    public Mono<NewOrderResponse> sellMarketPosition(BotInstance bot) {

        State state = bot.getState();
        Position position = state.getPosition();
        final double quantity = position.totalQuantity();
        log.debug("sell position. quantity: " + quantity + bot.botInfo());

        return BotInstance.fromContext()
                .flatMap(c ->
                        accountService.getAvailableBalance(bot.getExchangeAccount(), state.getSignal().getBaseAsset())
                )
                .doOnNext(balance ->
                        log.warn("[" + state.getSymbol() + "] " +
                                "sell position - market sell order, account balance: " + balance + ", quantity to sell: " + quantity + bot.profileId()))

                .flatMap(balance ->
                        trader.sellOrderMarket(bot.getExchangeAccount(), state.getSymbol(), Math.min(quantity, balance))
                )
                .doOnNext(orderResponse -> log.debug("sell position order response: " + orderResponse))
                ;
    }

    public Mono<BotInstance> handleSellTrade(TradeRecord trade) {
        return BotInstance.fromContext()
                .doOnNext(bot -> {
                    bot.getState().setOpenSellOrder(null);
                    Position position = bot.getState().getPosition();
                    position.getSellTrades().add(trade);
                });
    }

    //////////////////
    private Mono<TradeRecord> validateTradeResponse(TradeRecord tradeRecord, final Long orderId, final double buyAmount) {

        if ( tradeRecord.getPrice() == Double.NaN || tradeRecord.getPrice() == 0 ) {

            log.error("trade price is NaN or 0.  trade: " + tradeRecord + "wait one second and try to read trade again...");

            return Mono.delay(Duration.ofSeconds(1))
                    .then(tradeForOrder(orderId))
                    .map(trade -> {
                        log.info("Trade for order " + orderId + ". " + tradeRecord);
                        if ( trade.getPrice() == Double.NaN || trade.getPrice() == 0 ) {

                            log.error("trade price is NaN or 0.  trade: " + tradeRecord + "will attempt to set price and quanity from order result");

                            Ticker ticker = tickerService.getTicker(trade.getSymbol());
                            trade.setPrice(ticker.getLast());
                            trade.setQty(buyAmount / ticker.getAsk());
                        }
                        return trade;
                    });

        } else {
            return Mono.just(tradeRecord);
        }
    }

    //NO I\O
    private TradeRecord consolidateTrades(List<TradeRecord> currTradeList) {

        if ( currTradeList.size() == 1 ) {
            return currTradeList.get(0);
        }

        TradeRecord consolidate = currTradeList.get(0).toBuilder().build();

        TradeRecord sumTrades = currTradeList.stream()
                .reduce(new TradeRecord(0.0, 0.0, 0.0), (TradeRecord t1, TradeRecord t2) ->
                        new TradeRecord(t1.getPrice() + t2.getPrice(),
                                t1.getQty() + t2.getQty(),
                                t1.getCommission() + t2.getCommission()));

        consolidate.setPrice(sumTrades.getPrice() / currTradeList.size());
        consolidate.setCommission(sumTrades.getCommission());
        consolidate.setQty(sumTrades.getQty());
        return consolidate;
    }

    private double checkBuyAmount(BotInstance bot, double buyAmount) {
        String quoteAsset = bot.getState().getSignal().getQuoteAsset();
        //FIXME don't read balance from exchange to save time on the call
        //double balance = binanceAccountService.getBalance(quoteAsset).getBalance();
        double balance = bot.getState().getCurrentAmount();
        if ( balance < buyAmount ) {
            log.error("insufficient funds, Quote Asset: " + quoteAsset + ", balance: " + df(balance) + " < amount: " + df(buyAmount) + bot.profileId());
            log.error("set amount to balance * 0.95" + bot.profileId());
            buyAmount = (balance * 0.95);
        }
        log.debug("but order market, quoteAsset: " + quoteAsset + ",  balance: " + balance + ", amount: " + buyAmount + bot.profileId());
        return buyAmount;
    }

    private double calcBuyAmount(BotInstance bot) {

        double buyAmount = 0.0;
        Position position = bot.getState().getPosition();

        log.debug("#Application amount: " + bot.getDefaultAmount() + bot.profileId());

        if ( position == null ) {
            buyAmount = bot.getDefaultAmount() / 2;
            log.debug("position is null. first buy app amount/2: " + buyAmount + bot.profileId());

        } else {
            if ( position.getBuyTrades().size() > 1 ) {
                log.debug("try to buy after second time" + bot.profileId());
                throw new RuntimeException("try to buy after second time" + bot.profileId());
            }
            //second buy
            buyAmount = bot.getDefaultAmount() - position.totalInvested();
            log.debug("position not null. second buy app amount - totalInvested: " + buyAmount + bot.profileId());
        }
        return buyAmount;
    }

    //	private static final DecimalFormat df = new DecimalFormat("#.#");
    private String df(double d) {
        return Double.toString(d);
    }
}
