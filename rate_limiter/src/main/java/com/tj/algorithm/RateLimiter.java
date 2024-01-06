package com.tj.algorithm;

public interface RateLimiter {
    boolean isAllowed(String clientId);
}
