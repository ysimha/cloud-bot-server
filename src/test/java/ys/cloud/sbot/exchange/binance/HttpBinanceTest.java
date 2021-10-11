package ys.cloud.sbot.exchange.binance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ys.cloud.sbot.exchange.binance.model.ExchangeInfo;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HttpBinanceTest {

    @Autowired HttpBinance httpBinance;

    @Test
    void getResponseJson() {
        //test increased buffer size
        ExchangeInfo result = httpBinance
                .getResponseJson(HttpBinance.EXCHANGE_INFO,new HashMap<>(),ExchangeInfo.class).block();
        assertNotNull(result);
    }
}