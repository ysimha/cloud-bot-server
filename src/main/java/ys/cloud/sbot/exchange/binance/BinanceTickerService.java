package ys.cloud.sbot.exchange.binance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ys.cloud.sbot.exchange.PublicApi;
import ys.cloud.sbot.exchange.Ticker;
import ys.cloud.sbot.exchange.TickerService;
import ys.cloud.sbot.exchange.binance.model.BookTicker;
import ys.cloud.sbot.exchange.binance.model.TickerPrice;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

		for (TickerPrice tickerPrice : prices) {
			Double last = tickerPrice.getPrice();
			newMap.put(tickerPrice.getSymbol().trim(), new Ticker(last, last, last));
		}

		BookTicker[] books = binancePublicAPI.getBookTicker().block() ;

		for (BookTicker bookTicker : books) {
			Ticker ticker = newMap.get(bookTicker.getSymbol());
			if (ticker!=null) {
				ticker.setAsk(bookTicker.getAskPrice());
				ticker.setBid(bookTicker.getBidPrice());
			}
		}
		
		this.tickerMap = newMap;

		if (books.length!= prices.length) {
			log.warn("books and prices size not equal !  prices size: "+prices.length+", books size: "+books.length);
		}
	}

	@Override
	public Ticker getTicker(String market) {
		return tickerMap.get(market);
	}

}
