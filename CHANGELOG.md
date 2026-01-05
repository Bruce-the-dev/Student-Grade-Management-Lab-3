# CHANGELOG

## v3.0.0 – Lab 3: Concurrent Systems & Advanced Java

### Added
- Thread-safe caching system using `ConcurrentHashMap`
- LRU cache eviction policy with a maximum size of 150 entries
- Cache statistics (hits, misses, evictions, memory usage)
- Background cache auto-refresh using `ScheduledExecutorService`
- Manual cache invalidation and cache clearing
- Cache warming on application startup
- Concurrent audit logging system with asynchronous file writing
- Audit log rotation (daily or when file size exceeds 10MB)
- Filtering audit logs by date range, operation type, and thread ID
- Audit statistics (average execution time, total operations)
- Thread-safe data structures across managers
- Separation of computation and presentation following SOLID principles
- Comprehensive unit tests covering collections, concurrency, streams, and regex validation

### Improved
- Performance benchmarks show up to **10x faster** batch report generation compared to v2.0
- Regex validation expanded to cover student IDs, emails, phone numbers, and dates
- Reduced repeated computations through cached statistics

### Changed
- Refactored statistics computation to support caching
- Refactored printing logic into dedicated printer classes
- Updated managers to support dependency injection

### Fixed
- Thread safety issues in shared resources
- Recursive statistics calculation bug
- Lambda type mismatch errors in cache entries
- File I/O resource leaks using proper try-with-resources
