package ys.cloud.sbot.exchange.binance;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import ys.cloud.sbot.exchange.PublicApi;
import ys.cloud.sbot.exchange.binance.enums.BnTickFields;
import ys.cloud.sbot.exchange.binance.log.BinanceApiLogHandler;
import ys.cloud.sbot.exchange.binance.log.BinanceHitCounter;
import ys.cloud.sbot.exchange.binance.model.*;


@Service
public class BinancePublicService implements PublicApi  {

	@Autowired
	HttpBinance httpBinance;

	@Override
	@BinanceApiLogHandler(weight = 40)
	public Mono<Ticker24hr[]> getTicker24Hr() {
		return httpBinance.getResponseJson(
				HttpBinance.TICKER_24HR,new HashMap<>(),Ticker24hr[].class);
	}

	@Override
	@BinanceApiLogHandler(weight = 2)
	public Mono<BookTicker[]> getBookTicker() {
		return httpBinance.getResponseJson(
				HttpBinance.BOOK_TICKER,new HashMap<>(),BookTicker[].class);
	}

	@Override
	@BinanceApiLogHandler(weight = 2)
	public Mono<TickerPrice[]> getPrice() {
		return httpBinance.getResponseJson(
				HttpBinance.PRICE,new HashMap<>(),TickerPrice[].class);
	}

	@Override
	@BinanceApiLogHandler(weight = 40)
	public Mono<ExchangeInfo> exchangeInfo() {
		return httpBinance.getResponseJson(
				HttpBinance.EXCHANGE_INFO,new HashMap<>(),ExchangeInfo.class);
	}

	@Override
	@BinanceApiLogHandler(weight = 1)
	public Mono<ChartData[]> chartData(String symbol, String interval)  {
		HashMap<String, String> params = new HashMap<>();
		params.put("symbol", symbol);
		params.put("interval", interval);
		return httpBinance.getResponseJson(
				HttpBinance.CANDLESTICK,params,Double[][].class).map(this::mapKline);

	}

	private ChartData[] mapKline(Double[][] klines) {
		ChartData[] arr = new ChartData[klines.length];
		for (int i = 0; i < klines.length; i++) {
			Double[] fields = klines[i];
			arr[i] = ChartData.builder()
						.high(fields[BnTickFields.High.ordinal()])
						.close(fields[BnTickFields.Close.ordinal()])
						.closeTime(fields[BnTickFields.Close_time.ordinal()].longValue())
						.openTime(fields[BnTickFields.Open_time.ordinal()].longValue())
						.low(fields[BnTickFields.Low.ordinal()])
						.numberOfTrades(fields[BnTickFields.Number_of_trades.ordinal()])
						.open(fields[BnTickFields.Open.ordinal()])
						.volume(fields[BnTickFields.Volume.ordinal()])
						.build();

		}
		return arr;
	}
}
