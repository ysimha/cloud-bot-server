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
import ys.cloud.sbot.users.profile.ExchangeAccount;


@Service
public class BinanceAccountService implements AccountApi {

	@Autowired
	HttpBinance httpBinance;

	@Override
	public Flux<Balance> getBalances(ExchangeAccount exchangeAccount) {
		Mono<BinanceAccount> account = binanceAccountInformation(exchangeAccount);

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
	public Mono<AccountPermission> accountPermission(ExchangeAccount exchangeAccount) {
		Mono<BinanceAccount> account = binanceAccountInformation(exchangeAccount);
		
		return account.map(acct-> AccountPermission.builder()
				.canDeposit(acct.getCanDeposit())
				.canTrade(acct.getCanTrade())
				.canWithdraw(acct.getCanTrade())
				.build());
	}

	@Override
	public Mono<Account> getAccount(ExchangeAccount exchangeAccount) {
		return binanceAccountInformation(exchangeAccount)
				.map(binanceAccount->{
							Account account = new Account();
							BeanUtils.copyProperties(binanceAccount,account);
							return account;
						}
				);
	}

	@BinanceApiLogHandler(weight = 5)
	private Mono<BinanceAccount> binanceAccountInformation(ExchangeAccount exchangeAccount) {
		return httpBinance.getResponseJson(
				resolveUrl(HttpBinance.ACCOUNT_URL,exchangeAccount),new HashMap<>(),exchangeAccount.getPublicKey(),exchangeAccount.getSecret(),BinanceAccount.class);
	}

	@Override
	@BinanceApiLogHandler(weight = 5)
	public Mono<List<TradeRecord>> myTrades(ExchangeAccount exchangeAccount, String asset) {
		HashMap<String, String> params = new HashMap<>();
		params.put("symbol", asset);
		params.put("timestamp", Long.valueOf(System.currentTimeMillis()).toString());
		
		return httpBinance.getResponseJson(
				resolveUrl(HttpBinance.MY_TRADES_URL,exchangeAccount),params,exchangeAccount.getPublicKey(),exchangeAccount.getSecret(),Trade[].class)
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

	private String resolveUrl(String endPoint, ExchangeAccount exchangeAccount) {
		switch (exchangeAccount.getExchange().toUpperCase()) {
			case "BINANCE":
				return HttpBinance.BASE_URL+endPoint;
			case "BINANCE-US":
				return HttpBinance.BASE_URL_US+endPoint;
			default:
				throw new RuntimeException("can't url for: "+exchangeAccount.getExchange());
		}
	}
}
