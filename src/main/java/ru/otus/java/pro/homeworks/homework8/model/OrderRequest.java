package ru.otus.java.pro.homeworks.homework8.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class OrderRequest {
    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("customer")
    private String customer;
    @JsonProperty("items")
    private List<OrderItem> items;
    @JsonProperty("discountPercent")
    private double discountPercent;

    public OrderRequest() {}

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }
}