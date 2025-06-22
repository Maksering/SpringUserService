package springApp.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:29092", "port=29092" })
public class KafkaProducerServiceIntegrationTest {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Test
    void testServiceIsProperlyConfigured() {
        assertNotNull(kafkaProducerService);
    }

    @Test
    void testSendUserCreate() {
        String testEmail = "test@example.com";

        assertDoesNotThrow(() -> kafkaProducerService.sendUserCrate(testEmail));
    }

    @Test
    void testSendUserDelete() {
        String testEmail = "test@example.com";

        assertDoesNotThrow(() -> kafkaProducerService.sendUserDelete(testEmail));
    }
}

