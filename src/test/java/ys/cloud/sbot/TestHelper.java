package ys.cloud.sbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ys.cloud.sbot.exchange.ExHelper;
import ys.cloud.sbot.users.User;
import ys.cloud.sbot.users.UserRepository;
import ys.cloud.sbot.users.profile.ExchangeAccount;
import ys.cloud.sbot.users.profile.UserProfile;
import ys.cloud.sbot.users.profile.UserProfileRepository;

import java.util.Arrays;

@Service
public class TestHelper {


    static {
        ExHelper.init("_pass");
    }

    public static final String TEST_USER = "test-user@mail.com";
    public static final String TEST_PASSWORD = "test-password";
    private static final ExchangeAccount TEST_EXCHANGE = ExchangeAccount.builder()
            .exchange("binance").publicKey("public").secret("secret").build();

    @Autowired
    UserProfileRepository userProfileRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public UserProfile createUserProfile(String _id){
        String id = _id == null ? TEST_USER : _id;
        return  userProfileRepository.insert(UserProfile.builder().id(id).build()).block();
    }
    public User createUser(String _id, String _password){
        String id = _id == null ? TEST_USER : _id;
        String password = _password == null ? TEST_PASSWORD : _password;

        return userRepository.insert(User.builder().username(TEST_USER)
                .password(passwordEncoder.encode(TEST_PASSWORD)).email(TEST_USER)
                .roles(Arrays.asList("ROLE_USER")).build()).block();

    }

    public User createUser() {
        return createUser(null,null);
    }

    public UserProfile createUserProfile() {
        return createUserProfile(null);
    }

    public UserProfile addExchangeAccount(String profileId, ExchangeAccount _exchangeAccount){
        String id = profileId == null ? TEST_USER : profileId;
        ExchangeAccount exchangeAccount = _exchangeAccount == null ? TEST_EXCHANGE : _exchangeAccount;
        return userProfileRepository.findById(id)
                .doOnNext(userProfile -> userProfile.getExchangeAccounts().add(exchangeAccount))
                .doOnNext(userProfileRepository::save).block();

    }

    public UserProfile addExchangeAccount() {
        return addExchangeAccount(null, null);
    }
}
