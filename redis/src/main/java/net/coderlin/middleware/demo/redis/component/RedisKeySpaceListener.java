package net.coderlin.middleware.demo.redis.component;//package net.coderlin.middleware.demo.redis.component;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.connection.Message;
//import org.springframework.data.redis.listener.*;
//import org.springframework.stereotype.Component;
//
///**
// * Title: RedisKeySpaceListener
// * Description:
// *
// * @author Lin Hui
// * Created on 2021/9/18 22:56:09
// */
//@Component
//@Slf4j
//public class RedisKeySpaceListener extends KeyspaceEventMessageListener {
//
////    private String keyspaceNotificationsConfigParameter = "Kx";
////
////    @Override
////    public void setKeyspaceNotificationsConfigParameter(String keyspaceNotificationsConfigParameter) {
////        this.keyspaceNotificationsConfigParameter = keyspaceNotificationsConfigParameter;
////    }
//
//    /**
//     * Creates new {@link KeyspaceEventMessageListener}.
//     *
//     * @param listenerContainer must not be {@literal null}.
//     */
//    public RedisKeySpaceListener(RedisMessageListenerContainer listenerContainer) {
//        super(listenerContainer);
//        this.setKeyspaceNotificationsConfigParameter("KEA");
//    }
//
////    private static final Topic TOPIC_ALL_KEYEVENTS1 = new PatternTopic("__keyspace@*__:*");
////    private static final Topic TOPIC_ALL_KEYEVENTS = new PatternTopic("__keyevent@*__:*");
//    private static final Topic TOPIC_ALL_KEYEVENTS = new PatternTopic("__key*__:*");
////    private static final Topic TOPIC_ALL_KEYEVENTS = new PatternTopic("__keyspace@* hello");
////    private static final Topic KEYEVENT_EXPIRED_TOPIC = new PatternTopic("__keyevent@*__:expired hello");
////    private static final ObjectMapper MAPPER = new ObjectMapper();
//
//    @Override
//    protected void doRegister(RedisMessageListenerContainer listenerContainer) {
////        listenerContainer.addMessageListener(this, TOPIC_ALL_KEYEVENTS1);
//        listenerContainer.addMessageListener(this, TOPIC_ALL_KEYEVENTS);
////        super.doRegister(listenerContainer);
//    }
//
//    @Override
//    public void onMessage(Message message, byte[] pattern) {
//        String channel = new String(message.getChannel());
//        String body = new String(message.getBody());
//        String patternStr = new String(pattern);
//        log.info("RedisKeySpaceListener onMessage. message: [{}]. channel: [{}]. body: [{}]. pattern: [{}]",
//                message.toString(), channel, body, patternStr);
////        super.onMessage(message, pattern);
//    }
//
//    @Override
//    protected void doHandleMessage(Message message) {
//        String channel = new String(message.getChannel());
//        String body = new String(message.getBody());
//        log.info("RedisKeySpaceListener doHandleMessage. message: [{}]. channel: [{}]. body: [{}]",
//                message.toString(), channel, body);
//    }
//
////    @Override
////    public void setKeyspaceNotificationsConfigParameter(String keyspaceNotificationsConfigParameter) {
////        super.setKeyspaceNotificationsConfigParameter(keyspaceNotificationsConfigParameter);
////    }
//}
