package ys.cloud.sbot.exchange.binance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.exchange.Interval;
import ys.cloud.sbot.exchange.binance.model.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BinancePublicServiceTest {

    @Autowired
    BinancePublicService binancePublicService;

    @Test
    void getTicker24Hr() {
        Ticker24hr[] ticker24hrs = binancePublicService.getTicker24Hr().block();
        assertTrue(ticker24hrs.length > 0);
        assertTrue(Arrays.stream(ticker24hrs).anyMatch(bt->bt.getSymbol().equals("BTCUSDT")));
        Arrays.stream(ticker24hrs).filter(t -> t.getSymbol().endsWith("USDT"))
                .forEach(
                        t-> System.out.println(t.getSymbol()+" -> "+t.getLastPrice())
                );
    }

    @Test
    void getBookTicker() {
        BookTicker[] bookTickers = binancePublicService.getBookTicker().block();
        assertTrue(bookTickers.length > 0);
        assertTrue(Arrays.stream(bookTickers).anyMatch(bt->bt.getSymbol().equals("BTCUSDT")));
    }

    @Test
    void getPrice() {
        TickerPrice[] tickerPrices = binancePublicService.getPrice().block();
        assertTrue(tickerPrices.length > 0);
        assertTrue(Arrays.stream(tickerPrices).anyMatch(tp->tp.getSymbol().equals("BTCUSDT")));
    }

    @Test
    void exchangeInfo() {
        ExchangeInfo exchangeInfo = binancePublicService.exchangeInfo().block();
        assertNotNull(exchangeInfo);
    }

    @Test
    void chartData() {
        ChartData[] data = binancePublicService.chartData("BTCUSDT", Interval._1h.getText()).block();
        assertNotNull(data);
    }
}