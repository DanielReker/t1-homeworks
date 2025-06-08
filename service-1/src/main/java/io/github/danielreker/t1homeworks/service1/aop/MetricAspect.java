package io.github.danielreker.t1homeworks.service1.aop;

import io.github.danielreker.t1homeworks.service1.model.dto.TimeLimitExceedLogDto;
import io.github.danielreker.t1homeworks.service1.service.TimeLimitExceedLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class MetricAspect {
    @Value("${spring.application.metrics.time-limit-ms}")
    private double timeLimitMs;

    private final TimeLimitExceedLogService logService;

    @Around("@annotation(io.github.danielreker.t1homeworks.service1.aop.annotation.Metric)")
    public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch clock = new StopWatch();
        try {
            clock.start();
            return joinPoint.proceed();
        } finally {
            clock.stop();
            if (clock.getTotalTimeMillis() > timeLimitMs) {
                try {
                    logService.logTimeLimitExceed(TimeLimitExceedLogDto.builder()
                            .measuredTimeMs(clock.getTotalTime(TimeUnit.MILLISECONDS))
                            .methodSignature(joinPoint.getSignature().toLongString())
                            .loggedAt(Instant.now())
                            .build());
                } catch (Exception ex) {
                    log.error("Failed to log time limit exceed: {}", ex.getMessage());
                }
            }
        }
    }
}
