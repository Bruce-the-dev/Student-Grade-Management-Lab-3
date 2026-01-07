import Exceptions.InvalidGradeException;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a single grade record assigned to a student.
 * Immutable fields: gradeId, studentId, subject, date.
 * Grade value can be updated but remains validated.
 */
public class Grade implements Gradable, Serializable {
private static final long serialVersionUID = 1L;
    private final LocalDateTime addedTime;
    private static int gradeCounter;

    private final String gradeId;
    private final String studentId;
    private final Subject subject;
    private final LocalDate date;

    private double grade;

    public Grade( String studentId, Subject subject, double grade) throws InvalidGradeException {
        this.addedTime = LocalDateTime.now();
        gradeCounter++;
        this.gradeId = "GRD" + String.format("%03d", gradeCounter);

        if (!validateGrade(grade)) {
            throw new InvalidGradeException("Grade must be between 0 and 100");
        }

        this.grade = grade;
        this.studentId = studentId;
        this.subject = subject;
        this.date = LocalDate.now();
    }

    // ----- Display -----
    public void displayGradeDetails() {
        System.out.printf("%-8s | %-12s | %-15s | %-10s | %-6.1f%%%n",
                gradeId,
                date.toString(),
                subject.getSubjectName(),
                subject.getSubjectType(),
                grade
        );
    }

    // ----- Getters -----
    public String getGradeId() {
        return gradeId;
    }

    public String getStudentId() {
        return studentId;
    }

    public Subject getSubject() {
        return subject;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getGrade() {
        return grade;
    }


    @Override
    public boolean recordGrade(double grade) {
        if (validateGrade(grade)) {
            this.grade = grade;
            return true;
        }
        return false;
    }
    public LocalDateTime getTimestamp() {
        return addedTime;
    }
    @Override
    public boolean validateGrade(double grade) {
        return grade >= 0 && grade <= 100;
    }

    // ----- Equals + HashCode (for HashSet / Map keys) -----
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Grade that)) return false;
        return gradeId.equals(that.gradeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gradeId);
    }
}
