package ys.cloud.sbot.logic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ys.cloud.sbot.api.BotActivation;
import ys.cloud.sbot.api.BotTermination;
import ys.cloud.sbot.exceptions.ResourceNotFoundException;
import ys.cloud.sbot.exceptions.UnsupportedArgumentException;
import ys.cloud.sbot.model.config.BotConfig;
import ys.cloud.sbot.model.config.BotConfigRepository;
import ys.cloud.sbot.model.instance.BotInstance;
import ys.cloud.sbot.model.instance.BotInstanceRepository;
import ys.cloud.sbot.users.profile.ExchangeAccount;
import ys.cloud.sbot.users.profile.UserProfile;

import javax.validation.Valid;
import java.util.Optional;

@Service
@Slf4j
public class APIBotInstanceService {
	
	@Autowired BotInstanceRepository botInstanceRepository;
	@Autowired BotConfigRepository botConfigRepository;
	
	public Mono<BotInstance> botInstance(Mono<UserProfile> profile, Mono<BotActivation> activation) {
		return Mono.zip(profile, activation)
				.doOnNext( params->
				validExchange(params.getT1(),params.getT2()))
				.flatMap(this::initInstance);
	}
	
	public Mono<BotInstance> botInstance(UserProfile profile, BotActivation activation) {
		return botInstance(Mono.just(profile), Mono.just(activation));
	}
	
	private Mono<BotInstance> initInstance(Tuple2<UserProfile, BotActivation> t) {
		return initInstance(t.getT1(),t.getT2());
	}

	private Mono<BotInstance> initInstance(UserProfile userProfile, BotActivation activation) {

		log.debug("init bot instance. user profile: " + userProfile + ", activation: " + activation);

		Optional<ExchangeAccount> exchangeAccount = userProfile.getExchangeAccounts()
				.stream().filter(ea -> ea.getExchange().equals(activation.getExchange())).findFirst();

		if (exchangeAccount.isPresent()) {
			return botConfigRepository.findById(activation.getBotConfigId())
					.switchIfEmpty(Mono.error(new ResourceNotFoundException("bot config for id: " + activation.getBotConfigId() + " not found")))
					.doOnNext(bc -> validBotConfig(userProfile, bc))
					.map(config ->
							BotInstance.builder()
									.defaultAmount(config.getDefaultAmount())
									.defaultStoploss(config.getStoploss())
									.exchangeAccount(exchangeAccount.get())
									.profileId(userProfile.getId())
									.name(activation.getName())
									.loop(activation.isLoop())
									.state(null)
									.build()
					)
					.flatMap(botInstanceRepository::insert);
		} else {
			throw new UnsupportedArgumentException("can not create bot instance for activation parameters: " + activation + ", user profile: " + userProfile);
		}
	}

	public Mono<BotInstance> stopInstance(UserProfile profile, @Valid BotTermination termination) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Mono<BotInstance> removeInstance(UserProfile profile, @Valid BotTermination termination) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean validExchange(UserProfile profile, BotActivation activation) {
		if (profile.getExchangeAccounts().stream().anyMatch(ea-> ea.getExchange().equals(activation.getExchange()))) {
			return true;
		}
		throw new UnsupportedArgumentException("exchange ["+activation.getExchange()+"] not define for user");
	}

	private boolean validBotConfig(UserProfile userProfile, BotConfig botConfig) {
		if (userProfile.getId().equals(botConfig.getProfileId())) {
			return true;
		}
		throw new UnsupportedArgumentException("botConfig profile id not define for user profile id");
	}
}
