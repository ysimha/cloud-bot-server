package ys.cloud.sbot.exchange;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import ys.cloud.sbot.exchange.binance.BinanceTradingService;
import ys.cloud.sbot.exchange.binance.model.CancelResponse;
import ys.cloud.sbot.exchange.binance.model.GetOrderResponse;
import ys.cloud.sbot.exchange.binance.model.NewOrderResponse;
import ys.cloud.sbot.users.profile.ExchangeAccount;

@Service
public class TradingService {
	
	@Autowired BinanceTradingService binanceTradingService;
	
	public Mono<NewOrderResponse> newOrder(ExchangeAccount exchangeAccount, Map<String, String> params) {
		return getApiService(exchangeAccount).newOrder( exchangeAccount, params) ;
	}

	public Mono<CancelResponse> cancelOrder(ExchangeAccount exchangeAccount, Map<String,String> params) {
		return getApiService(exchangeAccount).cancelOrder( exchangeAccount, params) ;
	}
	
	public Mono<GetOrderResponse> getOrder(ExchangeAccount exchangeAccount,Map<String,String> params) {
		return getApiService(exchangeAccount).getOrder( exchangeAccount, params) ;
	}

	private TradingApi getApiService(ExchangeAccount exchangeAccount) {
		switch (exchangeAccount.getExchange().toUpperCase()) {
			case "BINANCE":
			case "BINANCE_US":
				return binanceTradingService;
		default:
			throw new RuntimeException(exchangeAccount.getExchange());
		}
	}


}
