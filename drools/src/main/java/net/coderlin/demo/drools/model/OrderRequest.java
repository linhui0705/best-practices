package net.coderlin.demo.drools.model;

import lombok.Data;
import net.coderlin.demo.drools.enums.CustomerType;

/**
 * Title: OrderRequest
 * Description:
 *
 * @author Lin Hui
 * Created on 2024/8/15 16:47:10
 */
@Data
public class OrderRequest {
    private String customerNumber;
    private Integer age;
    private Integer amount;
    private CustomerType customerType;
}
