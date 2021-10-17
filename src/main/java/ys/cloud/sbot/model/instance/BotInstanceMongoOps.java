package ys.cloud.sbot.model.instance;

import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@Slf4j
public class BotInstanceMongoOps {
	
	@Autowired ReactiveMongoTemplate reactiveMongoTemplate;

	public Mono<UpdateResult> saveState( BotInstance botInstance ){

		log.debug("save state: "+botInstance.getState());
		Query query = Query.query(Criteria.where("id").is(botInstance.getId()));
		Update update = new Update().set("state", botInstance.getState());
		
		return reactiveMongoTemplate.updateFirst(query, update, BotInstance.class)
				.doOnNext(r-> log.debug("save state result: "+r) );
	}
	
	public Flux<String> findStandbyIds(){
		return reactiveMongoTemplate.query(BotInstance.class)
				.distinct("id")
				.matching(Query.query(Criteria.where("method").is(null).andOperator(Criteria.where("state").is(null))))
				.all().cast(String.class);
	}
	
	public Flux<String> findActiveIds(){
		return reactiveMongoTemplate.query(BotInstance.class)
				.distinct("id")
				.matching(Query.query(Criteria.where("method").is(null).andOperator(Criteria.where("state").ne(null))))
				.all().cast(String.class);
	}
	
	public Mono<BotInstance> takeForMethod(String method, String id){

		Date now = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

		Query query = Query.query(	Criteria.where("id").is(id).andOperator(Criteria.where("method").is(null)));
		Update update = new Update();
		update.set("method", method);
//		update.set("lastMethodUpdate", now);
		
		return reactiveMongoTemplate.findAndModify(query, update, BotInstance.class);
	}
	
	public Mono<UpdateResult> releaseFormMethod(String id){
		log.debug("releaseFormMethod: "+id);
		Query query = Query.query(Criteria.where("id").is(id));
		Update update = new Update().set("method", null);

		return reactiveMongoTemplate.updateFirst(query, update, BotInstance.class);
	}

	public Flux<String> findIdsForMaintenance(int seconds){
		Date past = Date.from(LocalDateTime.now().minusSeconds(seconds).atZone(ZoneId.systemDefault()).toInstant());
		return reactiveMongoTemplate.query(BotInstance.class)
				.distinct("id")
				.matching(Query.query(Criteria.where("method").ne(null)
				.andOperator(Criteria.where("lastModifiedDate").lt(past))
				)).all().cast(String.class);

	}
}
