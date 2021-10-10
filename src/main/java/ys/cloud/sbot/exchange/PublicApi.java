package ys.cloud.sbot.exchange;

import reactor.core.publisher.Mono;
import ys.cloud.sbot.exchange.binance.model.BookTicker;
import ys.cloud.sbot.exchange.binance.model.ChartData;
import ys.cloud.sbot.exchange.binance.model.ExchangeInfo;
import ys.cloud.sbot.exchange.binance.model.TickerPrice;

import java.util.List;

public interface PublicApi {

	public Mono<BookTicker[]> getBookTicker();

	public Mono<TickerPrice[]> getPrice();

	public Mono<ExchangeInfo> exchangeInfo();

	public Mono<ChartData[]> chartData(String symbol, String interval);

}