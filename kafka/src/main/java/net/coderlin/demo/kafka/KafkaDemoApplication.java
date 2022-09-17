package net.coderlin.demo.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Title: KafkaDemoApplication
 * Description:
 * Spring Boot与Kafka版本号对应关系：https://spring.io/projects/spring-kafka
 *
 * @author Lin Hui
 * Created on 2022/9/13 14:56:10
 */
@SpringBootApplication
public class KafkaDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaDemoApplication.class, args);
    }
}
