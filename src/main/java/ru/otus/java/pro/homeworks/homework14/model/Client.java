package ru.otus.java.pro.homeworks.homework14.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Table("CLIENTS")
public class Client {
    @Id
    @Column("ID")
    private Long id;

    @Column("NAME")
    private String name;

    @Embedded.Nullable
    private Address address;

    @MappedCollection(idColumn = "CLIENT_ID", keyColumn = "PHONE_ORDER")
    private List<Phone> phones = new ArrayList<>();

    public Client() {}

    public Client(String name, Address address, List<Phone> phones) {
        this.name = name;
        this.address = address;
        setPhones(phones);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Phone> getPhones() {
        return Collections.unmodifiableList(phones);
    }

    public void setPhones(List<Phone> phones) {
        this.phones = new ArrayList<>(phones);
    }
}