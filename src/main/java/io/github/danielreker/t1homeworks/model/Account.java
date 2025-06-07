package io.github.danielreker.t1homeworks.model;

import io.github.danielreker.t1homeworks.model.enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @NotNull
    @Enumerated
    @Column(name = "type", nullable = false)
    private AccountType type;

    @NotNull
    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
}