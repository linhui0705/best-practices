package net.coderlin.demo.drools.service;

import net.coderlin.demo.drools.model.OrderDiscount;
import net.coderlin.demo.drools.model.OrderRequest;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Title: OrderDiscountService
 * Description:
 *
 * @author Lin Hui
 * Created on 2024/8/15 17:14:18
 */
@Service
public class OrderDiscountService {
    @Resource
    private KieContainer kieContainer;

    public OrderDiscount getDiscount(OrderRequest orderRequest) {
        OrderDiscount orderDiscount = new OrderDiscount();
        // 从container中获取session
        KieSession kieSession = kieContainer.newKieSession();
        kieSession.setGlobal("orderDiscount", orderDiscount);
        kieSession.insert(orderRequest);
        // 执行规则
        kieSession.fireAllRules();
        // 关闭session
        kieSession.dispose();
        return orderDiscount;
    }
}
