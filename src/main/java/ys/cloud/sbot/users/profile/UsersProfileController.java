package ys.cloud.sbot.users.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.exceptions.ResourceNotFoundException;
import ys.cloud.sbot.users.UsersBase;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/profile")
public class UsersProfileController extends UsersBase {
	
//	@Autowired UserProfileRepository userProfileRepository;
//	@Autowired UserProfileService userProfileService;
//
//	@GetMapping()
//	public Mono<UserProfile> getUserProfile(@AuthenticationPrincipal UsernamePasswordAuthenticationToken principal){
//			return 	userProfileRepository.findById(getUserName(principal))
//				.switchIfEmpty(Mono.error(new ResourceNotFoundException("profile not found")))
//				.doOnNext(p->p.getExchangeAccounts().forEach(e->e.setSecret("...")));
//	}
//
//	@DeleteMapping("excattc/{exchange}")
//	public Mono<UserProfile> deleteExAcct(
//			@AuthenticationPrincipal UsernamePasswordAuthenticationToken principal ,
//			@PathVariable String exchange ) {
//
//		return userProfileRepository.findById(getUserName(principal))
//		.switchIfEmpty(
//				Mono.error(new ResourceNotFoundException("profile not found")))
//		.flatMap(userProfile-> userProfileService.deleteExchangeAccount(userProfile, exchange));
//	}
//
//	@PostMapping("excattc")
//	public Mono<UserProfile> addExAcct(
//			@AuthenticationPrincipal UsernamePasswordAuthenticationToken principal ,
//			@RequestBody @Valid  ExchangeAccount exchangeAccount){
//
//		return userProfileRepository.findById(getUserName(principal))
//				.switchIfEmpty(
//						Mono.error(new ResourceNotFoundException("profile not found")))
//				.flatMap(userProfile-> userProfileService.addExchangeAccount(userProfile, exchangeAccount));
//
//	}
}










