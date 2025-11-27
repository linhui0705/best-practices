package net.coderlin.demo.drools.controller;

import net.coderlin.demo.drools.model.OrderDiscount;
import net.coderlin.demo.drools.model.OrderRequest;
import net.coderlin.demo.drools.service.OrderDiscountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Title: OrderDiscountController
 * Description:
 *
 * @author Lin Hui
 * Created on 2024/8/15 17:16:27
 */
@RestController
public class OrderDiscountController {
    @Resource
    private OrderDiscountService orderDiscountService;

    @PostMapping("get_discount")
    public ResponseEntity<OrderDiscount> getDiscount(@RequestBody OrderRequest orderRequest) {
        OrderDiscount discount = orderDiscountService.getDiscount(orderRequest);
        return new ResponseEntity<OrderDiscount>(discount, HttpStatus.OK);
    }
}
