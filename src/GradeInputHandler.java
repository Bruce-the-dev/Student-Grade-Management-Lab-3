import Exceptions.InvalidGradeException;
import Exceptions.LoggerHandler;
import Exceptions.StudentNotFoundException;
import Exceptions.ValidationException;
import Validations.ValidationUtils;

import java.util.Scanner;

public class GradeInputHandler {

    private final GradeManager gradeManager;
    private final StudentManager studentManager;
    private final Scanner scanner;

    public GradeInputHandler(GradeManager gradeManager, StudentManager studentManager, Scanner scanner) {
        this.gradeManager = gradeManager;
        this.studentManager = studentManager;
        this.scanner = scanner;
    }

    public void addGradeMenu() throws InvalidGradeException {
        System.out.println("\nRECORD GRADE");
        System.out.println("═══════════════════════════════════════════════════");

        scanner.nextLine(); // consume leftover newline

        // -------- Student ID --------
        String studentId;
        Student student;
        while (true) {
            System.out.print("Enter Student ID (e.g., STU001): ");
            studentId = scanner.nextLine().trim();
            try {
                ValidationUtils.validateStudentId(studentId);
                student = studentManager.findStudent(studentId);
                break;
            } catch (ValidationException ve) {
                System.out.println("❌ " + ve.getMessage());
            } catch (StudentNotFoundException snf) {
                System.out.println("❌ Student not found: " + studentId);
            }
        }

        System.out.println("\nStudent Found:");
        student.displayStudentDetails();
        System.out.println("───────────────────────────────────────────────────");

        // -------- Subject Selection --------
        Subject subject = selectSubject();
        if (subject == null) return;

        // -------- Grade Input --------
        double gradeValue;
        while (true) {
            System.out.print("\nEnter grade (0-100): ");
            String input = scanner.nextLine().trim();
            try {
                ValidationUtils.validateGrade(input);
                gradeValue = Double.parseDouble(input);
                break;
            } catch (ValidationException ve) {
                System.out.println("❌ " + ve.getMessage());
            } catch (NumberFormatException nfe) {
                System.out.println("❌ Invalid number format.");
            }
        }

        // -------- Confirmation --------
        Grade grade = new Grade(studentId, subject, gradeValue);
        System.out.println("\nGRADE CONFIRMATION");
        System.out.println("---------------------");
        System.out.println("GradeID: " + grade.getGradeId());
        System.out.println("Student: " + student.getName());
        System.out.println("Subject: " + subject.getSubjectName());
        System.out.println("Grade:   " + gradeValue);

        System.out.print("\nSave this grade? (Y/N): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("\nGrade cancelled. Nothing saved.");
            return;
        }

        // -------- Save Grade --------
        try {
            gradeManager.addGrade(grade);
            System.out.println("\n✔ Grade saved successfully!");
            LoggerHandler.log("Grade added for student: " + studentId + ", subject: " + subject.getSubjectName());
        } catch (InvalidGradeException ig) {
            LoggerHandler.log("InvalidGradeException — " + ig.getMessage());
            System.out.println("\n❌ ERROR: " + ig.getMessage());
        }
    }

    // -------- Subject Selection Helper --------
    private Subject selectSubject() {
        System.out.println("""
                Subject Type:
                1. Core Subject (Mathematics, English, Science)
                2. Elective Subject (Music, Art, Physical Education)
                """);
        System.out.print("Select type (1-2): ");
        int type = getIntInput();
        Subject subject = null;

        if (type == 1) {
            System.out.println("""
                    Available Core Subjects:
                    1. Mathematics
                    2. English
                    3. Science
                    """);
            System.out.print("Select subject (1-3): ");
            int s = getIntInput();
            subject = switch (s) {
                case 1 -> new CoreSubject("Mathematics", "MATH101");
                case 2 -> new CoreSubject("English", "ENG101");
                case 3 -> new CoreSubject("Science", "SCI101");
                default -> {
                    System.out.println("❌ Invalid choice!");
                    yield null;
                }
            };
        } else if (type == 2) {
            System.out.println("""
                    Available Elective Subjects:
                    1. Music
                    2. Art
                    3. Physical Education
                    """);
            System.out.print("Select subject (1-3): ");
            int s = getIntInput();
            subject = switch (s) {
                case 1 -> new ElectiveSubject("Music", "MUS101");
                case 2 -> new ElectiveSubject("Art", "ART101");
                case 3 -> new ElectiveSubject("Physical Education", "PE101");
                default -> {
                    System.out.println("❌ Invalid choice!");
                    yield null;
                }
            };
        } else {
            System.out.println("❌ Invalid type!");
            return null;
        }
        return subject;
    }

    // -------- Utility to safely read integer --------
    private int getIntInput() {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid number. Try again: ");
            }
        }
    }
}
