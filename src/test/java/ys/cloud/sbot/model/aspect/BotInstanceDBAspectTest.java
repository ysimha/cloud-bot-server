package ys.cloud.sbot.model.aspect;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ys.cloud.sbot.api.BotActivation;
import ys.cloud.sbot.exchange.binance.model.NewOrderResponse;
import ys.cloud.sbot.logic.APIBotInstanceService;
import ys.cloud.sbot.model.State;
import ys.cloud.sbot.model.config.BotConfig;
import ys.cloud.sbot.model.config.BotConfigRepository;
import ys.cloud.sbot.model.instance.BotInstance;
import ys.cloud.sbot.model.instance.BotInstanceRepository;
import ys.cloud.sbot.users.profile.ExchangeAccount;
import ys.cloud.sbot.users.profile.UserProfile;
import ys.cloud.sbot.users.profile.UserProfileRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BotInstanceDBAspectTest {
    private static final String USER_NAME0001 = "USER_NAME0001";
    @Autowired
    UserProfileRepository userProfileRepository;
    @Autowired
    BotConfigRepository botConfigRepository;
    @Autowired
    APIBotInstanceService botInstanceService;
    @Autowired
    BotInstanceRepository botInstanceRepository;

    @Test
    public void testProcessBotInstance() {
        ExchangeAccount exchangeAccount = ExchangeAccount.builder()
                .exchange("binance").publicKey("public").secret("secret").build();
        UserProfile userProfile = UserProfile.builder().id(USER_NAME0001).build();
        userProfile.getExchangeAccounts().add(exchangeAccount);
        userProfile = userProfileRepository.insert(userProfile).block();

        BotConfig botConfig = botConfigRepository.insert(BotConfig.builder().profileId(userProfile.getId()).costAverage(true)
                .defaultAmount(0.01).name("bc01").stoploss(4.0).build()).block();

        BotActivation activation = BotActivation.builder().botConfigId(botConfig.getId()).exchange(exchangeAccount.getExchange()).build();

        BotInstance botInstance = null;
        try {
            botInstance = botInstanceService.botInstance(userProfile, activation).block();
            assertNotNull(botInstance);
            assertFalse(botInstance.isHasOpenOrder());

            botInstance = botInstanceRepository.findByHasOpenOrderFalse().blockFirst();
            assertNotNull(botInstance);

            Flux<BotInstance> withOrder  = botInstanceRepository.findByHasOpenOrderTrue();
            StepVerifier.create(withOrder)
                    .expectNextCount(0)
                    .expectComplete()
                    .verify();

            botInstance.setState(new State());
            botInstance.getState().setOpenBuyOrder(new NewOrderResponse());
            botInstance = botInstanceRepository.save(botInstance).block();
            assertTrue(botInstance.isHasOpenOrder());

            botInstance = botInstanceRepository.findByHasOpenOrderTrue().blockFirst();
            assertNotNull(botInstance);

            botInstance.getState().setOpenBuyOrder(null);
            botInstance = botInstanceRepository.save(botInstance).block();
            assertFalse(botInstance.isHasOpenOrder());
        } catch (Exception e){
            e.printStackTrace();
        }finally  {
            botInstanceRepository.delete(botInstance).block();
            botConfigRepository.delete(botConfig).block();
            userProfileRepository.deleteById(USER_NAME0001).block();
        }
    }
}