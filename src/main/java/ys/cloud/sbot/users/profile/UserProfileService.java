package ys.cloud.sbot.users.profile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.encryption.ExHelper;
import ys.cloud.sbot.exceptions.APIException;
import ys.cloud.sbot.exceptions.ResourceNotFoundException;
//import ys.cloud.sbot.exchange.AccountService;
//import ys.cloud.sbot.exchange.binance.errors.BinanceApiException;

import java.util.Optional;

@Service
@Slf4j
public class UserProfileService {
	
//	@Autowired AccountService accountService;
	@Autowired UserProfileRepository userProfileRepository;

//	public Mono<UserProfile> deleteExchangeAccount(UserProfile userProfile, String exchange ) {
//
//		Optional<ExchangeAccount> exchangeAccount = userProfile.getExchangeAccounts()
//				.stream().filter(ea->ea.getExchange().equals(exchange)).findFirst();
//
//		if (exchangeAccount.isPresent()) {
//			userProfile.getExchangeAccounts().remove(exchangeAccount.get());
//			return userProfileRepository.save(userProfile);
//
//		}else {
//			log.error("can't find exchange account to delete in user profile "+userProfile+", exchange: "+exchange);
//			return Mono.error(new ResourceNotFoundException("Exchange account not found, "+exchange));
//		}
//	}
//
//	public Mono<UserProfile> addExchangeAccount(UserProfile userProfile,final ExchangeAccount exchangeAccount){
//
//		exchangeAccount.setSecret(ExHelper.set(exchangeAccount.getSecret()));
//
//		return validateAccount(exchangeAccount)
//				.onErrorResume(err->{
//						if (err instanceof WebClientResponseException ) {
//							return Mono.error(new APIException(err.getMessage(),err));
//						}
//						if (err instanceof BinanceApiException) {
//							return Mono.error(new APIException(((BinanceApiException)err).getError().getMsg(),err));
//						}
//						return Mono.error(err);
//					})
//				.flatMap( valid -> {
//					if (valid) {
//						removeExchangeAccount(userProfile,exchangeAccount.getExchange());
//						return Mono.just(userProfile) ;
//					}else {
//						return Mono.error(new APIException("Invalid Account, can not trade"));
//					}}
//				)
//				.flatMap(
//						profile->{
//							profile.getExchangeAccounts().add(exchangeAccount);
//							return userProfileRepository.save(profile);
//
//				});
//	}
//
//	private void removeExchangeAccount(UserProfile userProfile,String exchange) {
//
//		Optional<ExchangeAccount> optOldAccount = userProfile.getExchangeAccounts().stream()
//				.filter(ea -> ea.getExchange().equals(exchange)).findAny();
//
//		if (optOldAccount.isPresent()) {
//			userProfile.getExchangeAccounts().remove(optOldAccount.get());
//		}
//	}
//
//	private Mono<Boolean> validateAccount(ExchangeAccount exchangeAccount){
//		return accountService.accountPermissions(exchangeAccount)
//				.map(ap-> ap.getCanTrade() == true);
//	}
}
