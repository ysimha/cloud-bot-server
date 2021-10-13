package ys.cloud.sbot.exchange.binance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ys.cloud.sbot.exchange.Account;
import ys.cloud.sbot.exchange.ExHelper;
import ys.cloud.sbot.exchange.binance.model.ExchangeInfo;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HttpBinanceTest {

    @Autowired HttpBinance httpBinance;

    @Test
    void testGetResponseJson() {
        //test increased buffer size
        ExchangeInfo result = httpBinance
                .getResponseJson(HttpBinance.EXCHANGE_INFO,new HashMap<>(),ExchangeInfo.class).block();
        assertNotNull(result);
    }

//    @Test
    void testGetResponseJsonPrivate() {
        ExHelper.init("_pass");
//        String url, Map<String, String> params , String key, String secret, Class<T> klass
        String url = "https://api.binance.us/api/v3/account";
        Map<String,String> params = new HashMap();
        String secret = ExHelper.set("......");
        String apiKey = "......";

        Account account = httpBinance.getResponseJson(url,params,apiKey,secret,Account.class).block();
        assertNotNull(account);
    }

    @Test
    void postResponseJson() {
    }

    @Test
    void deleteResponseJson() {
    }

    @Test
    void deleteResponseSpec() {
    }

    @Test
    void encode() {
        ExHelper.init("_pass");
       String queryString = "symbol=LTCBTC&side=BUY&type=LIMIT&timeInForce=GTC&quantity=1&price=0.1&recvWindow=5000&timestamp=1499827319559";
       String publicKey = "vmPUZE6mv9SD5VNHk4HlWFsOr6aKE2zvsw0MuIgwCIPy6utIco14y7Ju91duEh8A";
       String secretKey = "NhqPtmdSJYdKjVHjA7PZj4Mge3R5YNiP1e3UZjInClVN65XAbvqqM6A7H5fATj0j";
       String expectedResult = "c8db56825ae71d6d79447849e617115f4a920fa2acdcab2b053c4b2838bd6b71";

       secretKey = ExHelper.set(secretKey);
       String result = httpBinance.encode(secretKey,queryString);

       assertEquals(expectedResult,result);
    }
}