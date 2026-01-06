package Statistics;

import java.util.Map;

public class CachedClassStatistics {

    private final int totalStudents;
    private final int totalGrades;
    private final Map<String, Integer> gradeDistribution;
    private final double mean;
    private final double median;
    private final double mode;
    private final double stdDev;
    private final Map<String, Double> subjectAverages;
    private final Map<String, Double> studentTypeAverages;

    // NEW: store highest and lowest grades with subjects
    private final double highestGrade;
    private final String highestSubject;
    private final double lowestGrade;
    private final String lowestSubject;

    public CachedClassStatistics(
            int totalStudents,
            int totalGrades,
            Map<String, Integer> gradeDistribution,
            double mean,
            double median,
            double mode,
            double stdDev,
            Map<String, Double> subjectAverages,
            Map<String, Double> studentTypeAverages,
            double highestGrade,
            String highestSubject,
            double lowestGrade,
            String lowestSubject
    ) {
        this.totalStudents = totalStudents;
        this.totalGrades = totalGrades;
        this.gradeDistribution = gradeDistribution;
        this.mean = mean;
        this.median = median;
        this.mode = mode;
        this.stdDev = stdDev;
        this.subjectAverages = subjectAverages;
        this.studentTypeAverages = studentTypeAverages;
        this.highestGrade = highestGrade;
        this.highestSubject = highestSubject;
        this.lowestGrade = lowestGrade;
        this.lowestSubject = lowestSubject;
    }

    // Getters
    public int getTotalStudents() { return totalStudents; }
    public int getTotalGrades() { return totalGrades; }
    public Map<String, Integer> getGradeDistribution() { return gradeDistribution; }
    public double getMean() { return mean; }
    public double getMedian() { return median; }
    public double getMode() { return mode; }
    public double getStdDev() { return stdDev; }
    public Map<String, Double> getSubjectAverages() { return subjectAverages; }
    public Map<String, Double> getStudentTypeAverages() { return studentTypeAverages; }
    public double getHighestGrade() { return highestGrade; }
    public String getHighestSubject() { return highestSubject; }
    public double getLowestGrade() { return lowestGrade; }
    public String getLowestSubject() { return lowestSubject; }
}
