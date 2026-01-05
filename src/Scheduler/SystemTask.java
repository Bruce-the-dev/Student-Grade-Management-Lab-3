package Scheduler;

public class SystemTask implements Comparable<SystemTask> {

    private final String taskName;
    private final int priority;

    public SystemTask(String taskName, int priority) {
        this.taskName = taskName;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(SystemTask other) {
        return Integer.compare(this.priority, other.priority);
    }
}
