package net.coderlin.demo.pulsar.service;

import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Title: SimpleMQService
 * Description:
 *
 * @author Lin Hui
 * Created on 2021/9/20 00:38:43
 */
@Service
public class SimpleMQService {
    @Resource
    private Producer<String> producer;

    public void produce(String message) throws PulsarClientException {
        MessageId messageId = producer.send(message);
    }
}
