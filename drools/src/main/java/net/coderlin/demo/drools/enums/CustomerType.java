package net.coderlin.demo.drools.enums;

/**
 * Title: CustomerType
 * Description:
 *
 * @author Lin Hui
 * Created on 2024/8/15 16:49:20
 */
public enum CustomerType {
    LOYAL, NEW, DISSATISFIED;

    public String getValue() {
        return this.toString();
    }
}
