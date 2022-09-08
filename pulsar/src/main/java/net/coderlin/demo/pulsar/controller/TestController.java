package net.coderlin.demo.pulsar.controller;

import net.coderlin.demo.pulsar.service.SimpleMQService;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Title: TestController
 * Description:
 *
 * @author Lin Hui
 * Created on 2021/9/20 00:38:13
 */
@RequestMapping("/test")
@RestController
public class TestController {
    @Resource
    private SimpleMQService simpleMQService;

    @RequestMapping("produce")
    public String produce(@RequestParam("msg") String message) throws PulsarClientException {
        simpleMQService.produce(message);
        return "SUCCESS";
    }
}
