package io.github.danielreker.t1homeworks.aop;

import io.github.danielreker.t1homeworks.model.TimeLimitExceedLog;
import io.github.danielreker.t1homeworks.repository.TimeLimitExceedLogRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class MetricAspect {
    @Value("${spring.application.metrics.time-limit-ms}")
    private double timeLimitMs;

    private final TimeLimitExceedLogRepository repository;

    @Around("@annotation(io.github.danielreker.t1homeworks.aop.annotation.Metric)")
    public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch clock = new StopWatch();
        try {
            clock.start();
            return joinPoint.proceed();
        } finally {
            clock.stop();
            if (clock.getTotalTimeMillis() > timeLimitMs) {
                repository.save(TimeLimitExceedLog.builder()
                        .measuredTimeMs(clock.getTotalTime(TimeUnit.MILLISECONDS))
                        .methodSignature(joinPoint.getSignature().toLongString())
                        .loggedAt(Instant.now())
                        .build());
            }
        }
    }
}
