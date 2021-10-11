package tr.com.mozpinar.alpakka;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.kafka.javadsl.Consumer;
import akka.kafka.javadsl.SendProducer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@EnableScheduling
public class Business {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${kafka.reviewTopic}")
    private String reviewTopic;

    private final Sink<Review, CompletionStage<Done>> sink = Sink.foreach(record -> logger.info(String.format("Received message: %s", record.toString())));
    private final Flow<ConsumerRecord<String, byte[]>, Review, NotUsed> flow = Flow.fromFunction(record -> Review.parseFrom(record.value()));

    private final AtomicInteger counter = new AtomicInteger(200);

    private final ActorSystem actorSystem;
    private final Source<ConsumerRecord<String, byte[]>, Consumer.Control> source;
    private final SendProducer<String, byte[]> producer;

    public Business(ActorSystem actorSystem, SendProducer<String, byte[]> producer,
                    Source<ConsumerRecord<String, byte[]>, Consumer.Control> source) {
        this.actorSystem = actorSystem;
        this.source = source;
        this.producer = producer;
    }

    @PostConstruct
    private void init() {
        source
                .via(flow)
                .toMat(sink, Keep.right())
                .run(actorSystem);
    }

    /**
     * Push messages periodically to topic
     */
    @Scheduled(fixedDelay = 2000L)
    private void publish() {
        int messageNumber = counter.addAndGet(1);
        Review review = Review.newBuilder()
                .setProductName("book-" + messageNumber)
                .setUsername("mozpinar-" + messageNumber)
                .setMessage("Exciting!-" + messageNumber)
                .setRate(messageNumber % 4 + 1)
                .build();

        logger.info("Sending new review: " + review.toString());
        producer.send(new ProducerRecord<>(reviewTopic, review.getProductName(), review.toByteArray()));
    }

}