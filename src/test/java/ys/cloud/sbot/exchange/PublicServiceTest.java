package ys.cloud.sbot.exchange;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.exchange.binance.BinancePublicService;
import ys.cloud.sbot.exchange.binance.model.ChartData;
import ys.cloud.sbot.exchange.binance.model.ExchangeInfo;
import ys.cloud.sbot.users.profile.UserProfileRepository;
import ys.cloud.sbot.users.profile.UserProfileService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class PublicServiceTest {

    @Mock
    BinancePublicService binancePublicService;
    @InjectMocks
    PublicService publicService ;

    @Test
    void chartData() {
        ChartData[] expected = new ChartData[]{ChartData.builder().open(1.0).close(1.5).build()};
        when(binancePublicService.chartData("BTC" , Interval._5m.getText())).thenReturn(Mono.just(expected));
        ChartData[] data = publicService.chartData("binance", "BTC" , Interval._5m.getText()).block();
        assertEquals(expected,data);
    }

    @Test
    void exchangeInfo() {
        ExchangeInfo expected = new ExchangeInfo(); expected.setServerTime(System.currentTimeMillis());
        when(binancePublicService.exchangeInfo()).thenReturn(Mono.just(expected));
        ExchangeInfo data = publicService.exchangeInfo("binance").block();
        assertEquals(expected,data);
    }
}