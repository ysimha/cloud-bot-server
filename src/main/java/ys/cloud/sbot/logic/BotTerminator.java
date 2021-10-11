package ys.cloud.sbot.logic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ys.cloud.sbot.model.history.TradingSessionRecord;
import ys.cloud.sbot.model.instance.BotInstance;
import ys.cloud.sbot.model.instance.BotInstanceMongoOps;
import ys.cloud.sbot.model.instance.BotInstanceRepository;

import java.util.function.Consumer;

@Service
@Slf4j
public class BotTerminator {

    @Autowired  PositionRecorder positionRecorder;
    @Autowired  BotInstanceMongoOps botInstanceMongoOps;
    @Autowired  BotInstanceRepository botInstanceRepository;

    public Consumer<? super BotInstance> endSession() {

        return botInstance -> {

                    recordAndEndSession()

                    .then( BotInstance.fromContext().doOnNext(botInstanceRepository::save) )
                    //release method always
                    .doFinally( event-> botInstanceMongoOps.releaseFormMethod(botInstance.getId()).subscribe())

                    .subscriberContext( botInstance::subscriberContext)

                    .subscribe(
                            bot->   log.debug("bot: "+ bot.getProfileId()+", end session successfully.  instance: "+bot.getProfileId()),
                            err-> {
                                log.error("bot: "+ botInstance.getProfileId()+",ERROR while end session  .  instance: "+botInstance.getProfileId(),err);
                                },
                            ()->  log.debug("++++ bot: "+ botInstance.getProfileId()+", end session  complete. ")
                    );
        };
    }



    public Mono<BotInstance> recordAndEndSession() {

        return BotInstance.fromContext()

                .doOnNext(bot->log.debug("save position data and close position: "+  bot.botInfo()))
                .flatMap(positionRecorder::saveTradingSession)
                .doOnError(err->log.error("error while trying to save trading session data.",err))
                .onErrorReturn(TradingSessionRecord.builder().build())
                .flatMap(r->BotInstance.fromContext())
                .flatMap( bot->{
                    if (bot.isLoop()) {
                        bot.setState(null);
                        log.debug("start bot instance again: "+  bot.botInfo());
                        return botInstanceMongoOps.saveState(bot).thenReturn(bot);
                    }else {
                        log.debug("delete bot instance: "+  bot.botInfo());
                        return botInstanceRepository.delete(bot).then(Mono.empty());
                    }
                });
    }
}
