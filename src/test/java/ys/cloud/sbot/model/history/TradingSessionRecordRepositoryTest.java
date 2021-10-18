package ys.cloud.sbot.model.history;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TradingSessionRecordRepositoryTest {

    @Autowired TradingSessionRecordRepository recordRepository;

    @Test
    void findByProfileId() {

        TradingSessionRecord record = TradingSessionRecord.builder().profileId("profileId001")
                .createdDate(LocalDateTime.now())
                .build();
        TradingSessionRecord fromDb =  recordRepository.insert(record).block();
        assertNotNull(fromDb.getCreatedDate());

    }
}