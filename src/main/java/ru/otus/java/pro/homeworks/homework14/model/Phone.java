package ru.otus.java.pro.homeworks.homework14.model;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("PHONES")
public class Phone {
    @Column("NUMBER")
    private String number;

    public Phone() {}

    public Phone(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}