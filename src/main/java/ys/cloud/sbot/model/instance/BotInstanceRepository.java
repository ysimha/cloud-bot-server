package ys.cloud.sbot.model.instance;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.model.aspect.BotInstanceDBHandler;

public interface BotInstanceRepository extends ReactiveMongoRepository<BotInstance, String> {

    Flux<BotInstance> findByProfileId(String profileId);
//    Flux<BotInstance> findByStateIsNull();
//    Flux<BotInstance> findByStateIsNotNull();
    
    Flux<BotInstance> findByHasOpenOrderTrue();
    Flux<BotInstance> findByHasOpenOrderFalse();
    
    @BotInstanceDBHandler(isSave=true)
    Mono<BotInstance> save(BotInstance botInstance);
	
    @BotInstanceDBHandler(isSave=true)
    Mono<BotInstance> insert(BotInstance botInstance) ;
    
    Flux<BotInstance>  deleteByProfileId(String profileId);

}
