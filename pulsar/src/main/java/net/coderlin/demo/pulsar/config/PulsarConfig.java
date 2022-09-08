package net.coderlin.demo.pulsar.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Title: PulsarConfig
 * Description:
 *
 * @author Lin Hui
 * Created on 2021/9/16 23:51:45
 */
@Configuration
@Slf4j
public class PulsarConfig {
    @Bean
    public PulsarClient client() throws PulsarClientException {
        return PulsarClient
                .builder()
                .serviceUrl("pulsar://localhost:6650")
                .build();
    }

    @Bean
    public Producer<String> producer(PulsarClient client) throws PulsarClientException {
        return client.newProducer(Schema.STRING)
                .topic("my-topic")
                .create();
    }

    @Bean
    public Consumer<String> consumer(PulsarClient client,
                                     @Qualifier("simpleMessageListener") MessageListener<String> listener) throws PulsarClientException {
        return client.newConsumer(Schema.STRING)
                .topic("my-topic")
                .subscriptionName("my-subscription")
                .messageListener(listener)
                .subscribe();
    }

}
