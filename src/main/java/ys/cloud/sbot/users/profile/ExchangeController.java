package ys.cloud.sbot.users.profile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@Slf4j
@RestController
@RequestMapping("/exchanges")
public class ExchangeController {

    private final ExchangeRepository exchangeRepository;

    public ExchangeController(ExchangeRepository exchangeRepository) {
        this.exchangeRepository = exchangeRepository;
    }

    @GetMapping(value = "/public")
    public Flux<Exchange> exchanges(){
        return exchangeRepository.findAll();
    }

//    @PostMapping(value = "/admin")
//    public Flux<Exchange> initialize(@RequestBody Flux<Exchange> exchangeFlux){
//        return exchangeRepository
//                .deleteAll()
//                .thenMany(  exchangeRepository.insert(exchangeFlux) ).log();
//    }
}
