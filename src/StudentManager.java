import Audit.AuditLogger;
import Caching.CacheManager;
import Exceptions.StudentNotFoundException;
import java.util.*;

import static Audit.OperationType.ADD_STUDENT;

/**
 * StudentManager
 * <p>
 * Optimizations (US-1):
 * - HashMap<String, Student> for O(1) lookup by ID
 * - ArrayList<Student> preserved for insertion order
 * <p>
 * No public methods removed or changed.
 */
public class StudentManager {

    // O(1) lookup
    private final HashMap<String, Student> studentMap = new HashMap<>();

    // Preserves insertion order (original behavior)
    private final ArrayList<Student> students = new ArrayList<>();
    private final CacheManager<String, Object> cache;
    private final AuditLogger auditLogger;

    public StudentManager(CacheManager<String, Object> cache, AuditLogger auditLogger) {
        this.cache = cache;
        this.auditLogger = auditLogger;
    }

    /**
     * Adds a student.
     * Time Complexity: O(1)
     */
    public void addStudent(Student student) {
        long start = System.currentTimeMillis();
        boolean success = false;

        try {

            students.add(student);
            studentMap.put(student.getStudentId(), student);
            success = true;
            System.out.println("Student added successfully!");
        } finally {
            long execTime = System.currentTimeMillis() - start;

            auditLogger.log(
                    ADD_STUDENT,
                    "Added student " + student.getStudentId(),
                    execTime,
                    success
            );
        }
    }


    /**
     * Finds student by ID.
     * Time Complexity: O(1)
     */
    public Student findStudent(String studentId)
            throws StudentNotFoundException {

        String key = "STUDENT_" + studentId;
        Object cached = cache.get(key);
        if (cached != null) return (Student) cached;

        Student s = studentMap.get(studentId);
        if (s != null) {
            cache.put(key,s);
            return s;
        }

        throw new StudentNotFoundException(
                "Student with ID " + studentId + " doesn't exist."
        );
    }

    /**
     * Returns number of students.
     * Time Complexity: O(1)
     */
    public int getStudentCount() {
        return students.size();
    }

    /**
     * Returns student by index.
     * Time Complexity: O(1)
     */
    public Student getStudentByIndex(int index) {
        if (index >= 0 && index < students.size()) {
            return students.get(index);
        }
        return null;
    }

    /**
     * Calculates class average.
     * Time Complexity: O(n)
     */
    public double getAverageClassGrade(GradeManager gradeManager) {
        double total = 0;
        int count = 0;

        for (Student s : students) {
            double avg =
                    gradeManager.calculateOverallAverage(s.getStudentId());

            if (gradeManager.getGradeCount(s.getStudentId()) > 0) {
                total += avg;
                count++;
            }
        }

        return (count == 0) ? 0 : total / count;
    }

    /**
     * Searches students by name.
     * Time Complexity: O(n)
     */
    public Student[] findStudentByName(String name) {
        name = name.toLowerCase();
        List<Student> results = new ArrayList<>();

        for (Student s : students) {
            if (s.getName().toLowerCase().contains(name)) {
                results.add(s);
            }
        }

        return results.toArray(new Student[0]);
    }

    /**
     * Searches students by grade range.
     * Time Complexity: O(n)
     */
    public Student[] searchByGradeRange(
            double min, double max, GradeManager gradeManager) {

        List<Student> results = new ArrayList<>();

        for (Student s : students) {
            double avg =
                    gradeManager.calculateOverallAverage(s.getStudentId());

            if (avg >= min && avg <= max) {
                results.add(s);
            }
        }

        return results.toArray(new Student[0]);
    }

    /**
     * Searches students by type.
     * Time Complexity: O(n)
     */
    public Student[] searchByStudentType(String type) {
        type = type.toLowerCase();
        List<Student> results = new ArrayList<>();

        for (Student s : students) {
            if (s.getStudentType().toLowerCase().equals(type)) {
                results.add(s);
            }
        }

        return results.toArray(new Student[0]);
    }

    /**
     * Returns all students.
     * Time Complexity: O(n)
     */
    public Student[] getAllStudents() {
        return students.toArray(new Student[0]);
    }
}
