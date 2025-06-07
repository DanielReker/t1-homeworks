package io.github.danielreker.t1homeworks.aop.data;

import lombok.RequiredArgsConstructor;

import java.time.Instant;

@RequiredArgsConstructor
public class CacheEntry {
    public final Object value;
    public final Instant cachedAt = Instant.now();
}
