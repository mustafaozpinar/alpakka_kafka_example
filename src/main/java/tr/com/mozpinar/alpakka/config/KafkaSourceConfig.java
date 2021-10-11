package tr.com.mozpinar.alpakka.config;

import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.kafka.ProducerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.kafka.javadsl.SendProducer;
import akka.stream.javadsl.Source;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class KafkaSourceConfig {

    @Value("${kafka.bootstrapServers}")
    private String kafkaBootstrapServers;

    @Value("${kafka.reviewTopic}")
    private String reviewTopic;

    private final ActorSystem actorSystem;

    public KafkaSourceConfig(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    @Bean
    public ConsumerSettings<String, byte[]> getKafkaConsumerSettings() {
        return ConsumerSettings.create(actorSystem, new StringDeserializer(), new ByteArrayDeserializer())
                .withBootstrapServers(kafkaBootstrapServers)
                .withGroupId("alpakka-event-ingestor")
                .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
                .withStopTimeout(Duration.ofSeconds(5));
    }

    @Bean
    public Source<ConsumerRecord<String, byte[]>, Consumer.Control> getKafkaCommittableSource() {
        return Consumer.plainSource(getKafkaConsumerSettings(), Subscriptions.topics(reviewTopic));
    }

    @Bean
    public ProducerSettings<String, byte[]> getKafkaProducerSettings() {
        return ProducerSettings.create(actorSystem, new StringSerializer(), new ByteArraySerializer())
                .withBootstrapServers(kafkaBootstrapServers);
    }

    @Bean
    public SendProducer<String, byte[]> getKafkaProducer() {
        return new SendProducer<>(getKafkaProducerSettings(), actorSystem);
    }
}