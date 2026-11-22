package ru.otus.java.pro.homeworks.homework14.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.otus.java.pro.homeworks.homework14.model.Client;
import java.util.List;

public interface ClientRepository extends ListCrudRepository<Client, Long> {
    List<Client> findAllByOrderByIdAsc();
}