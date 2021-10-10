package ys.cloud.sbot.exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ys.cloud.sbot.exceptions.ResourceNotFoundException;
import ys.cloud.sbot.users.profile.ExchangeAccount;
import ys.cloud.sbot.users.profile.UserProfile;

import java.util.Optional;

@Slf4j
@Service
public class ExchangesSummaryService {

    final private AccountService accountService;
    final private PriceDataService priceDataService;

//    final private TickerServiceAdaptor tickerServiceAdaptor;

    //    public ExchangesSummaryService(AccountService accountService, TickerServiceAdaptor tickerServiceAdaptor) {
    public ExchangesSummaryService(AccountService accountService,
                                   /*TickerServiceAdaptor tickerServiceAdaptor, */
                                   PriceDataService priceDataService) {

        this.accountService = accountService;
//        this.tickerServiceAdaptor = tickerServiceAdaptor;
        this.priceDataService = priceDataService;
    }

    public Flux<ExcAcctBalance> getExchangeSummary(UserProfile userProfile, final String exchange) {

        Optional<ExchangeAccount> exchangeAccountOptional =
                userProfile.getExchangeAccounts().stream().filter(ea -> ea.getExchange().equalsIgnoreCase(exchange)).findFirst();

        if ( !exchangeAccountOptional.isPresent() ) {
            String errorMsg = "exchange " + exchange + " not found for user profile " + userProfile.getId();
            log.error(errorMsg);
            return Flux.error(new ResourceNotFoundException(errorMsg));
        }

        Flux<Balance> balanceFlux = accountService.getBalances(exchangeAccountOptional.get());

        return balanceFlux.map(balance -> {
                ExcAcctBalance excAcctBalance = ExcAcctBalance.builder()
                        .id(balance.getId())
                        .balance(balance.getBalance())
                        .available(balance.getAvailable())
                        .pending(balance.getPending())
                        .cryptoAddress(balance.getCryptoAddress())
                        .build();

                PriceData priceData =  priceDataService.getPriceData(balance.getId(),exchange);
                if ( priceData != null ) {
                    excAcctBalance.setFiatValue(priceData.getPrice());
                    excAcctBalance.setBtcValue(priceData.getPrice() / priceDataService.getBtcValue(exchange));
                    excAcctBalance.setVolume(priceData.getVolume());
                    excAcctBalance.setPerc24Change(priceData.getPerc24Change());
                    excAcctBalance.setName(priceData.getName());
                }
                return excAcctBalance;
            }
        );
    }
}
