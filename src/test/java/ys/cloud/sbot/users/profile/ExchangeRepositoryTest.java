package ys.cloud.sbot.users.profile;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExchangeRepositoryTest {

    @Autowired ExchangeRepository exchangeRepository;
    final Exchange _exchange = Exchange.builder().id("test").name("test-name").build();

    @Test
    @Order(10)
    public void testRepo(){assertNotNull(exchangeRepository);}

    @Test
    @Order(20)
    public void testCreate(){
        Exchange exchange = exchangeRepository.save(_exchange).block();
        assertEquals(_exchange,exchange);
    }

    @Test
    @Order(30)
    public void testFind(){
        Exchange exchange = exchangeRepository.findById("test").block();
        assertEquals(_exchange,exchange);    }

    @Test
    @Order(40)
    public void testDelete(){
        Exchange exchange = exchangeRepository.findById("test").block();
        exchangeRepository.delete(exchange).block();
        StepVerifier.create(exchangeRepository.findById("test")).verifyComplete();
    }
}