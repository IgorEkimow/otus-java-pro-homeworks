package ru.otus.java.pro.homeworks.homework10.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "client", fetch = FetchType.LAZY, orphanRemoval = true)
    private Address address;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Phone> phones = new ArrayList<>();

    public Client() {}

    public Client(String name) {
        this.name = name;
    }

    public void setAddress(Address address) {
        if (this.address == address) {
            return;
        }

        if (this.address != null) {
            this.address.setClient(null);
        }

        this.address = address;

        if (address != null) {
            address.setClient(this);
        }
    }

    public void addPhone(Phone phone) {
        if (!phones.contains(phone)) {
            phones.add(phone);
            phone.setClient(this);
        }
    }

    public void removePhone(Phone phone) {
        if (phones.remove(phone)) {
            phone.setClient(null);
        }
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

    public List<Phone> getPhones() {
        return Collections.unmodifiableList(phones);
    }

    @Override
    public String toString() {
        return "Client{id=" + id + ", name='" + name + "'" + ", address=" + (address != null ? address.getStreet() : null) + ", phones=" + phones.size() + "}";
    }
}