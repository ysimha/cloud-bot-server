package ys.cloud.sbot.exchange;

import java.util.Map;

import reactor.core.publisher.Mono;
import ys.cloud.sbot.exchange.binance.model.CancelResponse;
import ys.cloud.sbot.exchange.binance.model.GetOrderResponse;
import ys.cloud.sbot.exchange.binance.model.NewOrderResponse;
import ys.cloud.sbot.users.profile.ExchangeAccount;

public interface TradingApi {

	Mono<NewOrderResponse> newOrder(ExchangeAccount exchangeAccount, Map<String, String> params);

	Mono<CancelResponse> cancelOrder(ExchangeAccount exchangeAccount, Map<String,String> params) ;
	
	Mono<GetOrderResponse> getOrder(ExchangeAccount exchangeAccount, Map<String,String> params) ;
}
