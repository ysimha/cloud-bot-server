package ys.cloud.sbot.exchange;

import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//@Service
public interface AccountApi {
	
	public Flux<Balance> getBalances(String apikey,String apisecret);

	public Mono<AccountPermission> accountPermission(String apikey, String apisecret);
	
	public Mono<List<TradeRecord>> myTrades(String apikey,String apisecret,String symbol) ;

	public Mono<Account> getAccount(String apikey,String apisecret);
}
