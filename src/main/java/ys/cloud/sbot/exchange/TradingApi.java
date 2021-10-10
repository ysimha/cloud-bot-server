package ys.cloud.sbot.exchange;

import java.util.Map;

import reactor.core.publisher.Mono;
import ys.cloud.sbot.exchange.binance.model.CancelResponse;
import ys.cloud.sbot.exchange.binance.model.GetOrderResponse;
import ys.cloud.sbot.exchange.binance.model.NewOrderResponse;

public interface TradingApi {

	Mono<NewOrderResponse> newOrder(String apikey, String apisecret, Map<String, String> params);

	Mono<CancelResponse> cancelOrder(String apikey, String apisecret, Map<String,String> params) ;
	
	Mono<GetOrderResponse> getOrder(String apikey, String apisecret, Map<String,String> params) ;
	
}
