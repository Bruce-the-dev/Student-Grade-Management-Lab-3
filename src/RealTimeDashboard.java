import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class RealTimeDashboard {

    private final StudentManager studentManager;
    private final GradeManager gradeManager;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicBoolean paused = new AtomicBoolean(false);

    private final int refreshInterval = 5; // seconds
    private final Scanner scanner = new Scanner(System.in);

    public RealTimeDashboard(StudentManager studentManager, GradeManager gradeManager) {
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
        GpaCalculator gpaCalculator = new GpaCalculator(gradeManager);
    }

    public void start() {
        Runnable updateTask = () -> {
            if (paused.get()) return;
            clearConsole();
            displayDashboard();
        };

        scheduler.scheduleAtFixedRate(updateTask, 0, refreshInterval, TimeUnit.SECONDS);

        while (running.get()) {
            System.out.println("\nCommand: (Q=Quit | R=Refresh | P=Pause/Resume)");
            String cmd = scanner.nextLine().trim().toUpperCase();
            switch (cmd) {
                case "Q":
                    running.set(false);
                    scheduler.shutdownNow();
                    System.out.println("Exiting dashboard...");
                    break;
                case "R":
                    displayDashboard();
                    break;
                case "P":
                    paused.set(!paused.get());
                    System.out.println(paused.get() ? "Dashboard paused." : "Dashboard resumed.");
                    break;
                default:
                    System.out.println("Unknown command.");
            }
        }
    }

    private void displayDashboard() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("REAL-TIME STATISTICS DASHBOARD");
        System.out.println("Auto-refresh: Enabled (" + refreshInterval + " sec) | Thread: RUNNING");
        System.out.println("Last Updated: " + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("\nSYSTEM STATUS");

        int totalStudents = studentManager.getStudentCount();
        int activeThreads = Thread.activeCount();

        System.out.println("Total Students: " + totalStudents);
        System.out.println("Active Threads: " + activeThreads);

        // LIVE STATISTICS
        System.out.println("\nLIVE STATISTICS");
        int totalGrades = 0;
        double classSum = 0;

        List<Student> allStudents = new ArrayList<>();
        for (int i = 0; i < totalStudents; i++) {
            Student s = studentManager.getStudentByIndex(i);
            allStudents.add(s);
            totalGrades += gradeManager.getGradeCount(s.getStudentId());
            classSum += gradeManager.calculateOverallAverage(s.getStudentId());
        }

        double classAverage = totalStudents > 0 ? classSum / totalStudents : 0;
        System.out.println("Total Grades: " + totalGrades);
        System.out.printf("Class Average: %.2f%%%n", classAverage);

        // Top performers (by average)
        System.out.println("\nTop Performers:");
        allStudents.sort((s1, s2) -> Double.compare(
                gradeManager.calculateOverallAverage(s2.getStudentId()),
                gradeManager.calculateOverallAverage(s1.getStudentId())
        ));

        int limit = Math.min(5, allStudents.size());
        for (int i = 0; i < limit; i++) {
            Student s = allStudents.get(i);
            double avg = gradeManager.calculateOverallAverage(s.getStudentId());
            System.out.printf("%d. %s - %.2f%%%n", i + 1, s.getName(), avg);
        }

        // Concurrent Operations (simulated)
        System.out.println("\nConcurrent Operations In Progress:");
        System.out.println("Batch Report Generation (simulated) - " + new Random().nextInt(101) + "%");
        System.out.println("Statistics Calculation - RUNNING");
        System.out.println("Cache Refresh - " + new Random().nextInt(101) + "%");
    }

    private void clearConsole() {
        // ANSI escape codes to clear the console
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
