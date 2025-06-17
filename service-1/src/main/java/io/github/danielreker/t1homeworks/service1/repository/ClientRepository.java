package io.github.danielreker.t1homeworks.service1.repository;

import io.github.danielreker.t1homeworks.service1.model.Client;
import io.github.danielreker.t1homeworks.service1.model.enums.ClientStatus;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByStatusIn(
            Collection<ClientStatus> statuses,
            Limit limit
    );

    List<Client> findByClientId(UUID clientId);
}