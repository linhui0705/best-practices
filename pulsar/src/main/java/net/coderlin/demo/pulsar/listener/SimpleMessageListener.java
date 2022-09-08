package net.coderlin.demo.pulsar.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Title: MessageListener
 * Description:
 *
 * @author Lin Hui
 * Created on 2021/9/20 00:54:25
 */
@Component("simpleMessageListener")
@Slf4j
public class SimpleMessageListener implements MessageListener<String> {
    @Override
    public void received(Consumer<String> consumer, Message<String> msg) {
        try {
            log.info("Message received: {}", new String(msg.getData()));
            consumer.acknowledge(msg);
        } catch (Exception e) {
            consumer.negativeAcknowledge(msg);
        }
    }
}
