package ru.otus.java.pro.homeworks.homework8.service;

import ru.otus.java.pro.homeworks.homework8.model.InvoiceResponse;
import ru.otus.java.pro.homeworks.homework8.model.OrderItem;
import ru.otus.java.pro.homeworks.homework8.model.OrderRequest;
import java.time.Instant;
import java.util.List;

public class OrderProcessor {
    private static final double TAX_RATE = 0.20;

    public InvoiceResponse process(OrderRequest order) {
        InvoiceResponse response = new InvoiceResponse();

        if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
            response.setOrderId(order != null ? order.getOrderId() : null);
            response.setStatus("ERROR");
            response.setMessage("Заказ пуст или не содержит позиций");
            response.setProcessedAt(Instant.now().toString());

            return response;
        }

        List<OrderItem> items = order.getItems();
        double subtotal = 0.0;
        for (OrderItem item : items) {
            if (item.getPrice() < 0 || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Некорректные данные в позиции: " + item.getName());
            }

            subtotal += item.getTotal();
        }

        double discountPercent = Math.max(0, Math.min(100, order.getDiscountPercent()));
        double discountAmount = subtotal * discountPercent / 100.0;
        double afterDiscount = subtotal - discountAmount;
        double taxAmount = afterDiscount * TAX_RATE;
        double total = afterDiscount + taxAmount;

        response.setOrderId(order.getOrderId());
        response.setCustomer(order.getCustomer());
        response.setSubtotal(round(subtotal));
        response.setDiscountAmount(round(discountAmount));
        response.setTaxAmount(round(taxAmount));
        response.setTotal(round(total));
        response.setProcessedAt(Instant.now().toString());
        response.setStatus("OK");
        response.setMessage("Счёт успешно сформирован");

        return response;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}