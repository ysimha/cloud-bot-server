package ys.cloud.sbot.model;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OpenOrderRepository extends ReactiveMongoRepository<OpenOrder, String> {
	//FIXME save open order and update
    Flux<OpenOrder> findByBotId(String botId);
	Mono<Void> deleteByBotId(String id);

}
