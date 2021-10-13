package ys.cloud.sbot.exchange.binance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ys.cloud.sbot.exchange.Account;
import ys.cloud.sbot.exchange.ExHelper;
import ys.cloud.sbot.exchange.binance.model.BookTicker;
import ys.cloud.sbot.users.profile.ExchangeAccount;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BinanceAccountServiceTest {

    @Autowired BinanceAccountService binanceAccountService;

    @Test
    void getBalances() {

    }

    @Test
    void accountPermission() {
    }

    @Test
    void myTrades() {
    }

//    @Test
    void getAccount() {
        ExHelper.init("_pass");
        String secret = ExHelper.set("------");
        String key = "------";
        ExchangeAccount exchangeAccount = ExchangeAccount.builder().exchange("BINANCE-US").publicKey(key).secret(secret).build();
        Account account = binanceAccountService.getAccount(exchangeAccount).block();
        assertNotNull(account);
        System.out.println(account);
    }
}