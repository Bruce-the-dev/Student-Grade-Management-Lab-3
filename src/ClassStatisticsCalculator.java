import Caching.CacheManager;
import Statistics.CachedClassStatistics;

import java.util.*;

public class ClassStatisticsCalculator {
    private static final String CLASS_STATS_KEY = "CLASS_STATISTICS";

    private final GradeManager gradeManager;
    private final StudentManager studentManager;
    private final CacheManager<String, CachedClassStatistics> cache;


    public ClassStatisticsCalculator(GradeManager gradeManager, StudentManager studentManager, CacheManager<String, CachedClassStatistics> cache) {
        this.gradeManager = gradeManager;
        this.studentManager = studentManager;
        this.cache = cache;
    }

    public CachedClassStatistics computeStatistics() {
        List<Grade> allGrades = getAllGrades();

        int totalStudents = studentManager.getStudentCount();
        int totalGrades = allGrades.size();

        Map<String, Integer> gradeDist = computeGradeDistribution(allGrades);
        double mean = calculateMean(allGrades);
        double median = calculateMedian(allGrades);
        double mode = calculateMode(allGrades);
        double stdDev = calculateStdDev(allGrades, mean);
        Map<String, Double> subjectAvg = computeSubjectAverages(allGrades);
        Map<String, Double> studentTypeAvg = computeStudentTypeAverages();
        double highestGrade = 0;
        String highestSubject = "";
        double lowestGrade = 0;
        String lowestSubject = "";
        if (!allGrades.isEmpty()) {
            Grade highest = allGrades.get(0);
            Grade lowest = allGrades.get(0);
            for (Grade g : allGrades) {
                if (g.getGrade() > highest.getGrade()) highest = g;
                if (g.getGrade() < lowest.getGrade()) lowest = g;
            }
            highestGrade = highest.getGrade();
            highestSubject = highest.getSubject().getSubjectName();
            lowestGrade = lowest.getGrade();
            lowestSubject = lowest.getSubject().getSubjectName();
        }

        return new CachedClassStatistics(
                totalStudents,
                totalGrades,
                gradeDist,
                mean,
                median,
                mode,
                stdDev,
                subjectAvg,
                studentTypeAvg,
                highestGrade,
                highestSubject,
                lowestGrade,
                lowestSubject
        );
    }

    private List<Grade> getAllGrades() {
        List<Grade> grades = new ArrayList<>();
        for (int i = 0; i < gradeManager.getTotalGradeCount(); i++) {
            Grade g = gradeManager.getGradeAt(i);
            if (g != null) grades.add(g);
        }
        return grades;
    }

    private Map<String, Integer> computeGradeDistribution(List<Grade> grades) {
        Map<String, Integer> dist = new LinkedHashMap<>();
        dist.put("A", 0); dist.put("B", 0); dist.put("C", 0); dist.put("D", 0); dist.put("F", 0);

        for (Grade g : grades) {
            double score = g.getGrade();
            if (score >= 90) dist.put("A", dist.get("A") + 1);
            else if (score >= 80) dist.put("B", dist.get("B") + 1);
            else if (score >= 70) dist.put("C", dist.get("C") + 1);
            else if (score >= 60) dist.put("D", dist.get("D") + 1);
            else dist.put("F", dist.get("F") + 1);
        }
        return dist;
    }

    private double calculateMean(List<Grade> grades) {
        if (grades.isEmpty()) return 0;
        double sum = 0;
        for (Grade g : grades) sum += g.getGrade();
        return sum / grades.size();
    }

    private double calculateMedian(List<Grade> grades) {
        List<Double> values = new ArrayList<>();
        for (Grade g : grades) values.add(g.getGrade());
        Collections.sort(values);
        int n = values.size();
        return (n % 2 == 0) ? (values.get(n / 2 - 1) + values.get(n / 2)) / 2 : values.get(n / 2);
    }

    private double calculateMode(List<Grade> grades) {
        Map<Double, Integer> freq = new HashMap<>();
        for (Grade g : grades) freq.put(g.getGrade(), freq.getOrDefault(g.getGrade(), 0) + 1);

        double mode = 0;
        int maxCount = 0;
        for (Map.Entry<Double, Integer> e : freq.entrySet()) {
            if (e.getValue() > maxCount) {
                maxCount = e.getValue();
                mode = e.getKey();
            }
        }
        return mode;
    }

    private double calculateStdDev(List<Grade> grades, double mean) {
        if (grades.isEmpty()) return 0;
        double sum = 0;
        for (Grade g : grades) sum += Math.pow(g.getGrade() - mean, 2);
        return Math.sqrt(sum / grades.size());
    }

    private Map<String, Double> computeSubjectAverages(List<Grade> grades) {
        Map<String, List<Double>> subjectMap = new HashMap<>();
        for (Grade g : grades) {
            subjectMap.computeIfAbsent(g.getSubject().getSubjectName(), k -> new ArrayList<>()).add(g.getGrade());
        }
        Map<String, Double> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<Double>> e : subjectMap.entrySet()) {
            double avg = e.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
            result.put(e.getKey(), avg);
        }
        return result;
    }

    private Map<String, Double> computeStudentTypeAverages() {
        Map<String, Double> result = new HashMap<>();
        double regSum = 0, regCount = 0, honSum = 0, honCount = 0;
        for (Student s : studentManager.getAllStudents()) {
            double avg = gradeManager.calculateOverallAverage(s.getStudentId());
            if (s.getStudentType().equalsIgnoreCase("Regular")) { regSum += avg; regCount++; }
            else if (s.getStudentType().equalsIgnoreCase("Honors")) { honSum += avg; honCount++; }
        }
        result.put("Regular", regCount == 0 ? 0 : regSum / regCount);
        result.put("Honors", honCount == 0 ? 0 : honSum / honCount);
        return result;
    }
    public CachedClassStatistics calculateClassStatistics() {
        CachedClassStatistics stats = cache.get(CLASS_STATS_KEY);
        if (stats == null) {
            stats = computeStatistics();
            cache.put(CLASS_STATS_KEY, stats);
        }

        return stats;
    }

}
