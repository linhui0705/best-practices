package net.coderlin.demo.rocketmq.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Title: SimpleMQConsumeListener
 * Description:
 *
 * @author Lin Hui
 * Created on 2021/8/21 12:13:28
 */
@Component
@Slf4j
public class SimpleMQConsumeListener implements MessageListenerConcurrently {
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        log.info("SimpleMQConsumeListener. {} Receive New Messages: {}", Thread.currentThread().getName(), list);
        for (MessageExt message : list) {
            log.info("SimpleMQConsumeListener. message: {}", new String(message.getBody()));
        }
        // 标记该消息已经被成功消费
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
