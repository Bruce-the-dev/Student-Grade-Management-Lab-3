import Exceptions.GpaErrorException;
import Exceptions.StudentNotFoundException;

import java.util.*;

/**
 * GPA Calculator (US-1 compliant)
 * - TreeMap used for GPA ranking
 * - Custom Comparator for multi-criteria sorting
 * - Preserves all original methods and signatures
 */
public class GpaCalculator {
    private final GradeManager gradeManager;

    public GpaCalculator(GradeManager gradeManager) {
        this.gradeManager = gradeManager;
    }

    // 1. Convert % grade → GPA scale
    public double convertToGPA(double percentage) {
        if (percentage < 0 || percentage > 100)
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        if (percentage >= 93) return 4.0;
        if (percentage >= 90) return 3.7;
        if (percentage >= 87) return 3.3;
        if (percentage >= 83) return 3.0;
        if (percentage >= 80) return 2.7;
        if (percentage >= 77) return 2.3;
        if (percentage >= 73) return 2.0;
        if (percentage >= 70) return 1.7;
        if (percentage >= 67) return 1.3;
        if (percentage >= 60) return 1.0;
        return 0.0;
    }

    // 3. Convert GPA → letter grade (A, B, C...)
    public String getLetterGrade(double gpa) {
        if (gpa < 0 || gpa > 4) throw new IllegalArgumentException("GPA must be between 0.0 and 4.0");
        if (gpa == 4.0) return "A";
        if (gpa >= 3.7) return "A-";
        if (gpa >= 3.3) return "B+";
        if (gpa >= 3.0) return "B";
        if (gpa >= 2.7) return "B-";
        if (gpa >= 2.3) return "C+";
        if (gpa >= 2.0) return "C";
        if (gpa >= 1.7) return "C-";
        if (gpa >= 1.3) return "D+";
        if (gpa >= 1.0) return "D";
        return "F";
    }

    // Calculate GPA for a student
    public double calculateGPA(String studentId) throws GpaErrorException {
        Grade[] grades = gradeManager.getGradesForStudent(studentId);
        if (grades.length == 0) {
            throw new GpaErrorException("The student has no grades");
        }

        double totalGpa = 0;
        for (Grade g : grades) {
            totalGpa += convertToGPA(g.getGrade());
        }

        return totalGpa / grades.length;
    }

    // Display GPA report
    public void displayGPAReport(String studentId) throws GpaErrorException {
        List<Grade> gradeList = new ArrayList<>(Arrays.asList(gradeManager.getGradesForStudent(studentId)));

        if (gradeList.isEmpty()) {
            throw new GpaErrorException("The student has no grades");
        }

        System.out.println("\nGPA REPORT");
        System.out.println("-----------------------------------------------");
        System.out.printf("%-15s | %-7s | %-10s%n", "Subject", "Grade", "GPA Points");
        System.out.println("-----------------------------------------------");

        for (Grade g : gradeList) {
            double gpa = convertToGPA(g.getGrade());
            System.out.printf("%-15s | %-7s | %.1f (%s)%n",
                    g.getSubject().getSubjectName(),
                    String.format("%.0f%%", g.getGrade()),
                    gpa,
                    getLetterGrade(gpa)
            );
        }

        double finalGpa = calculateGPA(studentId);
        System.out.println("-------------------------------------------------------------");
        System.out.printf("GPA: %.2f%n", finalGpa);
        System.out.println("Letter Grade: " + getLetterGrade(finalGpa));
    }

    /**
     * Build GPA ranking for all students using TreeMap
     * Keys = GPA (descending), Values = list of students
     * Time Complexity: O(n log n)
     */
    public TreeMap<Double, List<Student>> getGpaRanking(StudentManager studentManager) {
        TreeMap<Double, List<Student>> ranking = new TreeMap<>(Collections.reverseOrder());
        int totalStudents = studentManager.getStudentCount();

        for (int i = 0; i < totalStudents; i++) {
            Student s = studentManager.getStudentByIndex(i);
            double avg = gradeManager.calculateOverallAverage(s.getStudentId());
            double gpa = avg > 0 ? convertToGPA(avg) : 0.0;

            ranking.computeIfAbsent(gpa, k -> new ArrayList<>()).add(s);
        }

        return ranking;
    }

    /**
     * Get rank of a student in class using Comparator-based sorting
     * Criteria: GPA descending, Name ascending, ID ascending
     */
    public int getRankInClass(String studentId, StudentManager studentManager)
            throws GpaErrorException, StudentNotFoundException {

        int totalStudents = studentManager.getStudentCount();
        List<Student> students = new ArrayList<>();

        for (int i = 0; i < totalStudents; i++) {
            students.add(studentManager.getStudentByIndex(i));
        }

        // Custom comparator
        Comparator<Student> comparator = (s1, s2) -> {
            double gpa1 = convertToGPA(gradeManager.calculateOverallAverage(s1.getStudentId()));
            double gpa2 = convertToGPA(gradeManager.calculateOverallAverage(s2.getStudentId()));
            int cmp = Double.compare(gpa2, gpa1); // GPA descending
            if (cmp != 0) return cmp;
            cmp = s1.getName().compareToIgnoreCase(s2.getName()); // Name ascending
            if (cmp != 0) return cmp;
            return s1.getStudentId().compareTo(s2.getStudentId()); // ID ascending
        };

        students.sort(comparator);

        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId().equals(studentId)) {
                return i + 1; // Rank is 1-based
            }
        }

        throw new StudentNotFoundException("Student ID not found: " + studentId);
    }
}
