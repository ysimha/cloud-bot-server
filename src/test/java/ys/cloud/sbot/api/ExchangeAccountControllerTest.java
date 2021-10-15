package ys.cloud.sbot.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ys.cloud.sbot.TestHelper;
import ys.cloud.sbot.exchange.ExcAcctBalance;
import ys.cloud.sbot.exchange.ExchangesSummaryService;
import ys.cloud.sbot.users.profile.Exchange;
import ys.cloud.sbot.users.profile.ExchangeRepository;
import ys.cloud.sbot.users.profile.UserProfile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;
import static ys.cloud.sbot.TestHelper.TEST_PASSWORD;
import static ys.cloud.sbot.TestHelper.TEST_USER;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ExchangeAccountControllerTest {

    @MockBean
    ExchangesSummaryService exchangesSummaryService;

    @Autowired
    TestHelper testHelper;

    @LocalServerPort
    int port;
    WebTestClient webClient;

    @BeforeEach
    public void setup() {
        webClient = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void getExchangeSummary() {
        testHelper.createUser();
        UserProfile userProfile = testHelper.createUserProfile();
        ExcAcctBalance excAcctBalance = ExcAcctBalance.builder().name("test-acct-balance").balance(99.98).build();
        Flux<ExcAcctBalance> excAcctBalanceFlux = Flux.just(excAcctBalance);
        String exchange = TestHelper.TEST_EXCHANGE.getExchange();
        when(exchangesSummaryService.getExchangeSummary(userProfile, exchange))
                .thenReturn(excAcctBalanceFlux);

        ExcAcctBalance[] excAcctBalances = webClient.mutate().filter(basicAuthentication(TEST_USER, TEST_PASSWORD)).build()
                .get()
                .uri("/exchange/account/"+exchange).exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ExcAcctBalance[].class)
                .returnResult().getResponseBody();

        assertEquals(excAcctBalance,excAcctBalances[0]);

    }
}