package ys.cloud.sbot.exchange.binance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.exchange.Interval;
import ys.cloud.sbot.exchange.binance.model.BookTicker;
import ys.cloud.sbot.exchange.binance.model.ChartData;
import ys.cloud.sbot.exchange.binance.model.ExchangeInfo;
import ys.cloud.sbot.exchange.binance.model.TickerPrice;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BinancePublicServiceTest {

    @Autowired
    BinancePublicService binancePublicService;

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