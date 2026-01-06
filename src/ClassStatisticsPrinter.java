import Statistics.CachedClassStatistics;

import java.util.Map;

public class ClassStatisticsPrinter {

    private final ClassStatisticsCalculator calculator;

    public ClassStatisticsPrinter(ClassStatisticsCalculator calculator) {
        this.calculator = calculator;
    }

    public void print() {
        // Fetch statistics from cache (or compute if not cached)
        CachedClassStatistics stats = calculator.calculateClassStatistics();

        System.out.println("\nCLASS STATISTICS");
        System.out.println("────────────────────────────────────────");

        if (stats.getTotalGrades() == 0) {
            System.out.println("No grades recorded.");
            return;
        }

        System.out.println("Total Students: " + stats.getTotalStudents());
        System.out.println("Total Grades:   " + stats.getTotalGrades());

        printGradeDistribution(stats.getGradeDistribution());
        printStatisticalAnalysis(stats);
        printSubjectPerformance(stats.getSubjectAverages());
        printStudentTypeComparison(stats.getStudentTypeAverages());

        System.out.println("\nPress Enter to continue...");
        new java.util.Scanner(System.in).nextLine();
    }

    private void printGradeDistribution(Map<String, Integer> dist) {
        System.out.println("\nGRADE DISTRIBUTION");
        System.out.println("────────────────────────────────────────");

        String[] labels = {"A (90–100)", "B (80–89)", "C (70–79)", "D (60–69)", "F (0–59)"};
        int total = dist.values().stream().mapToInt(Integer::intValue).sum();

        for (int i = 0; i < labels.length; i++) {
            String key = labels[i].substring(0, 1); // "A", "B", ...
            int count = dist.getOrDefault(key, 0);
            double percent = total == 0 ? 0 : (double) count / total * 100;
            System.out.printf("%-12s %5.1f%% (%d grades)%n", labels[i], percent, count);
        }
    }

    private void printStatisticalAnalysis(CachedClassStatistics stats) {
        System.out.println("\nSTATISTICAL ANALYSIS");
        System.out.println("────────────────────────────────────────");

        System.out.printf("Mean:   %.1f%%%n", stats.getMean());
        System.out.printf("Median: %.1f%%%n", stats.getMedian());
        System.out.printf("Mode:   %.1f%%%n", stats.getMode());
        System.out.printf("StdDev: %.1f%%%n", stats.getStdDev());

        System.out.println("Highest: " + stats.getHighestGrade() +
                "% (" + stats.getHighestSubject() + ")");
        System.out.println("Lowest:  " + stats.getLowestGrade() +
                "% (" + stats.getLowestSubject() + ")");
    }

    private void printSubjectPerformance(Map<String, Double> subjectAvg) {
        System.out.println("\nSUBJECT PERFORMANCE");
        System.out.println("────────────────────────────────────────");

        for (Map.Entry<String, Double> e : subjectAvg.entrySet()) {
            System.out.printf("%-15s %.1f%%%n", e.getKey() + ":", e.getValue());
        }
    }

    private void printStudentTypeComparison(Map<String, Double> typeAvg) {
        System.out.println("\nSTUDENT TYPE COMPARISON");
        System.out.println("────────────────────────────────────────");

        System.out.printf("Regular: %.1f%%%n", typeAvg.getOrDefault("Regular", 0.0));
        System.out.printf("Honors:  %.1f%%%n", typeAvg.getOrDefault("Honors", 0.0));
    }
}
