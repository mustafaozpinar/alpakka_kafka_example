package tr.com.mozpinar.alpakka;

// #testcontainers-settings

import akka.actor.ActorSystem;
import akka.kafka.testkit.KafkaTestkitTestcontainersSettings;
import akka.kafka.testkit.javadsl.TestcontainersKafkaTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestkitTestcontainersTest extends TestcontainersKafkaTest {

    private static final ActorSystem system = ActorSystem.create("TestkitTestcontainersTest");

    private static KafkaTestkitTestcontainersSettings testcontainersSettings =
            KafkaTestkitTestcontainersSettings.create(system)
                    .withNumBrokers(1)
                    .withReadinessCheckTimeout(Duration.ofSeconds(60));

    TestkitTestcontainersTest() {
        super(system, testcontainersSettings);
    }

    @Test
    void contextLoads() {
    }

    // omit this implementation if you want the cluster to stay up for all your tests
    @AfterAll
    void afterClass() {
        TestcontainersKafkaTest.stopKafka();
    }
}
