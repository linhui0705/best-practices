package net.coderlin.middleware.demo.redis.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;

/**
 * Title: RedisKeyExpirationListener
 * Description:
 *
 * @author Lin Hui
 * Created on 2021/9/18 22:56:09
 */
@Component
@Slf4j
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    private static final Topic KEYEVENT_EXPIRED_TOPIC = new PatternTopic("__keyevent@*__:expired");

    @Override
    protected void doRegister(RedisMessageListenerContainer listenerContainer) {
        listenerContainer.addMessageListener(this, KEYEVENT_EXPIRED_TOPIC);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());
        String patternStr = new String(pattern);
        log.info("RedisKeyExpirationListener onMessage. message: [{}]. channel: [{}]. body: [{}]. pattern: [{}]",
                message.toString(), channel, body, patternStr);
        super.onMessage(message, pattern);
    }

    @Override
    protected void doHandleMessage(Message message) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());
        log.info("RedisKeyExpirationListener doHandleMessage. message: [{}]. channel: [{}]. body: [{}].",
                message.toString(), channel, body);
    }
}
