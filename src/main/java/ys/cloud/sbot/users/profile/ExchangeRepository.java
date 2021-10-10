package ys.cloud.sbot.users.profile;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;


public interface ExchangeRepository extends ReactiveMongoRepository<Exchange, String> {

}
