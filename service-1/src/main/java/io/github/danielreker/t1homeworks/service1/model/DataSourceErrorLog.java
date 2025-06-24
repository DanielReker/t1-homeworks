package io.github.danielreker.t1homeworks.service1.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "data_source_error_log")
public class DataSourceErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "stacktrace", columnDefinition = "text")
    private String stacktrace;

    @Column(name = "message", columnDefinition = "text")
    private String message;

    @Column(name = "method_signature", columnDefinition = "text")
    private String methodSignature;
}