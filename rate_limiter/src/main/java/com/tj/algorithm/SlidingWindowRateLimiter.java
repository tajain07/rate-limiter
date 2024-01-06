package com.tj.algorithm;

import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SlidingWindowRateLimiter implements RateLimiter {
    private final int maxRequests;
    private final int windowSizeInMillis;
    private final ConcurrentHashMap<String, Deque<Long>> timestamps = new ConcurrentHashMap<>();

    public SlidingWindowRateLimiter(int maxRequests, int windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
    }

    @Override
    public boolean isAllowed(String clientId) {
        Deque<Long> clientTimestamps = timestamps.computeIfAbsent(clientId, k -> new ConcurrentLinkedDeque<>());

        long currentTimeMillis = System.currentTimeMillis();

        while (!clientTimestamps.isEmpty() && currentTimeMillis - clientTimestamps.peekFirst() > windowSizeInMillis) {
            clientTimestamps.pollFirst();
        }

        if (clientTimestamps.size() < maxRequests) {
            clientTimestamps.addLast(currentTimeMillis);
            return true;
        }

        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(3, 30000);
        boolean allowed_client1 = limiter.isAllowed("client1");
        allowed_client1 = limiter.isAllowed("client1");
        boolean allowed_client2 = limiter.isAllowed("client2");
        allowed_client1 = limiter.isAllowed("client1");
        System.out.println("client1_1 " + allowed_client1);

        boolean allowed_client3 = limiter.isAllowed("client3");

        Thread.sleep(20000);
        allowed_client1 = limiter.isAllowed("client1");

        System.out.println("client1_2 " + allowed_client1);
        System.out.println("client2 " + allowed_client2);
        System.out.println("client3 " + allowed_client3);
    }
}
