package ys.cloud.sbot.exchange.binance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ys.cloud.sbot.exchange.PublicApi;
import ys.cloud.sbot.exchange.Ticker;
import ys.cloud.sbot.exchange.TickerService;
import ys.cloud.sbot.exchange.binance.model.BookTicker;
import ys.cloud.sbot.exchange.binance.model.TickerPrice;

@Service
@Slf4j
public class BinanceTickerService implements TickerService {
	
	final private PublicApi binancePublicAPI;

	private Map<String,Ticker> tickerMap = new ConcurrentHashMap<>();

	@Value("${spring.profiles.active:}")
	private String activeProfile;

	public BinanceTickerService(PublicApi binancePublicAPI) {
		this.binancePublicAPI = binancePublicAPI;
	}

	@PostConstruct
	public void init() {
		loadTickers();
	}

	@Scheduled(fixedRate = 5000)
	public void loadTickers() {
		if ("test".equals(activeProfile))return ;

		Map<String,Ticker> newMap = new ConcurrentHashMap<>();
		
		TickerPrice[] prices = binancePublicAPI.getPrice().block();
		BookTicker[] books = binancePublicAPI.getBookTicker().block() ;

		for (TickerPrice tickerPrice : prices) {
			Double last = tickerPrice.getPrice();
			newMap.put(tickerPrice.getSymbol().trim(), new Ticker(last, last, last));
		}
		
		for (BookTicker bookTicker : books) {
			Ticker ticker = newMap.get(bookTicker.getSymbol());
			if (ticker!=null) {
				ticker.setAsk(bookTicker.getAskPrice());
				ticker.setBid(bookTicker.getBidPrice());
			}
		}
		
		this.tickerMap = newMap;

//		newMap.entrySet().forEach(System.out::println);
		
		if (books.length!= prices.length) {
			log.warn("books and prices size not equal !  prices size: "+prices.length+", books size: "+books.length);
		}
	}

	@Override
	public Ticker getTicker(String market) {
		return tickerMap.get(market);
	}

}
