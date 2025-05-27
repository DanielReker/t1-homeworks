package io.github.danielreker.t1homeworks.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Getter
@Setter
@Entity
@Table(name = "data_source_error_log")
public class DataSourceErrorLog extends AbstractPersistable<Long> {
    @Column(name = "stacktrace")
    private String stacktrace;

    @Column(name = "message")
    private String message;

    @Column(name = "method_signature")
    private String methodSignature;

}