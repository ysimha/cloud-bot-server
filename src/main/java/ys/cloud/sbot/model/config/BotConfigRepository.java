package ys.cloud.sbot.model.config;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface BotConfigRepository extends ReactiveMongoRepository<BotConfig, String> {
	
    Flux<BotConfig> findByProfileId(String profileId);
    Flux<BotConfig> deleteByProfileId(String profileId);

}
