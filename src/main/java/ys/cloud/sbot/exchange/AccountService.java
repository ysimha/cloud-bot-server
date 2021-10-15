package ys.cloud.sbot.exchange;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.exchange.binance.BinanceAccountService;
import ys.cloud.sbot.users.profile.ExchangeAccount;

@Service
public class AccountService {

	@Autowired private BinanceAccountService binanceAccountService;
	
	public Mono<Double> getAvailableBalance(ExchangeAccount exchangeAccount , String currency){
		return getBalances(exchangeAccount)
				.filter(b-> b.getId().equals(currency)).next()
				.switchIfEmpty(Mono.just(Balance.builder().available(0.0).build()))
				.map(b->b.getAvailable());
	}

	public Mono<Account> getAccount(ExchangeAccount exchangeAccount){
		return getApiService(exchangeAccount).getAccount(exchangeAccount);
	}

	public Flux<Balance> getBalances(ExchangeAccount exchangeAccount){
		return getApiService(exchangeAccount).getBalances(exchangeAccount);
	}

	public Mono<AccountPermission> accountPermissions(ExchangeAccount exchangeAccount){
		return getApiService(exchangeAccount).accountPermission(exchangeAccount);
	}
	
	public Mono<List<TradeRecord>> myTrades(ExchangeAccount exchangeAccount,String symbol) {
		return getApiService(exchangeAccount).myTrades(exchangeAccount, symbol);
	}
	
	private AccountApi getApiService(ExchangeAccount exchangeAccount) {
		switch (exchangeAccount.getExchange().toUpperCase()) {
			case "BINANCE":
			case "BINANCE_US":
				return binanceAccountService;
		default:
			throw new RuntimeException(exchangeAccount.getExchange());
		}
	}
}
