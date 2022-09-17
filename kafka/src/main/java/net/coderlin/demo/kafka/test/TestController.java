package net.coderlin.demo.kafka.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.common.TopicPartitionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Title: TestController
 * Description:
 *
 * @author Lin Hui
 * Created on 2022/9/13 14:57:36
 */
@RestController
@Slf4j
public class TestController {
    @Value("${spring.kafka.bootstrap-servers}")
    private String springKafkaBootstrapServers;

    private AdminClient adminClient;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @PostConstruct
    private void initAdminClient() {
        Map<String, Object> props = new HashMap<>(1);
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, springKafkaBootstrapServers);
        adminClient = KafkaAdminClient.create(props);
    }

    /**
     * 新增topic (支持批量，这里就单个作为演示)
     *
     * @param topic topic
     * @return ResponseVo
     */
    @PostMapping("kafka")
    public String add(String topic) {
        NewTopic newTopic = new NewTopic(topic, 3, (short) 1);
        adminClient.createTopics(Arrays.asList(newTopic));
        return "SUCCESS";
    }

    /**
     * 查询topic信息 (支持批量，这里就单个作为演示)
     *
     * @param topic 自增主键
     * @return ResponseVo
     */
    @GetMapping("kafka/{topic}")
    public String getBytTopic(@PathVariable String topic) {
        Collection<String> topics = Arrays.asList(topic);
        AtomicReference<String> info = new AtomicReference<>("");
        try {
            adminClient.describeTopics(topics).all().get().forEach((t, description) -> {
                for (TopicPartitionInfo partition : description.partitions()) {
                    info.set(info + partition.toString() + "\n");
                }
            });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return info.get();
    }

    /**
     * 删除topic (支持批量，这里就单个作为演示)
     * (注意：如果topic正在被监听会给人感觉删除不掉（但其实是删除掉后又会被创建）)
     *
     * @param topic topic
     */
    @DeleteMapping("kafka/{topic}")
    public String delete(@PathVariable String topic) {
        adminClient.deleteTopics(Arrays.asList(topic));
        return "SUCCESS";
    }

    /**
     * 查询所有topic
     */
    @GetMapping("kafka/allTopic")
    public List<String> getAllTopic() {
        try {
            return adminClient.listTopics().listings().get().stream().map(TopicListing::name).collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * 生产者往topic中发送消息demo
     *
     * @param topic
     * @param message
     * @return
     */
    @PostMapping("kafka/message")
    public String sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
        return "SUCCESS";
    }

    /**
     * 消费者示例demo
     * <p>
     * 基于注解监听多个topic，消费topic中消息
     * （注意：如果监听的topic不存在则会自动创建）
     */
    @KafkaListener(topics = {"topic1", "topic2", "topic3"})
    public void consume(String message) {
        log.info("receive msg: " + message);
    }
}
