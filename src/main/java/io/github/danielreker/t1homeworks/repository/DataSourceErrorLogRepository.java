package io.github.danielreker.t1homeworks.repository;

import io.github.danielreker.t1homeworks.model.DataSourceErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataSourceErrorLogRepository extends JpaRepository<DataSourceErrorLog, Long> {
}