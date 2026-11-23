package ru.otus.java.pro.homeworks.homework8;

import ru.otus.java.pro.homeworks.homework8.model.InvoiceResponse;
import ru.otus.java.pro.homeworks.homework8.model.OrderItem;
import ru.otus.java.pro.homeworks.homework8.model.OrderRequest;
import ru.otus.java.pro.homeworks.homework8.service.JsonFileService;
import ru.otus.java.pro.homeworks.homework8.service.OrderProcessor;
import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        File inputFile  = new File("data/input/order.json");
        File outputFile = new File("data/output/invoice.json");
        JsonFileService fileService = new JsonFileService();
        OrderProcessor processor = new OrderProcessor();

        try {
            if (!inputFile.exists()) {
                OrderRequest demo = buildDemoOrder();
                fileService.write(inputFile, demo);
                System.out.println("Создан тестовый входной файл: " + inputFile.getPath());
            }

            OrderRequest order = fileService.read(inputFile, OrderRequest.class);
            System.out.println("Прочитан заказ: " + order.getOrderId() + ", позиций: " + order.getItems().size());
            InvoiceResponse invoice = processor.process(order);
            fileService.write(outputFile, invoice);
            System.out.println("Счёт записан в: " + outputFile.getPath());
            System.out.println("Итог: " + invoice.getTotal());
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка обработки файла: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static OrderRequest buildDemoOrder() {
        OrderItem item1 = new OrderItem(1L, "Ноутбук", 75000.0, 1);
        OrderItem item2 = new OrderItem(2L, "Мышь", 2500.0, 2);
        OrderRequest order = new OrderRequest();
        order.setOrderId("ORD-100500");
        order.setCustomer("Иван Иванов");
        order.setItems(List.of(item1, item2));
        order.setDiscountPercent(10);

        return order;
    }
}