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
import ys.cloud.sbot.users.profile.ExchangeAccount;

@Service
public class BinanceTradingService implements TradingApi{

	@Autowired HttpBinance httpBinance;
	
	@Override
	@BinanceApiLogHandler(weight = 1)
	public Mono<NewOrderResponse> newOrder(ExchangeAccount exchangeAccount, Map<String, String> params) {
		return httpBinance.postResponseJson(
				resolveUrl(HttpBinance.ORDER_URL, exchangeAccount),
				params,
				exchangeAccount.getPublicKey(),
				exchangeAccount.getSecret(),
				NewOrderResponse.class);
	}

	@Override
	@BinanceApiLogHandler(weight = 1)
	public Mono<CancelResponse> cancelOrder(ExchangeAccount exchangeAccount, Map<String, String> params) {
		return httpBinance.deleteResponseJson(
				resolveUrl(HttpBinance.ORDER_URL, exchangeAccount),
				params,
				exchangeAccount.getPublicKey(),
				exchangeAccount.getSecret(),
				CancelResponse.class);
	}

	@Override
	@BinanceApiLogHandler(weight = 1)
	public Mono<GetOrderResponse> getOrder(ExchangeAccount exchangeAccount, Map<String, String> params) {
		return httpBinance.getResponseJson(
				resolveUrl(HttpBinance.ORDER_URL, exchangeAccount),
				params,
				exchangeAccount.getPublicKey(),
				exchangeAccount.getSecret(),
				GetOrderResponse.class);
	}

	private String resolveUrl(String endPoint, ExchangeAccount exchangeAccount) {
		switch (exchangeAccount.getExchange().toUpperCase()) {
			case "BINANCE":
				return HttpBinance.BASE_URL+endPoint;
			case "BINANCE_US":
				return HttpBinance.BASE_URL_US+endPoint;
			default:
				throw new RuntimeException("can't url for: "+exchangeAccount.getExchange());
		}
	}
}
