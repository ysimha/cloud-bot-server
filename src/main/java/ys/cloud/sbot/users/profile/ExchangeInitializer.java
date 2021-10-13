package ys.cloud.sbot.users.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Component
public class ExchangeInitializer {

    @Autowired
    ExchangeRepository exchangeRepository;

    @PostConstruct
    private void init(){
        exchangeRepository.findAll()
            .switchIfEmpty(
                exchangeRepository.saveAll(Arrays.asList(
                        new Exchange[]{
                        Exchange.builder().name("Binance").id("BINANCE").build(),
                        Exchange.builder().name("Binance-US").id("BINANCE-US").build(),
                }))
            ).doOnNext(System.out::println).blockLast();
    }
}
