package net.coderlin.middleware.demo.rocketmq.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

/**
 * Title: SimpleMQService
 * Description:
 *
 * @author Lin Hui
 * Created on 2021/8/21 12:11:00
 */
@Service
@Slf4j
public class SimpleMQService {

    @Resource(name = "simpleMQProducer")
    private MQProducer producer;

    public void produce(String str) throws UnsupportedEncodingException, InterruptedException, RemotingException, MQClientException, MQBrokerException {
        Message msg = new Message("SELF_TEST_TOPIC" /* Topic */,
                "TagA" /* Tag */,
                (str).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
        );
        // 发送消息到一个Broker
        SendResult sendResult = producer.send(msg);
        log.info("sendResult: {}", sendResult);
    }

    public void produceDelayMsg(String str, int delayLevel) throws UnsupportedEncodingException, InterruptedException, RemotingException, MQClientException, MQBrokerException {
        Message msg = new Message("SELF_TEST_TOPIC" /* Topic */,
                "TagA" /* Tag */,
                (str).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
        );
        // 发送消息到一个Broker
        msg.setDelayTimeLevel(delayLevel);
        SendResult sendResult = producer.send(msg);
        log.info("sendResult: {}", sendResult);
    }
}
