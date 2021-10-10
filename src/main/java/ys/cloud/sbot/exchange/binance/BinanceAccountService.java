package ys.cloud.sbot.exchange.binance;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.exchange.*;
import ys.cloud.sbot.exchange.binance.log.BinanceApiLogHandler;
import ys.cloud.sbot.exchange.binance.log.BinanceHitCounter;
import ys.cloud.sbot.exchange.binance.model.Trade;


@Service
public class BinanceAccountService implements AccountApi {

	@Autowired
	HttpBinance httpBinance;

	@Override
	public Flux<Balance> getBalances(String apiKey,String apiSecret) {
		Mono<BinanceAccount> account = binanceAccountInformation(apiKey, apiSecret);

		return account.flatMapMany(res-> Flux.fromIterable(res.getBalances()))
				.filter(b-> {return b.getBalance()>0.0;})
				.map(balance -> Balance.builder()
						.available(balance.getFree())
						.balance(balance.getBalance())
						.id(balance.getAsset())
						.name(balance.getAsset())
						.pending(balance.getLocked())
						.cryptoAddress("n/a")
						.build()
				); 
	}

	@Override
	public Mono<AccountPermission> accountPermission(String apikey, String apisecret) {
		Mono<BinanceAccount> account = binanceAccountInformation(apikey, apisecret);
		
		return account.map(acct-> AccountPermission.builder()
				.canDeposit(acct.getCanDeposit())
				.canTrade(acct.getCanTrade())
				.canWithdraw(acct.getCanTrade())
				.build());
	}

	@BinanceApiLogHandler(weight = 5)
	private Mono<BinanceAccount> binanceAccountInformation(String apikey, String apisecret) {
		return httpBinance.getResponseJson(
				HttpBinance.ACCOUNT_URL,new HashMap<>(),apikey,apisecret,BinanceAccount.class);
	}

	@Override
	@BinanceApiLogHandler(weight = 5)
	public Mono<List<TradeRecord>> myTrades(String apikey, String apisecret, String asset) {
		HashMap<String, String> params = new HashMap<>();
		params.put("symbol", asset);
		params.put("timestamp", Long.valueOf(System.currentTimeMillis()).toString());
		
		return httpBinance.getResponseJson(
				HttpBinance.MY_TRADES_URL,params,apikey,apisecret,Trade[].class)
				.map(trades->
					Arrays.asList(trades).stream().map(
							t-> TradeRecord.builder()
							.commission(t.getCommission())
							.commissionAsset(t.getCommissionAsset())
							.id(t.getId())
							.isBestMatch(t.getIsBestMatch())
							.isBuyer(t.getIsBuyer())
							.isMaker(t.getIsMaker())
							.orderId(t.getOrderId())
							.price(t.getPrice())
							.qty(t.getQty())
							.symbol(t.getSymbol())
							.time(t.getTime())
							.build()).collect(Collectors.toList())
				);
	}

    @Override
    public Mono<Account> getAccount(String apiKey, String apiSecret) {
        return binanceAccountInformation(apiKey,apiSecret)
                .map(binanceAccount->{
                    Account account = new Account();
                    BeanUtils.copyProperties(binanceAccount,account);
                    return account;
                 }
         );
    }
}
