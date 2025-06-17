package io.github.danielreker.t1homeworks.service1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "time_limit_exceed_log")
public class TimeLimitExceedLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "method_signature", nullable = false, columnDefinition = "text")
    private String methodSignature;

    @NotNull
    @Column(name = "measured_time_ms", nullable = false)
    private Double measuredTimeMs;

    @NotNull
    @Column(name = "logged_at", nullable = false)
    private Instant loggedAt;

}