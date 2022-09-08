package net.coderlin.middleware.demo.rocketmq.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.coderlin.middleware.demo.rocketmq.service.SimpleMQService;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Title: TestController
 * Description:
 *
 * @author Lin Hui
 * Created on 2021/8/21 12:23:39
 */
@RequestMapping("/test")
@RestController
@Slf4j
public class TestController {
    @Resource
    private SimpleMQService simpleMQService;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @RequestMapping(value = "/produce", method = RequestMethod.POST)
    public String produce(@RequestBody Map<String, Object> map) throws InterruptedException, RemotingException, MQClientException, MQBrokerException, UnsupportedEncodingException, JsonProcessingException {
        String json = OBJECT_MAPPER.writeValueAsString(map);
        simpleMQService.produce(json);
        return "SUCCESS";
    }

    @RequestMapping(value = "/produce_delay/{level}", method = RequestMethod.POST)
    public String produce(@RequestBody Map<String, Object> map, @PathVariable("level") int level) throws InterruptedException, RemotingException, MQClientException, MQBrokerException, UnsupportedEncodingException, JsonProcessingException {
        String json = OBJECT_MAPPER.writeValueAsString(map);
        simpleMQService.produceDelayMsg(json, level);
        return "SUCCESS";
    }
}
