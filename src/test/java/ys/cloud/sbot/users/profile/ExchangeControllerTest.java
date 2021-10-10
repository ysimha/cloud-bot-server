package ys.cloud.sbot.users.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ExchangeControllerTest {

    @Autowired
    ExchangeRepository exchangeRepository;
    final Exchange _exchange = Exchange.builder().id("test").name("test-name").build();

    @LocalServerPort int port;
    WebTestClient webClient;

    @BeforeEach
    public void setup() {
        webClient = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void exchanges() {

        exchangeRepository.save(_exchange).block();

        webClient.get().uri("/exchanges/public")
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Exchange[].class)
        ;
    }
}