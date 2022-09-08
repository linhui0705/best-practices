package net.coderlin.middleware.demo.rocketmq.config;

import net.coderlin.middleware.demo.rocketmq.listener.SimpleMQConsumeBListener;
import net.coderlin.middleware.demo.rocketmq.listener.SimpleMQConsumeListener;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Title: RocketMQConfig
 * Description:
 *
 * @author Lin Hui
 * Created on 2021/8/21 12:01:59
 */
@Configuration
public class RocketMQConfig {

    @Value("${rocketmq.namesrvaddr:localhost:9876}")
    private String namesrvAddr;

    @Resource
    private SimpleMQConsumeListener listener;

    @Resource
    private SimpleMQConsumeBListener bListener;

    @Bean(name = "simpleMQProducer")
    public MQProducer simpleMQProducer() throws MQClientException {
        // 实例化消息生产者Producer
        DefaultMQProducer producer = new DefaultMQProducer("GID_SELF_TEST");
        // 设置NameServer的地址
        producer.setNamesrvAddr(namesrvAddr);
        // 启动Producer实例
        producer.start();
        return producer;
    }

    @Bean(name = "simpleMQConsumer")
    public MQConsumer simpleMQConsumer() throws MQClientException {
        // 实例化消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("GID_SELF_TEST");
        // 设置NameServer的地址
        consumer.setNamesrvAddr("localhost:9876");
        // 订阅一个或者多个Topic，以及Tag来过滤需要消费的消息
        consumer.subscribe("SELF_TEST_TOPIC", "*");
        consumer.setMessageListener(listener);
        // 启动消费者实例
        consumer.start();
        return consumer;
    }

    /**
     * 同一Topic、不同Group下的消费者
     *
     * @return
     * @throws MQClientException
     */
    @Bean(name = "simpleMQBConsumer")
    public MQConsumer simpleMQBConsumer() throws MQClientException {
        // 实例化消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("GID_SELF_TEST_B");
        // 设置NameServer的地址
        consumer.setNamesrvAddr("localhost:9876");
        // 订阅一个或者多个Topic，以及Tag来过滤需要消费的消息
        consumer.subscribe("SELF_TEST_TOPIC", "*");
        consumer.setMessageListener(bListener);
        // 启动消费者实例
        consumer.start();
        return consumer;
    }
}
