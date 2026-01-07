import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import Exceptions.GpaErrorException;
import Exceptions.StudentNotFoundException;

/**
 * StudentAnalytics
 *
 * - Performs stream-based operations on students and grades
 * - Honors eligibility checked using HonorsStudent#isHonorsEligible
 * - Sequential and parallel execution supported for performance comparison
 */
public class StudentAnalytics {

    private final StudentManager studentManager;
    private final GradeManager gradeManager;
    private final GpaCalculator gpaCalculator;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StudentAnalytics(StudentManager studentManager,
                            GradeManager gradeManager,
                            GpaCalculator gpaCalculator) {
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
        this.gpaCalculator = gpaCalculator;
    }

    public List<Student> findHonorsStudents() {
        return Arrays.stream(studentManager.getAllStudents())
                .filter(s -> s instanceof HonorsStudent)
                .filter(s -> ((HonorsStudent) s).isHonorsEligible())
                .collect(Collectors.toList());
    }

    public List<Student> topNStudents(int n) {
        return Arrays.stream(studentManager.getAllStudents())
                .sorted((s1, s2) -> {
                    try {
                        return Double.compare(
                                gpaCalculator.calculateGPA(s2.getStudentId()),
                                gpaCalculator.calculateGPA(s1.getStudentId())
                        );
                    } catch (GpaErrorException e) {
                        return 0; // treat students with no grades as equal
                    }
                })
                .limit(n)
                .collect(Collectors.toList());
    }

    public Map<String, List<Student>> groupByGradeRange() {
        return Arrays.stream(studentManager.getAllStudents())
                .collect(Collectors.groupingBy(s -> {
                    double avg = gradeManager.calculateOverallAverage(s.getStudentId());
                    if (avg >= 90) return "90-100";
                    if (avg >= 80) return "80-89";
                    if (avg >= 70) return "70-79";
                    if (avg >= 60) return "60-69";
                    return "<60";
                }));
    }

    public Set<String> extractUniqueCourseCodes() {
        return Arrays.stream(studentManager.getAllStudents())
                .flatMap(s -> Arrays.stream(gradeManager.getGradesForStudent(s.getStudentId()))
                        .map(g -> g.getSubject().getSubjectName())
                )
                .collect(Collectors.toSet());
    }

    public Map<String, Double> averageGradePerSubject() {
        Map<String, List<Double>> subjectGrades = new HashMap<>();

        for (Student s : studentManager.getAllStudents()) {
            Grade[] grades = gradeManager.getGradesForStudent(s.getStudentId());
            for (Grade g : grades) {
                subjectGrades.computeIfAbsent(g.getSubject().getSubjectName(), k -> new ArrayList<>())
                        .add(g.getGrade());
            }
        }

        Map<String, Double> avgGrades = new HashMap<>();
        subjectGrades.forEach((subject, gradesList) -> {
            double avg = gradesList.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            avgGrades.put(subject, avg);
        });

        return avgGrades;
    }
public long measureExecutionTime(boolean parallel) {
        List<Student> students = Arrays.asList(studentManager.getAllStudents());

        long start = System.currentTimeMillis();

        Stream<Student> stream = parallel ? students.parallelStream() : students.stream();

        // Example operation: count honors-eligible students
        long count = stream
                .filter(s -> s instanceof HonorsStudent)
                .filter(s -> ((HonorsStudent) s).isHonorsEligible())
                .count();

        return System.currentTimeMillis() - start;
    }
    /**
     * Query the grade history of a student in chronological order.
     */
    public List<String> queryGradeHistory(String studentId) throws StudentNotFoundException {
        // Verify student exists
        Student student = studentManager.findStudent(studentId);

        return Arrays.stream(gradeManager.getGradesForStudent(studentId))
                .sorted(Comparator.comparing(Grade::getTimestamp)) // chronological
                .map(g -> String.format("%s (%s): %.2f [%s]",
                        g.getSubject().getSubjectName(),
                        g.getSubject().getSubjectCode(),
                        g.getGrade(),
                        g.getTimestamp().format(FORMATTER)))
                .collect(Collectors.toList());
    }

}
