package Scheduler;

import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * Simple Scheduler.Task class for PriorityQueue demonstration
 */
class Task {
    private final String name;
    private final int priority; // higher number = higher priority

    public Task(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return name + " (Priority: " + priority + ")";
    }
}

/**
 * Scheduler.TaskScheduler using PriorityQueue
 * - Higher priority tasks are processed first
 * - Demonstrates US-1 requirement
 */
public class TaskScheduler {

    // PriorityQueue with custom Comparator: higher priority first
    private final PriorityQueue<Task> taskQueue =
            new PriorityQueue<>(Comparator.comparingInt(Task::getPriority).reversed());

    // Add a task
    public void addTask(Task task) {
        taskQueue.offer(task); // O(log n)
    }

    // Process the next task
    public Task pollTask() {
        return taskQueue.poll(); // O(log n)
    }

    // Peek at next task without removing
    public Task peekTask() {
        return taskQueue.peek(); // O(1)
    }

    // Number of tasks in queue
    public int getTaskCount() {
        return taskQueue.size(); // O(1)
    }

    // Display all tasks (not removing)
    public void displayTasks() {
        System.out.println("Tasks in queue:");
        taskQueue.stream()
                .sorted(Comparator.comparingInt(Task::getPriority).reversed())
                .forEach(System.out::println);
    }
}
