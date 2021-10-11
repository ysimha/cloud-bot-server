package ys.cloud.sbot.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ys.cloud.sbot.api.BotActivation;
import ys.cloud.sbot.model.config.BotConfig;
import ys.cloud.sbot.model.config.BotConfigRepository;
import ys.cloud.sbot.model.instance.BotInstance;
import ys.cloud.sbot.model.instance.BotInstanceRepository;
import ys.cloud.sbot.users.profile.ExchangeAccount;
import ys.cloud.sbot.users.profile.UserProfile;
import ys.cloud.sbot.users.profile.UserProfileRepository;

@Service
public class TestBotInstanceHelper {
	
	@Autowired UserProfileRepository userProfileRepository;
    @Autowired BotConfigRepository botConfigRepository;
	@Autowired APIBotInstanceService botInstanceService;
	@Autowired BotInstanceRepository botInstanceRepository;
    @Autowired PasswordEncoder passwordEncoder;

	public BotInstance createBotInstance(String profileId) {

	    BotConfig botConfig = botConfigRepository.insert(BotConfig.builder().profileId(profileId).costAverage(true)
	    		.defaultAmount(0.01).name("bc05").stoploss(1.5).build()).block();
	    
		ExchangeAccount exchangeAccount = ExchangeAccount.builder()
				.exchange("binance").publicKey("public").secret("secret").build();

	    UserProfile userProfile = UserProfile.builder().id(profileId).build();
	    userProfile.getExchangeAccounts().add(exchangeAccount);
	    
	    userProfile = userProfileRepository.insert(userProfile).block();
	    
		BotActivation activation = BotActivation.builder().botConfigId(botConfig.getId()).exchange(exchangeAccount.getExchange()).build();
		activation.setLoop(true);

		return botInstanceService.botInstance(userProfile, activation).block();
	}
	
	public  void cleanBotInstance(String profileId) {
		userProfileRepository.deleteById(profileId).block();
		botConfigRepository.deleteByProfileId(profileId).blockFirst();
		botInstanceRepository.deleteByProfileId(profileId).blockFirst();
	}

}
