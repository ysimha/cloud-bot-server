package ys.cloud.sbot.users;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import ys.cloud.sbot.exchange.AccountService;
import ys.cloud.sbot.exchange.ExHelper;
import ys.cloud.sbot.users.profile.UserProfile;
import ys.cloud.sbot.users.profile.UserProfileRepository;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {

    public static final String TEST_USER = "test-user@mail.com";
    public static final String TEST_PASSWORD = "test-password";

    @Autowired UserProfileRepository userProfileRepository;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @LocalServerPort
    int port;
    WebTestClient client;

    @BeforeAll
    public void setup() {

        client = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        userRepository.insert(User.builder().username(TEST_USER)
                .password(passwordEncoder.encode(TEST_PASSWORD)).email(TEST_USER)
                .roles(Arrays.asList("ROLE_USER")).build()).block();
    }

    @Test
    void current() {

        client.mutate().filter(basicAuthentication("other-user", TEST_PASSWORD)).build()
                .get()
                .uri("/auth/user").exchange()
                .expectStatus().isUnauthorized();

        client.mutate().filter(basicAuthentication(TEST_USER, "other-password")).build()
                .get()
                .uri("/auth/user").exchange()
                .expectStatus().isUnauthorized();

        client.mutate().filter(basicAuthentication(TEST_USER, TEST_PASSWORD)).build()
                .get()
                .uri("/auth/user").exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody().json("{\n" +
                        "    \"username\": \""+TEST_USER+"\",\n" +
                        "    \"email\": \""+TEST_USER+"\",\n" +
                        "    \"roles\": [\n" +
                        "        \"ROLE_USER\"\n" +
                        "    ]\n" +
                        "}");
    }

    @Test
    void logout() {
        client.mutate().filter(basicAuthentication(TEST_USER, TEST_PASSWORD)).build()
                .get().uri("/auth/logout").exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void register() {
        final String NEW_USER = "test-user2@mail.com";
        final String NEW_PASSWORD = "test-password2";
        Registration registration = Registration.builder().username(NEW_USER).password(NEW_PASSWORD).build();
        client.post().uri("/auth/register")
                .body(BodyInserters.fromValue(registration))
                .exchange().expectStatus()
                .is2xxSuccessful();

        UserProfile userProfile = userProfileRepository.findById(NEW_USER).block();
        assertEquals(NEW_USER,userProfile.getId());

        User user = userRepository.findByUsername(NEW_USER).block();
        assertEquals(NEW_USER,user.getEmail());
    }

    @Test
    public void register_authenticated() {

        Registration registration = Registration.builder().username(TEST_USER).password(TEST_PASSWORD).build();

        client.mutate().filter(basicAuthentication(TEST_USER, TEST_PASSWORD)).build()
                .post().uri("/auth/register")
                .body(BodyInserters.fromValue(registration))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);

    }

}