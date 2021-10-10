package ys.cloud.sbot.exchange.binance;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import ys.cloud.sbot.exchange.TradingApi;
import ys.cloud.sbot.exchange.binance.log.BinanceApiLogHandler;
import ys.cloud.sbot.exchange.binance.log.BinanceHitCounter;
import ys.cloud.sbot.exchange.binance.model.CancelResponse;
import ys.cloud.sbot.exchange.binance.model.GetOrderResponse;
import ys.cloud.sbot.exchange.binance.model.NewOrderResponse;

@Service
public class BinanceTradingService implements TradingApi{

	@Autowired HttpBinance httpBinance;
	
	@Override
	@BinanceApiLogHandler(weight = 1)
	public Mono<NewOrderResponse> newOrder(String apikey, String apisecret, Map<String, String> params) {
		return httpBinance.postResponseJson(
					HttpBinance.ORDER_URL,params,apikey,apisecret,NewOrderResponse.class);
	}

	@Override
	@BinanceApiLogHandler(weight = 1)
	public Mono<CancelResponse> cancelOrder(String apikey, String apisecret, Map<String, String> params) {
		return httpBinance.deleteResponseJson(
				HttpBinance.ORDER_URL,params,apikey,apisecret,CancelResponse.class);
	}

	@Override
	@BinanceApiLogHandler(weight = 1)
	public Mono<GetOrderResponse> getOrder(String apikey, String apisecret, Map<String, String> params) {
		return httpBinance.getResponseJson(
				HttpBinance.ORDER_URL,params,apikey,apisecret,GetOrderResponse.class);
	}
}
