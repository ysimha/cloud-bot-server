package ys.cloud.sbot.model.instance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ys.cloud.sbot.logic.TestBotInstanceHelper;
import ys.cloud.sbot.model.State;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BotInstanceMongoOpsTest {

    @Autowired BotInstanceMongoOps botInstanceMongoOps;
    @Autowired BotInstanceRepository botInstanceRepository;
    @Autowired TestBotInstanceHelper testBotInstanceHelper;

    @Test
    public void testSaveState() {

        Collection<String> ids = Arrays.asList("a,b".split(","));

        try {
            ids.forEach(testBotInstanceHelper::createBotInstance);

            BotInstance orgBot = botInstanceRepository.findByProfileId("a").blockFirst();

            String aId = orgBot.getId();

            StepVerifier.create(
                            botInstanceMongoOps.takeForMethod("test", aId)
                                    .doOnNext(bot -> {
                                        System.out.println("found: " + bot);
                                    }))
                    .assertNext(b->assertNull(b.getMethod()))
                    .verifyComplete();

            BotInstance botInstance = botInstanceRepository.findByProfileId("a").blockFirst();
            assertEquals("test",botInstance.getMethod());

            assertNull(orgBot.getMethod());

            State state = new State();
//			state.setSource("xxxx");
            orgBot.setState(state);

            StepVerifier.create(
                            botInstanceMongoOps.saveState(orgBot)
                                    .doOnNext(bot -> {
                                        System.out.println("result: " + bot);
                                    }))
                    .expectNextCount(1)
                    .verifyComplete();

            botInstance = botInstanceRepository.findByProfileId("a").blockFirst();
            assertEquals("test",botInstance.getMethod());
            assertNotNull(botInstance.getState());
//			assertEquals("xxxx",botInstance.getState().getSource());

        }finally {
            ids.forEach(testBotInstanceHelper::cleanBotInstance);
        }
    }

    @Test
    public void testFindStandByIds() {

        Collection<String> ids = Arrays.asList("a,b,c,d".split(","));

        try {
            ids.forEach(testBotInstanceHelper::createBotInstance);

            StepVerifier.create(
                            botInstanceMongoOps.findStandbyIds()
                                    .doOnNext(bot -> {
                                        System.out.println("found: " + bot);
                                    }))
                    .expectNextCount(4)
                    .verifyComplete();

            String id = botInstanceRepository.findByProfileId("a").blockFirst().getId();
            botInstanceMongoOps.takeForMethod("test", id).block();

            StepVerifier.create(
                            botInstanceMongoOps.findStandbyIds()
                                    .doOnNext(bot -> {
                                        System.out.println("found: " + bot);
                                    }))
                    .expectNextCount(3)
                    .verifyComplete();

        }finally {
            ids.forEach(testBotInstanceHelper::cleanBotInstance);
        }
    }

    @Test
    public void testFindActiveByIds() {
        Collection<String> ids = Arrays.asList("a,b,c,d".split(","));

        try {
            ids.forEach(testBotInstanceHelper::createBotInstance);

            StepVerifier.create(
                            botInstanceMongoOps.findActiveIds()
                                    .doOnNext(bot -> {
                                        System.out.println("found: " + bot);
                                    }))
                    .expectNextCount(0)
                    .verifyComplete();

            String id = botInstanceRepository.findByProfileId("a").blockFirst().getId();
            botInstanceMongoOps.takeForMethod("test", id).block();

            id = botInstanceRepository.findByProfileId("b").blockFirst().getId();
            botInstanceMongoOps.takeForMethod("test", id).block();

            StepVerifier.create(
                            botInstanceMongoOps.findStandbyIds()
                                    .doOnNext(bot -> {
                                        System.out.println("found: " + bot);
                                    }))
                    .expectNextCount(2)
                    .verifyComplete();

        }finally {
            ids.forEach(testBotInstanceHelper::cleanBotInstance);
        }
    }

    @Test
    public void testTakeForMethod() {
        Collection<String> ids = Arrays.asList("a,b".split(","));

        try {
            ids.forEach(testBotInstanceHelper::createBotInstance);

            String aId = botInstanceRepository.findByProfileId("a").blockFirst().getId();
            StepVerifier.create(
                            botInstanceMongoOps.takeForMethod("test", aId)
                                    .doOnNext(bot -> {
                                        System.out.println("found: " + bot);
                                    }))
                    .assertNext(b->assertNull(b.getMethod()))
                    .verifyComplete();

            BotInstance botInstance = botInstanceRepository.findByProfileId("a").blockFirst();
            assertEquals("test",botInstance.getMethod());

            StepVerifier.create(
                            botInstanceMongoOps.takeForMethod("test", aId)
                                    .doOnNext(bot -> {
                                        System.out.println("found: " + bot);
                                    }))
                    .expectNextCount(0)
                    .verifyComplete();

            StepVerifier.create(
                            botInstanceRepository.findByProfileId("a")
                                    .doOnNext(bot -> bot.setMethod(null))
                                    .flatMap(botInstanceRepository::save)
                                    .flatMap(x->botInstanceMongoOps.takeForMethod("test", aId))
                    )
                    .expectNextCount(1)
                    .verifyComplete();

            String bId = botInstanceRepository.findByProfileId("b").blockFirst().getId();

            StepVerifier.create(
                            botInstanceMongoOps.takeForMethod("test", bId)
                                    .doOnNext(bot -> {
                                        System.out.println("found: " + bot);
                                    }))
                    .expectNextCount(1)
                    .verifyComplete();

            botInstance = botInstanceRepository.findByProfileId("b").blockFirst();
            assertEquals("test", botInstance.getMethod());

        }finally {
            ids.forEach(testBotInstanceHelper::cleanBotInstance);
        }
    }


    @Test
    public void testTakeForMethodMissing() {
        Collection<String> ids = Arrays.asList("a,b,d".split(","));

        try {
            ids.forEach(testBotInstanceHelper::createBotInstance);

            StepVerifier.create(
                            Flux.fromArray("a,b,c,d".split(","))
                                    .flatMap(botInstanceRepository::findByProfileId)
                                    .doOnNext(System.out::println)
                    )
                    .expectNextCount(3)
                    .verifyComplete();

        }finally {
            ids.forEach(testBotInstanceHelper::cleanBotInstance);
        }
    }

    @Test
    public void testReleaseFormMethod() {
        Collection<String> ids = Arrays.asList("a,b".split(","));

        try {
            ids.forEach(testBotInstanceHelper::createBotInstance);

            String aId = botInstanceRepository.findByProfileId("a").blockFirst().getId();

            StepVerifier.create(
                            botInstanceMongoOps.takeForMethod("test", aId)
                                    .doOnNext(bot -> {
                                        System.out.println("found: " + bot);
                                    }))
                    .expectNextCount(1)
                    .verifyComplete();

            BotInstance botInstance = botInstanceRepository.findByProfileId("a").blockFirst();
            assertEquals("test", botInstance.getMethod());

            StepVerifier.create(
                            botInstanceMongoOps.releaseFormMethod(aId)
                                    .doOnNext(res -> {
                                        System.out.println("got: " + res);
                                    }))
                    .expectNextCount(1)
                    .verifyComplete();

            botInstance = botInstanceRepository.findByProfileId("a").blockFirst();
            assertNull(botInstance.getMethod());

            String bId = botInstanceRepository.findByProfileId("b").blockFirst().getId();

            StepVerifier.create(
                            botInstanceMongoOps.releaseFormMethod(bId)
                                    .doOnNext(res -> {
                                        System.out.println("got: " + res);
                                    }))
                    .expectNextCount(1)
                    .verifyComplete();

        }finally {
            ids.forEach(testBotInstanceHelper::cleanBotInstance);
        }
    }

    @Test
    public void testFindForMaintenanceIds(){
        String s = botInstanceMongoOps.findIdsForMaintenance(60).blockFirst();
        System.out.println("\n==================="+s+"\n");
    }

    @Test
    public void release(){
        botInstanceMongoOps.releaseFormMethod("5c284bc1e607c17690e55190").block();
    }

    @Test
    public void takeForMethod(){
        botInstanceMongoOps.takeForMethod("test","5c2107e1e607c144377f131e").block();
    }

    @Test
    public void removeSession(){
        List<BotInstance> list =botInstanceRepository.findAll().toStream().collect(Collectors.toList());
        list.forEach(bi->{
                    bi.setState(null);
                    botInstanceRepository.save(bi).block();
                }
        );
    }
}