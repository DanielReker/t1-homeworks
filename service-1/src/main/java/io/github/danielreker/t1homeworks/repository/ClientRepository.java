package io.github.danielreker.t1homeworks.repository;

import io.github.danielreker.t1homeworks.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}