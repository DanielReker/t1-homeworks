package io.github.danielreker.t1homeworks.service1.repository;

import io.github.danielreker.t1homeworks.service1.model.DataSourceErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataSourceErrorLogRepository extends JpaRepository<DataSourceErrorLog, Long> {
}