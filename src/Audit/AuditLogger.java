package Audit;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public class AuditLogger {

    private static final String LOG_FILE = "audit.log";

    private final BlockingQueue<AuditLogEntry> logQueue = new LinkedBlockingQueue<>();
    private final ExecutorService writerThread = Executors.newSingleThreadExecutor();

    public AuditLogger() {
        writerThread.submit(this::processLogs);
    }

    /**
     * Public API used by the system
     */
    public void log(OperationType operationType,
                    String userAction,
                    long executionTimeMs,
                    boolean success) {

        AuditLogEntry entry = new AuditLogEntry(
                Instant.now(), // UTC
                Thread.currentThread().getId(),
                operationType,
                userAction,
                executionTimeMs,
                success
        );

        logQueue.offer(entry); // NEVER blocks
    }

    /**
     * Background writer thread
     */
    private void processLogs() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            while (true) {
                AuditLogEntry entry = logQueue.take(); // blocks safely
                writer.write(entry.toLogLine());
                writer.newLine();
                writer.flush();
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Shutdown hook (important)
     */
    public void shutdown() {
        writerThread.shutdownNow();
    }
}
