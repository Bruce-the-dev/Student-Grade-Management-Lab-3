import Audit.AuditLogger;
import Audit.OperationType;
import Caching.CacheManager;
import Exceptions.InvalidGradeException;
import java.util.*;

/**
 * GradeManager
 *
 * - LinkedList for grade history
 * - HashMap<String, LinkedList<Grade>> for fast student access
 * - HashSet for unique course tracking
 *
 */
public class GradeManager {

    private final LinkedList<Grade> grades = new LinkedList<>();

    private final HashMap<String, LinkedList<Grade>> gradeMap =
            new HashMap<>();

    private final HashMap<String, HashSet<String>> courseMap =
            new HashMap<>();

    private final CacheManager<String, Object> cache;
    private final AuditLogger auditLogger;

    public GradeManager(CacheManager<String, Object> cache, AuditLogger auditLogger) {
        this.cache = cache;
        this.auditLogger = auditLogger;
    }

    /**
     * Adds a grade.
     * Time Complexity: O(1)
     */
    public void addGrade(Grade grade) throws InvalidGradeException {
        long start = System.currentTimeMillis();
        boolean success = false;

        try {

            if (!grade.validateGrade(grade.getGrade())) {
                throw new InvalidGradeException("Grade must be between 0 and 100.");
            }

            grades.add(grade);

            gradeMap
                    .computeIfAbsent(grade.getStudentId(), k -> new LinkedList<>())
                    .add(grade);

            courseMap
                    .computeIfAbsent(grade.getStudentId(), k -> new HashSet<>())
                    .add(grade.getSubject().getSubjectName());

            success = true; // operation succeeded
        } finally {
            long execTime = System.currentTimeMillis() - start;

            auditLogger.log(
                    OperationType.RECORD_GRADE,
                    "Added grade for student " + grade.getStudentId() +
                            ", subject " + grade.getSubject().getSubjectName() +
                            ", score " + grade.getGrade(),
                    execTime,
                    success
            );
        }
    }


    /**
     * Views grades by student.
     * Time Complexity: O(n)
     */
    public void viewGradesByStudent(String studentId) {

        boolean found = false;
        System.out.println("\n=== Grades for Student: " + studentId + " ===");

        ListIterator<Grade> it =
                grades.listIterator(grades.size());

        while (it.hasPrevious()) {
            Grade g = it.previous();
            if (g.getStudentId().equals(studentId)) {
                found = true;
                g.displayGradeDetails();
            }
        }

        if (!found) {
            System.out.println("No grades recorded for this student.");
        }
    }

    /**
     * Core average.
     * Time Complexity: O(k)
     */
    public double calculateCoreAverage(String studentId) {

        double sum = 0;
        int count = 0;

        LinkedList<Grade> list = gradeMap.get(studentId);
        if (list == null) return 0;

        for (Grade g : list) {
            if (g.getSubject() instanceof CoreSubject) {
                sum += g.getGrade();
                count++;
            }
        }

        return (count == 0) ? 0 : sum / count;
    }

    /**
     * Elective average.
     * Time Complexity: O(k)
     */
    public double calculateElectiveAverage(String studentId) {

        double sum = 0;
        int count = 0;

        LinkedList<Grade> list = gradeMap.get(studentId);
        if (list == null) return 0;

        for (Grade g : list) {
            if (g.getSubject().getSubjectType().equals("Elective")) {
                sum += g.getGrade();
                count++;
            }
        }

        return (count == 0) ? 0 : sum / count;
    }

    /**
     * Overall average.
     * Time Complexity: O(k)
     */
    public double calculateOverallAverage(String studentId) {

        LinkedList<Grade> list = gradeMap.get(studentId);
        if (list == null || list.isEmpty()) return 0;

        double sum = 0;
        for (Grade g : list) {
            sum += g.getGrade();
        }
        return sum / list.size();
    }

    /**
     * Grade count.
     * Time Complexity: O(1)
     */
    public int getGradeCount(String studentId) {
        LinkedList<Grade> list = gradeMap.get(studentId);
        return list == null ? 0 : list.size();
    }

    /**
     * Grades for student (newest first).
     * Time Complexity: O(k)
     */
    public Grade[] getGradesForStudent(String studentId) {
        String key = "GRADES_"+studentId;
        Object cached = cache.get(key);
        if (cached != null) {
            return (Grade[]) cached;
        }
        LinkedList<Grade> list = gradeMap.get(studentId);
        if (list == null) return new Grade[0];

        LinkedList<Grade> reversed = new LinkedList<>(list);
        Collections.reverse(reversed);

        Grade[] arr = reversed.toArray(new Grade[0]);

        cache.put(key, arr);
        return arr;
    }

    /**
     * Total grades stored.
     * Time Complexity: O(1)
     */
    public int getTotalGradeCount() {
        return grades.size();
    }

    /**
     * Grade by index.
     * Time Complexity: O(1)
     */
    public Grade getGradeAt(int index) {
        if (index >= 0 && index < grades.size()) {
            return grades.get(index);
        }
        return null;
    }

    // ================= CLASS-WIDE AVERAGE =================
    public double calculateOverallClassAverage() {

        if (grades.isEmpty()) return 0;

        double sum = 0;

        for (Grade g : grades) {
            sum += g.getGrade();
        }

        return sum / grades.size();
    }

}
