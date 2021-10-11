package ys.cloud.sbot.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.exceptions.ResourceNotFoundException;
import ys.cloud.sbot.exchange.ExcAcctBalance;
import ys.cloud.sbot.exchange.ExchangesSummaryService;
import ys.cloud.sbot.users.UsersBase;
import ys.cloud.sbot.users.profile.UserProfileRepository;

@RestController
@RequestMapping("/exchange/account")
public class ExchangeAccountConttroller extends UsersBase {

    @Autowired
    UserProfileRepository userProfileRepository;

    @Autowired
    ExchangesSummaryService exchangesSummaryService;

    @GetMapping("/{exchange}")
    public Flux<ExcAcctBalance> getExchangeSummary(@AuthenticationPrincipal UsernamePasswordAuthenticationToken principal
            , @PathVariable String exchange){

        return userProfileRepository.findById(getUserName(principal))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("profile not found")))
                .flatMapMany( profile-> exchangesSummaryService.getExchangeSummary(profile,exchange));
    }

}
