package ys.cloud.sbot.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.exceptions.ResourceNotFoundException;
import ys.cloud.sbot.exceptions.UnsupportedArgumentException;
import ys.cloud.sbot.model.config.BotConfig;
import ys.cloud.sbot.model.config.BotConfigRepository;
import ys.cloud.sbot.users.UsersBase;
import ys.cloud.sbot.users.profile.UserProfileRepository;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/bot/config")
public class BotConfigController extends UsersBase {

    @Autowired UserProfileRepository userProfileRepository;
    @Autowired BotConfigRepository botConfigRepository;
//	@Autowired ReactiveMongoTemplate mongoTemplate;


	@GetMapping("")
	public Mono<BotConfig> get(@AuthenticationPrincipal UsernamePasswordAuthenticationToken principal){
			return 	userProfileRepository.findById(getUserName(principal))
					.switchIfEmpty(Mono.error(new ResourceNotFoundException("profile not found")))
					.flatMapMany(profile-> botConfigRepository.findByProfileId(profile.getId()))
					.switchIfEmpty(Mono.just(new BotConfig()))
					//FIXME for now support only one bot config
					.elementAt(0)
					;
	}

	@GetMapping("/all")
	public Flux<BotConfig> getAll(@AuthenticationPrincipal UsernamePasswordAuthenticationToken principal){
		return 	userProfileRepository.findById(getUserName(principal))
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("profile not found")))
				.flatMapMany(profile-> botConfigRepository.findByProfileId(profile.getId()));
	}

	@PutMapping("")
	public Mono<BotConfig> update(@AuthenticationPrincipal UsernamePasswordAuthenticationToken principal,
			@RequestBody @Valid BotConfig botConfig) {

		return userProfileRepository.findById(getUserName(principal))
//			.log()
			.switchIfEmpty(Mono.error(new ResourceNotFoundException("profile not found")))
			.map(profile->{
				if (botConfig.getStoploss()==null || botConfig.getStoploss() <= 0.0) {
					throw new UnsupportedArgumentException("stop loss must be positive number");
				}
				if (botConfig.getDefaultAmount()==null || botConfig.getDefaultAmount()<=0.0) {
					throw new UnsupportedArgumentException("default amount must be positive number");
				}
				botConfig.setProfileId(profile.getId());
				return botConfig;
			})
//				// FIXME for now support only one bot config
			.flatMap(
				bc-> botConfigRepository.deleteByProfileId(bc.getProfileId()).last())
				//mongoTemplate.remove(Query.query(Criteria.where("profileId").is(bc.getProfileId())),BotConfig.class ))
			//TODO FIXME patch 'botConfigRepository.deleteByProfileId' throws error when nothing to delete.
			.doOnError(err->{
				log.error("Error update bot config: "+err);
			})
			.onErrorReturn(BotConfig.builder().build())
			.flatMap(x->
				botConfigRepository.save(botConfig)
			);
	}
}
