package ys.cloud.sbot.exchange.binance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ys.cloud.sbot.exchange.binance.BinancePublicService;
import ys.cloud.sbot.exchange.binance.model.ExchangeInfo;
import ys.cloud.sbot.exchange.binance.model.Symbol;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BinanceExchangeInfoService {
	
	@Autowired private BinancePublicService binancePublicAPI;
	
	private ExchangeInfo exchangeInfo;
	private Map<String,Symbol> binanceSymbols = new HashMap<String,Symbol>();

	@Value("${spring.profiles.active:}")
	private String activeProfile;

	@PostConstruct
	private void init() {
		loadExchangeInfo();
	}

	public Symbol resolveSymbol(String baseAsset,String quoteAsset) {
		return binanceSymbols.get(baseAsset+quoteAsset);
	}
	
	@Scheduled(fixedRate = 300000) //call it every 5 minutes
	private void loadExchangeInfo() {
		if ("test".equals(activeProfile))return ;

		log.debug("loading binance exchange info");
		
		this.exchangeInfo = binancePublicAPI.exchangeInfo().block();
		
		this.binanceSymbols = this.exchangeInfo.getSymbols()
				.stream().collect(Collectors
						.toMap(Symbol::getSymbol, Function.identity()));
	}
}