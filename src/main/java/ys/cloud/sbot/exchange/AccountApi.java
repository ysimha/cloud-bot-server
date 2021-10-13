package ys.cloud.sbot.exchange;

import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.users.profile.ExchangeAccount;

//@Service
public interface AccountApi {
	
	public Flux<Balance> getBalances(ExchangeAccount exchangeAccount);

	public Mono<AccountPermission> accountPermission(ExchangeAccount exchangeAccount);
	
	public Mono<List<TradeRecord>> myTrades(ExchangeAccount exchangeAccount, String symbol) ;

	public Mono<Account> getAccount(ExchangeAccount exchangeAccount);
}
