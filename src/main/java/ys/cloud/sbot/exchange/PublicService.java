package ys.cloud.sbot.exchange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.exchange.binance.BinancePublicService;
import ys.cloud.sbot.exchange.binance.model.BookTicker;
import ys.cloud.sbot.exchange.binance.model.ChartData;
import ys.cloud.sbot.exchange.binance.model.ExchangeInfo;
import ys.cloud.sbot.exchange.binance.model.TickerPrice;

@Service
public class PublicService  {
	
	@Autowired BinancePublicService binancePublicService;

	public Mono<ChartData[]> chartData(String exchange,String symbol, String interval){
		return getApiService(exchange).chartData(symbol,interval);
	}
	public Mono<BookTicker[]> getBookTicker(String exchange) {
		return getApiService(exchange).getBookTicker();
	}

	public Mono<TickerPrice[]> getPrice(String exchange) {
		return getApiService(exchange).getPrice();
	}

	public Mono<ExchangeInfo> exchangeInfo(String exchange) {
		return getApiService(exchange).exchangeInfo();
	}

	private PublicApi getApiService(String exchange) {
		switch (exchange.toUpperCase()) {
			case "BINANCE":
			case "BINANCE_US":
				return binancePublicService;
			default:
				throw new RuntimeException(exchange);
		}
	}
}
