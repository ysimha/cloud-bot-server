package ys.cloud.sbot.users.profile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.PrivateMethod;
import ys.cloud.sbot.exceptions.ResourceNotFoundException;
import ys.cloud.sbot.exchange.AccountPermission;
import ys.cloud.sbot.exchange.AccountService;
import ys.cloud.sbot.exchange.ExHelper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest()
public class UserProfileServiceTest /*extends ApiKeysTest*/{

	@BeforeAll
	static void init(){
		ExHelper.init("_pass");
	}

	@Mock AccountService accountService;
	@Mock UserProfileRepository userProfileRepository;

	@InjectMocks
	UserProfileService userProfileService ;

	@BeforeEach
	public void beforeEach(){
		Mono<AccountPermission> permissions = Mono.just(AccountPermission.builder().canTrade(true).build());
		when(accountService.accountPermissions(any())).thenReturn(permissions);
		when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(invocation ->Mono.just(invocation.getArguments()[0]));
	}

	@Test
	public void testValidateAccount() {

		assertTrue(
				((Mono<Boolean>) PrivateMethod.invoke(
						userProfileService, "validateAccount", ExchangeAccount.builder().build())).block()
		);

		Mono<AccountPermission> permissions =  Mono.just(AccountPermission.builder().canTrade(false).build());
		when(accountService.accountPermissions(any())).thenReturn(permissions);
		assertFalse(
				((Mono<Boolean>) PrivateMethod.invoke(
						userProfileService, "validateAccount", ExchangeAccount.builder().build())).block()
		);
	}

	@Test
	public void testAddExchangeAccount() {

		UserProfile userProfile =  UserProfile.builder().build();
		ExchangeAccount exchangeAccount = ExchangeAccount.builder()
				.exchange("binance").publicKey("public").secret("secret").build();

		UserProfile result = userProfileService.addExchangeAccount(userProfile, exchangeAccount).block();
		assertTrue(result.getExchangeAccounts().contains(exchangeAccount));
	}

	@Test
	public void testDeleteExchangeAccount() {

		UserProfile userProfile =  UserProfile.builder().build();

		String publicKey = "test-public";

		ExchangeAccount exchangeAccount = ExchangeAccount.builder()
				.exchange("binance")
				.publicKey(publicKey)
				.secret("secret")
				.build();

		UserProfile result = userProfileService.addExchangeAccount(userProfile, exchangeAccount).block();
		assertTrue(result.getExchangeAccounts().contains(exchangeAccount));

		userProfileService.deleteExchangeAccount(userProfile, exchangeAccount.getExchange()).block();
		assertTrue(userProfile.getExchangeAccounts().isEmpty());
	}

	@Test
	public void testDeleteMissingExchangeAccount() {

		UserProfile userProfile =  UserProfile.builder().build();
		ExchangeAccount exchangeAccount = ExchangeAccount.builder()
				.exchange("binance")
				.build();

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			userProfileService.deleteExchangeAccount(userProfile, exchangeAccount.getExchange()).block();
		});
	}
}


