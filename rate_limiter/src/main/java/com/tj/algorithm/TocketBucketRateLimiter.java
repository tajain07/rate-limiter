package com.tj.algorithm;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class TocketBucketRateLimiter implements RateLimiter {

    private final long capacity;
    private final AtomicLong tokens;
    private final Duration refillPeriod;
    private volatile Instant lastRefillTime;

    public TocketBucketRateLimiter(long capacity, Duration refillPeriod) {
        this.capacity = capacity;
        this.tokens = new AtomicLong(capacity);
        this.refillPeriod = refillPeriod;
        this.lastRefillTime = Instant.now();
    }

    @Override
    public synchronized boolean isAllowed(String clientId) {
        refillTokens();

        long currentTokens = tokens.get();
        if (currentTokens > 0) {
            tokens.decrementAndGet();
            return true; // Request is allowed
        }

        return false; // Request is not allowed }

    }

    private synchronized void refillTokens() {
        Instant now = Instant.now();
        long timeElapsed = Duration.between(lastRefillTime, now).toMillis();
        long tokensToAdd = timeElapsed / refillPeriod.toMillis();

        if (tokensToAdd > 0) {
            lastRefillTime = now;
            tokens.getAndUpdate(currentTokens -> Math.min(capacity, currentTokens + tokensToAdd));
        }
    }
}
