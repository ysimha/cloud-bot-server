package ys.cloud.sbot.exchange;

import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.users.profile.ExchangeAccount;

//@Service
public interface AccountApi {
	
	Flux<Balance> getBalances(ExchangeAccount exchangeAccount);

	Mono<AccountPermission> accountPermission(ExchangeAccount exchangeAccount);
	
	Mono<List<TradeRecord>> myTrades(ExchangeAccount exchangeAccount, String symbol) ;

	Mono<Account> getAccount(ExchangeAccount exchangeAccount);
}
