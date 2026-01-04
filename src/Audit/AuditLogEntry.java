package Audit;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public final class AuditLogEntry {

    private final Instant timestamp;
    private final long threadId;
    private final OperationType operationType;
    private final String userAction;
    private final long executionTimeMs;
    private final boolean success;

    public AuditLogEntry(
            Instant timestamp,
            long threadId,
            OperationType operationType,
            String userAction,
            long executionTimeMs,
            boolean success
    ) {
        this.timestamp = timestamp;
        this.threadId = threadId;
        this.operationType = operationType;
        this.userAction = userAction;
        this.executionTimeMs = executionTimeMs;
        this.success = success;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public long getThreadId() {
        return threadId;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public String getUserAction() {
        return userAction;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public boolean isSuccess() {
        return success;
    }

    /**
     * Formats the log entry as a single line (ISO-8601 compliant)
     */
    public String toLogLine() {
        return String.format(
                "%s | thread=%d | op=%s | time=%dms | %s | %s",
                DateTimeFormatter.ISO_INSTANT.format(timestamp),
                threadId,
                operationType,
                executionTimeMs,
                success ? "SUCCESS" : "FAILURE",
                userAction
        );
    }
}

