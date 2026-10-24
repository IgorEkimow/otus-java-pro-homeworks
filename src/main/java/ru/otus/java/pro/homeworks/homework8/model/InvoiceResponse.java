package ru.otus.java.pro.homeworks.homework8.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InvoiceResponse {
    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("customer")
    private String customer;
    @JsonProperty("subtotal")
    private double subtotal;
    @JsonProperty("discountAmount")
    private double discountAmount;
    @JsonProperty("taxAmount")
    private double taxAmount;
    @JsonProperty("total")
    private double total;
    @JsonProperty("processedAt")
    private String processedAt;
    @JsonProperty("status")
    private String status;
    @JsonProperty("message")
    private String message;

    public InvoiceResponse() {}

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

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(String processedAt) {
        this.processedAt = processedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}