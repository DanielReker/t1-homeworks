package io.github.danielreker.t1homeworks.service1.aop;

import io.github.danielreker.t1homeworks.service1.aop.data.CacheEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class CachedAspect {
    @Value("${spring.application.cache.timeout-sec}")
    private long cacheTimeoutSeconds;

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    @Around("@annotation(io.github.danielreker.t1homeworks.service1.aop.annotation.Cached)")
    public Object cacheMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodSignature = joinPoint.getSignature().toLongString();

        String cacheKey = methodSignature + ":" + Arrays.deepHashCode(joinPoint.getArgs());
        CacheEntry entryFromCache = cache.get(cacheKey);

        if (entryFromCache != null && !isExpired(entryFromCache)) {
            log.info("CACHE HIT for method {}, key: {}", methodSignature, cacheKey);
            return entryFromCache.value;
        }

        log.info("CACHE MISS for method {}, key: {}. Executing method.", methodSignature, cacheKey);
        Object entryFromDataSource = joinPoint.proceed();

        cache.put(cacheKey, new CacheEntry(entryFromDataSource));
        log.info("CACHED result for method {}, key: {}", methodSignature, cacheKey);

        return entryFromDataSource;
    }

    private boolean isExpired(CacheEntry entry) {
        Instant expiresAt = entry.cachedAt.plus(Duration.ofSeconds(cacheTimeoutSeconds));
        return expiresAt.compareTo(Instant.now()) <= 0;
    }
}
