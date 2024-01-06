package com.tj.algorithm;

import java.util.HashMap;

public class FixedWindowCounter implements RateLimiter {
    private final int maxRequestPerWindow;
    private final int windowSizeInMillis;
    private final HashMap<String, Window> store = new HashMap<>();

    public FixedWindowCounter(int maxRequestPerWindow, int windoSizeInMillis) {
        this.maxRequestPerWindow = maxRequestPerWindow;
        this.windowSizeInMillis = windoSizeInMillis;
    }

    private static class Window {
        private final long startTime;
        private int requestCount;

        public Window(long startTime, int requestCount) {
            this.startTime = startTime;
            this.requestCount = requestCount;
        }

        public long getStartTime() {
            return startTime;
        }

        public int getRequestCount() {
            return requestCount;
        }

        public void setRequestCount(int requestCount) {
            this.requestCount = requestCount;
        }
    }

    @Override
    public synchronized boolean isAllowed(String clientId) {
        long currentTimeMillis = System.currentTimeMillis();
        Window window = store.get(clientId);

        if (window == null || window.getStartTime() < currentTimeMillis - windowSizeInMillis) {
            window = new Window(currentTimeMillis, 0);
        }

        if (window.getRequestCount() >= maxRequestPerWindow) {
            return false;
        }

        window.setRequestCount(window.getRequestCount() + 1);
        store.put(clientId, window);
        return true;
    }
}
