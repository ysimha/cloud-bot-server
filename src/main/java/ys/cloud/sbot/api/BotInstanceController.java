package ys.cloud.sbot.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.exceptions.ResourceNotFoundException;
import ys.cloud.sbot.logic.APIBotInstanceService;
import ys.cloud.sbot.model.instance.BotInstance;
import ys.cloud.sbot.model.instance.BotInstanceRepository;
import ys.cloud.sbot.users.UsersBase;
import ys.cloud.sbot.users.profile.UserProfileRepository;

import javax.validation.Valid;

@RestController
@RequestMapping("/bot/instance")
public class BotInstanceController extends UsersBase {
	
    @Autowired UserProfileRepository userProfileRepository;
    @Autowired APIBotInstanceService botInstanceService;
	@Autowired BotInstanceRepository botInstanceRepository;

	@GetMapping()
	public Flux<BotInstance> get(@AuthenticationPrincipal UsernamePasswordAuthenticationToken principal){
		return 	userProfileRepository.findById(getUserName(principal))
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("profile not found")))
				.flatMapMany(profile-> botInstanceRepository.findByProfileId(profile.getId()))
				.doOnNext(botInstance -> botInstance.getExchangeAccount().setSecret("...."));
	}

	@GetMapping("/byid/{id}")
	public Mono<BotInstance> getById( @AuthenticationPrincipal UsernamePasswordAuthenticationToken principal ,@PathVariable  String id){
		return 	get(principal)
				.filter(botInstance -> botInstance.getId().equals(id))
				.next();
	}

	//start instance
	@PostMapping
	public Mono<BotInstance> start( //FIXME validate one bot is running
		@AuthenticationPrincipal UsernamePasswordAuthenticationToken principal , @RequestBody @Valid  BotActivation activation){
		
		return 	userProfileRepository.findById(getUserName(principal))
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("profile not found")))
				.flatMap(profile-> botInstanceService.botInstance(profile, activation));

	}

	//FIXME
	//add start with bot id
	@DeleteMapping("/{id}/{sell}")
	public Mono<BotInstance> stop(
		@AuthenticationPrincipal UsernamePasswordAuthenticationToken principal ,
		@PathVariable String id , @PathVariable Boolean sell){
		
		return 	userProfileRepository.findById(getUserName(principal))
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("profile not found")))
				.flatMap(profile-> botInstanceService.stopInstance(profile, new BotTermination(id,sell)));

	}

	//add instance
	
	//delete instance

	//stop and sell
	
	//start and buy specific symbol
	
	//start and buy limit specific symbol
	
//	private  Mono<Boolean> valideExchange(UserProfile up, BotActivation ba) { 
//		return exchangeAccountRepository.findByProfileId(up.getId()).any(ea->ea.getExchange().equals(ba.getExchage()));
//	}
}
