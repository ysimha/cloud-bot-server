package ys.cloud.sbot.exchange;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ys.cloud.sbot.exchange.binance.BinancePriceDataService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class PriceDataServiceTest {

    @Mock
    BinancePriceDataService binancePriceData;
    @InjectMocks
    PriceDataService priceDataService ;

    @Test
    void getPriceData() {
        PriceData expected = PriceData.builder().symbol("ETH").price(4500.00).build();
        when(binancePriceData.getPriceData("ETH")).thenReturn(expected);
        PriceData priceData = priceDataService.getPriceData("ETH","binance");
        assertEquals(expected,priceData);
    }

    @Test
    void getBtcValue() {
        double expected = 50000.00;
        when(binancePriceData.getBtcValue()).thenReturn(expected);
        double btcPrice = priceDataService.getBtcValue("binance");
        assertEquals(expected,btcPrice);
    }

    @Test
    void getDollar() {
        PriceData dollar = PriceData.builder().symbol("USD").perc24Change(0.0).volume(0.0)
                .price(1.0).name("USD").dateTime(LocalDateTime.of(1999,1,1,0,0)).build();

        PriceData priceData = priceDataService.getDollar();

        assertEquals(dollar,priceData);
    }

}