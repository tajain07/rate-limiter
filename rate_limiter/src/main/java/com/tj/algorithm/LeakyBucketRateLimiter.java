package com.tj.algorithm;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The Leaky Bucket algorithm is a rate limiting technique that controls the
 * flow of requests by smoothing out the traffic. Requests are released from the
 * bucket at a fixed rate, resembling water leaking out of a bucket at a steady
 * pace. If the bucket becomes full, additional incoming requests are either
 * discarded or delayed.
 * 
 * The Leaky Bucket algorithm provides a controlled and consistent rate of
 * request processing, regardless of the incoming request rate. It helps prevent
 * sudden bursts of traffic and ensures a smoother flow of requests.
 * 
 */
public class LeakyBucketRateLimiter implements RateLimiter {

    private final long capacity;
    private final long ratePerSecond;
    private final AtomicLong lastRequestTime;
    private final AtomicLong currentBucketSize;

    public LeakyBucketRateLimiter(long capacity, long ratePerSecond) {
        this.capacity = capacity;
        this.ratePerSecond = ratePerSecond;
        this.lastRequestTime = new AtomicLong(System.currentTimeMillis());
        this.currentBucketSize = new AtomicLong(0);
    }

    @Override
    public synchronized boolean isAllowed(String clientId) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastRequestTime.getAndSet(currentTime);

        // Calculate the amount of tokens leaked since the last request
        long leakedTokens = elapsedTime * ratePerSecond / 1000;
        currentBucketSize.updateAndGet(bucketSize -> Math.max(0, Math.min(bucketSize + leakedTokens, capacity)));

        // Check if a request can be processed by consuming a token from the bucket
        if (currentBucketSize.get() > 0) {
            currentBucketSize.decrementAndGet();
            return true; // Request is allowed
        }

        return false; // Request is not allowed
    }

}
