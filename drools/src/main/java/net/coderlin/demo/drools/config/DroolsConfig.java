package net.coderlin.demo.drools.config;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Title: DroolsConfig
 * Description:
 *
 * @author Lin Hui
 * Created on 2024/8/15 16:33:36
 */
@Configuration
public class DroolsConfig {
    private static final String RELES_CUSTOMER_RULES_DRL = "rules/customer-discount.drl";
    // KieServices是drools系统服务的入口
    private static final KieServices KIE_SERVICES = KieServices.Factory.get();

    @Bean
    public KieContainer kieContainer() {
        // 读取规则文件
        KieFileSystem kieFileSystem = KIE_SERVICES.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource(RELES_CUSTOMER_RULES_DRL));
        // 构建知识库和module
        KieBuilder kieBuilder = KIE_SERVICES.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        KieModule kieModule = kieBuilder.getKieModule();
        // 获取指定module的container
        KieContainer kieContainer = KIE_SERVICES.newKieContainer(kieModule.getReleaseId());
        return kieContainer;
    }
}
