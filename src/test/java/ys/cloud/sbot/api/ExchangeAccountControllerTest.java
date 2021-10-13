package ys.cloud.sbot.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;
import ys.cloud.sbot.TestHelper;
import ys.cloud.sbot.users.profile.Exchange;
import ys.cloud.sbot.users.profile.ExchangeRepository;
import ys.cloud.sbot.users.profile.UserProfile;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ExchangeAccountControllerTest {

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
        testHelper.addExchangeAccount();
    }
}