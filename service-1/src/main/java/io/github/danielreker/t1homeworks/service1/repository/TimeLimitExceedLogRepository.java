package io.github.danielreker.t1homeworks.service1.repository;

import io.github.danielreker.t1homeworks.service1.model.TimeLimitExceedLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeLimitExceedLogRepository extends JpaRepository<TimeLimitExceedLog, Long> {
}