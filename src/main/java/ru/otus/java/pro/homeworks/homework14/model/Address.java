package ru.otus.java.pro.homeworks.homework14.model;

import org.springframework.data.relational.core.mapping.Column;

public class Address {
    @Column("STREET")
    private String street;

    public Address() {}

    public Address(String street) {
        this.street = street;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}