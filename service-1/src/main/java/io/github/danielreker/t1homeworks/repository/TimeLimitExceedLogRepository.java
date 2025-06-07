package io.github.danielreker.t1homeworks.repository;

import io.github.danielreker.t1homeworks.model.TimeLimitExceedLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeLimitExceedLogRepository extends JpaRepository<TimeLimitExceedLog, Long> {
}