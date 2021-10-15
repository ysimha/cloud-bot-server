package ys.cloud.sbot.exchange;

import reactor.core.publisher.Mono;
import ys.cloud.sbot.exchange.binance.model.*;

public interface PublicApi {

    Mono<Ticker24hr[]> getTicker24Hr();

    public Mono<BookTicker[]> getBookTicker();

	public Mono<TickerPrice[]> getPrice();

	public Mono<ExchangeInfo> exchangeInfo();

	public Mono<ChartData[]> chartData(String symbol, String interval);

}