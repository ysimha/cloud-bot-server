package ys.cloud.sbot.model.history;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TradingSessionRecordRepository extends ReactiveMongoRepository<TradingSessionRecord, String> {

    Flux<TradingSessionRecord> findByProfileId(String profileId);

}

