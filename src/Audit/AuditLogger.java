package Audit;

import java.io.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class AuditLogger {

    // Thread-safe queue (no lost logs)
    private final BlockingQueue<AuditLogEntry> logQueue =
            new LinkedBlockingQueue<>();

    // Background writer thread
    private final ExecutorService writerService =
            Executors.newSingleThreadExecutor();

    private final AtomicLong totalLogs = new AtomicLong();

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private File currentLogFile;


    public AuditLogger() {
        rotateLogFile();
        writerService.submit(this::writeLogsContinuously);
    }


    public void log(
            OperationType operationType,
            String userAction,
            long executionTimeMs,
            boolean success
    ) {
        AuditLogEntry entry = new AuditLogEntry(
                Instant.now(),                        // UTC
                Thread.currentThread().getName(),
                operationType,
                userAction,
                executionTimeMs,
                success
        );

        logQueue.offer(entry);
        totalLogs.incrementAndGet();
    }

    public void shutdown() {
        writerService.shutdownNow();
    }


    private void writeLogsContinuously() {
        try {
            while (true) {
                AuditLogEntry entry = logQueue.take();
                writeToFile(entry);
            }
        } catch (InterruptedException ignored) {
        }
    }

    private synchronized void writeToFile(AuditLogEntry entry) {
        try {
            if (currentLogFile.length() >= MAX_FILE_SIZE) {
                rotateLogFile();
            }

            try (FileWriter fw = new FileWriter(currentLogFile, true);
                 BufferedWriter bw = new BufferedWriter(fw)) {

                bw.write(entry.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Audit log write failed: " + e.getMessage());
        }
    }

    private void rotateLogFile() {
        String fileName = "audit_" + LocalDate.now() + ".log";
        currentLogFile = new File(fileName);
    }


    /**
     * Get a snapshot of all current log entries
     */
    public List<AuditLogEntry> getEntries() {
        return new ArrayList<>(logQueue); // thread-safe copy
    }

    /**
     * Print the most recent N log entries
     */
    public void printRecentEntries(int limit) {
        System.out.println("\nRECENT AUDIT LOGS");
        System.out.println("────────────────────────");

        List<AuditLogEntry> entries = getEntries();
        int start = Math.max(0, entries.size() - limit);

        for (int i = start; i < entries.size(); i++) {
            System.out.println(entries.get(i));
        }
    }

    /**
     * Print logs filtered by operation type
     */
    public void printByOperation(OperationType operationType) {
        System.out.println("\nAUDIT LOGS — Operation: " + operationType);
        System.out.println("----------------------------------");

        getEntries().stream()
                .filter(entry -> entry.getOperationType() == operationType)
                .forEach(System.out::println);
    }

    /**
     * Print logs filtered by thread ID
     */
    public void printByThread(String threadId) {
        System.out.println("\nAUDIT LOGS — Thread: " + threadId);
        System.out.println("----------------------------------");

        getEntries().stream()
                .filter(entry -> entry.getThreadId().equalsIgnoreCase(threadId))
                .forEach(System.out::println);
    }

    /**
     * Print logs filtered by UTC date range (inclusive)
     */
    public void printByDateRange(LocalDate start, LocalDate end) {
        Instant from = start.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant to = end.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant(); // include end date

        System.out.printf("\nAUDIT LOGS — From %s to %s%n", start, end);
        System.out.println("----------------------------------");

        getEntries().stream()
                .filter(entry -> !entry.getTimestampUTC().isBefore(from) &&
                        entry.getTimestampUTC().isBefore(to))
                .forEach(System.out::println);
    }

    /**
     * Print statistics of the audit trail
     */
    public void printStatistics() {
        List<AuditLogEntry> entries = getEntries();

        long totalOps = entries.size();
        double avgTime = entries.stream()
                .mapToLong(AuditLogEntry::getExecutionTimeMs)
                .average()
                .orElse(0);

        Map<OperationType, Long> opsPerType = new EnumMap<>(OperationType.class);
        for (OperationType type : OperationType.values()) {
            opsPerType.put(type, entries.stream()
                    .filter(e -> e.getOperationType() == type)
                    .count());
        }

        System.out.println("\nAUDIT STATISTICS");
        System.out.println("────────────────────────");
        System.out.println("Total Operations Logged: " + totalOps);
        System.out.printf("Average Execution Time: %.2f ms%n", avgTime);
        System.out.println("Operations per Type:");
        opsPerType.forEach((type, count) -> System.out.printf(" - %s: %d%n", type, count));
    }

}
