package Audit;

import java.time.Instant;

public class AuditLogEntry {

    private final Instant timestampUTC;
    private final String threadId;
    private final OperationType operationType;
    private final String userAction;
    private final long executionTimeMs;
    private final boolean success;

    public AuditLogEntry(
            Instant timestampUTC,
            String threadId,
            OperationType operationType,
            String userAction,
            long executionTimeMs,
            boolean success
    ) {
        this.timestampUTC = timestampUTC;
        this.threadId = threadId;
        this.operationType = operationType;
        this.userAction = userAction;
        this.executionTimeMs = executionTimeMs;
        this.success = success;
    }

    public Instant getTimestampUTC() {
        return timestampUTC;
    }

    public String getThreadId() {
        return threadId;
    }

    public OperationType getOperationType() {
        return operationType;
    }


    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    @Override
    public String toString() {
        return String.format(
                "%s | Thread=%s | Operation=%s | Action=%s | Time=%dms | Success=%s",
                timestampUTC,
                threadId,
                operationType,
                userAction,
                executionTimeMs,
                success
        );
    }
}
