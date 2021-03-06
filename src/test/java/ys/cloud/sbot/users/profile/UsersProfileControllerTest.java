package ys.cloud.sbot.users.profile;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.TestHelper;
import ys.cloud.sbot.exchange.AccountPermission;
import ys.cloud.sbot.exchange.AccountService;
import ys.cloud.sbot.exchange.ExHelper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;
import static ys.cloud.sbot.TestHelper.TEST_PASSWORD;
import static ys.cloud.sbot.TestHelper.TEST_USER;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration")
class UsersProfileControllerTest {

    @Autowired
    UserProfileRepository userProfileRepository;
    @Autowired
    TestHelper testHelper;

    @MockBean
    AccountService accountService;

    @LocalServerPort
    int port;
    WebTestClient client;

    @BeforeAll
    public void setup() {

        client = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        testHelper.createUser();
        testHelper.createUserProfile();
    }

    @Test
    void getUserProfile() {
        client.mutate().filter(basicAuthentication("other user", TEST_PASSWORD)).build()
                .get()
                .uri("/profile").exchange()
                .expectStatus().isUnauthorized();

        UserProfile profile = client.mutate().filter(basicAuthentication(TEST_USER, TEST_PASSWORD)).build()
                .get()
                .uri("/profile").exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(UserProfile.class)
                .returnResult().getResponseBody();

        assertEquals(TEST_USER,profile.getId());
    }


    @Test
    void addExAcct() {

        Mono<AccountPermission> permissions = Mono.just(AccountPermission.builder().canTrade(true).build());
        when(accountService.accountPermissions(any())).thenReturn(permissions);

        ExchangeAccount exchangeAccount = ExchangeAccount.builder()
                .exchange("binance").publicKey("public").secret("secret").build();

        client.mutate().filter(basicAuthentication(TEST_USER, TEST_PASSWORD)).build()
                .post().uri("/profile/excattc")
                .body(BodyInserters.fromValue(exchangeAccount))
                .exchange()
                .expectStatus().is2xxSuccessful();

        UserProfile userProfile = userProfileRepository.findById(TEST_USER).block();

        ExchangeAccount addedExchangeAccount = userProfile.getExchangeAccounts().get(0);

        assertNotEquals( exchangeAccount.getSecret(), addedExchangeAccount.getSecret());
        assertEquals(exchangeAccount.getSecret(), ExHelper.get(addedExchangeAccount.getSecret()));
    }

    @Test
    void deleteExAcct() {

        Mono<AccountPermission> permissions = Mono.just(AccountPermission.builder().canTrade(true).build());
        when(accountService.accountPermissions(any())).thenReturn(permissions);

        ExchangeAccount exchangeAccount = ExchangeAccount.builder()
                .exchange("binance").publicKey("public").secret("secret").build();

        client.mutate().filter(basicAuthentication(TEST_USER, TEST_PASSWORD)).build()
                .post().uri("/profile/excattc")
                .body(BodyInserters.fromValue(exchangeAccount))
                .exchange()
                .expectStatus().is2xxSuccessful();

        UserProfile userProfile =  userProfileRepository.findById(TEST_USER).block();

        assertTrue(userProfile.getExchangeAccounts().get(0).getExchange().equals(exchangeAccount.getExchange()));

        client.mutate().filter(basicAuthentication(TEST_USER, TEST_PASSWORD)).build()
                .delete().uri("/profile/excattc/"+exchangeAccount.getExchange())
                .exchange().expectStatus().is2xxSuccessful();

        userProfile =  userProfileRepository.findById(TEST_USER).block();

        assertTrue(userProfile.getExchangeAccounts().isEmpty());
    }
}