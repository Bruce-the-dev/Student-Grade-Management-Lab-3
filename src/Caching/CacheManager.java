package Caching;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;
import java.time.Instant;

/**
 * Thread-safe Cache Manager with LRU eviction, stats, invalidation, and auto-refresh
 */
public class CacheManager<K, V> {

    private final int MAX_SIZE = 150;

    // Thread-safe cache
    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();

    // Stats counters
    private final AtomicInteger hits = new AtomicInteger();
    private final AtomicInteger misses = new AtomicInteger();
    private final AtomicInteger evictions = new AtomicInteger();

    // Scheduler for background refresh
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * Retrieve from cache
     */
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null) {
            hits.incrementAndGet();
            entry.updateAccessTime();
            return entry.getValue();
        } else {
            misses.incrementAndGet();
            return null;
        }
    }

    /**
     * Add or update cache entry
     */
    public void put(K key, V value) {
        if (cache.size() >= MAX_SIZE) {
            evictLRU();
        }
        cache.put(key, new CacheEntry<>(value));
    }

    /**
     * Evict least recently used entry
     */
    private void evictLRU() {
        K lruKey = cache.entrySet()
                .stream()
                .min(Comparator.comparingLong(e -> e.getValue().getLastAccessTime()))
                .map(Map.Entry::getKey)
                .orElse(null);

        if (lruKey != null) {
            cache.remove(lruKey);
            evictions.incrementAndGet();
            System.out.println("Evicted LRU cache entry: " + lruKey);
        }
    }

    /**
     * Invalidate a specific entry
     */
    public void invalidate(K key) {
        if (cache.remove(key) != null) {
            evictions.incrementAndGet();
            System.out.println("Cache invalidated for key: " + key);
        }
    }

    /**
     * Clear all cache entries
     */
    public void clearCache() {
        cache.clear();
        System.out.println("Cache cleared");
    }

    /**
     * Start background refresh with custom task
     */
    public void startAutoRefresh(long intervalSeconds, Runnable refreshTask) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                refreshTask.run();
            } catch (Exception e) {
                System.err.println("Error refreshing cache: " + e.getMessage());
            }
        }, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
    }

    /**
     * Stop background refresh
     */
    public void stopAutoRefresh() {
        scheduler.shutdownNow();
    }

    /**
     * Display cache statistics
     */
    public void printStats() {
        int totalEntries = cache.size();
        double hitRate = totalEntries == 0 ? 0 : (hits.get() * 100.0) / (hits.get() + misses.get());
        double missRate = 100 - hitRate;

        System.out.println("CACHE STATISTICS");
        System.out.println("----------------");
        System.out.println("Total Entries: " + totalEntries);
        System.out.printf("Hits: %d (%.2f%%)%n", hits.get(), hitRate);
        System.out.printf("Misses: %d (%.2f%%)%n", misses.get(), missRate);
        System.out.println("Evictions: " + evictions.get());
        System.out.println("Memory Usage (approx): " + (totalEntries * 200) + " KB"); // rough estimate
    }

    /**
     * Show all cache contents with last access timestamp
     */
    public void displayCacheContents() {
        System.out.println("CACHE CONTENTS");
        System.out.println("--------------");
        cache.forEach((key, entry) -> {
            System.out.println(key + " | Last Accessed: " + Instant.ofEpochMilli(entry.getLastAccessTime()));
        });
    }

    /**
     * Cache entry wrapper
     */
    private static class CacheEntry<V> {
        private final V value;
        private long lastAccessTime;

        public CacheEntry(V value) {
            this.value = value;
            this.lastAccessTime = System.currentTimeMillis();
        }

        public V getValue() {
            return value;
        }

        public long getLastAccessTime() {
            return lastAccessTime;
        }

        public void updateAccessTime() {
            lastAccessTime = System.currentTimeMillis();
        }
    }
}
