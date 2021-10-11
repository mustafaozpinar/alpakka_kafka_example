package tr.com.mozpinar.alpakka.config;


import akka.actor.ActorSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(akka.stream.javadsl.Source.class)
public class AkkaConfig {

    private final ActorSystem system;

    @Autowired
    public AkkaConfig() {
        system = ActorSystem.create("SpringWebAkkaStreamsSystem");
    }

    @Bean
    @ConditionalOnMissingBean(ActorSystem.class)
    public ActorSystem getActorSystem() {
        return system;
    }
}
