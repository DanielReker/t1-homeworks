package io.github.danielreker.t1homeworks.service1.model;

import io.github.danielreker.t1homeworks.service1.model.enums.TransactionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "transaction_id", nullable = false, unique = true)
    private UUID transactionId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @NotNull
    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(name = "time", nullable = false)
    private Instant time;
}