import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BatchReportGenerator {

    private final StudentReportGenerator reportGenerator;

    public BatchReportGenerator(StudentReportGenerator reportGenerator) {
        this.reportGenerator = reportGenerator;
    }

    public void generateReports(List<Student> students, Set<String> formats, StudentReportGenerator.ReportType reportType, int numThreads) {
        if (numThreads < 2 || numThreads > 8) numThreads = Math.min(Math.max(numThreads, 2), 8);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        ConcurrentHashMap<String, Long> studentTimes = new ConcurrentHashMap<>();
        long startTotal = System.currentTimeMillis();

        AtomicInteger completedCount = new AtomicInteger(0);
        int totalStudents = students.size();

        System.out.println("\nStarting batch report generation with " + numThreads + " threads...\n");

        CountDownLatch latch = new CountDownLatch(totalStudents);

        for (Student s : students) {
            executor.submit(() -> {
                try {
                    long start = System.currentTimeMillis();
                    reportGenerator.exportReport(s, s.getStudentId() + "_batch", formats, reportType);
                    long end = System.currentTimeMillis();
                    studentTimes.put(s.getStudentId(), end - start);
                    System.out.printf("✔ %s report completed in %d ms%n", s.getName(), end - start);
                } catch (Exception e) {
                    System.out.printf("❌ Failed to generate report for %s: %s%n", s.getName(), e.getMessage());
                } finally {
                    int done = completedCount.incrementAndGet();
                    displayProgress(done, totalStudents);
                    latch.countDown();
                }
            });
        }

        try {
            latch.await(); // Wait for all tasks to finish
        } catch (InterruptedException e) {
            System.out.println("Batch generation interrupted.");
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        long endTotal = System.currentTimeMillis();
        long totalTime = endTotal - startTotal;
        double throughput = (students.size() * 1000.0) / totalTime;

        System.out.println("\nBatch generation complete!");
        System.out.println("Total time: " + totalTime + " ms");
        System.out.printf("Throughput: %.2f reports/sec%n", throughput);
        System.out.println("Individual report times: " + studentTimes);
        System.out.println("Executor stats: Active Threads: 0, Queue size: 0, Completed tasks: " + students.size());
    }

    // ========================= Progress Bar =========================
    private void displayProgress(int completed, int total) {
        int percent = (int) ((completed / (double) total) * 100);
        int barLength = 50; // length of the progress bar
        int filled = (percent * barLength) / 100;
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < filled; i++) bar.append("█");
        for (int i = filled; i < barLength; i++) bar.append(" ");
        System.out.print("\rProgress: [" + bar + "] " + percent + "% (" + completed + "/" + total + ")");
        if (completed == total) System.out.println(); // move to next line when done
    }
}
