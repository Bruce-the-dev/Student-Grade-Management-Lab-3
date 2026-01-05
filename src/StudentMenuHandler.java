import Exceptions.ValidationException;
import Validations.ValidationUtils;

import java.util.Scanner;

public class StudentMenuHandler {

    private final StudentManager studentManager;
    private final Scanner scanner;

    public StudentMenuHandler(StudentManager studentManager, Scanner scanner) {
        this.studentManager = studentManager;
        this.scanner = scanner;
    }

    // Main method to handle ADD STUDENT menu
    public void addStudentMenu() {
        System.out.println("\nAdd STUDENT");
        System.out.println("───────────────────────────────────────────────────────────────────────────");

        String studName = promptName();
        int studAge = promptAge();
        String studEmail = promptEmail();
        String studPhone = promptPhone();
        int studTypeChoice = promptStudentType();

        Student student;
        if (studTypeChoice == 1) {
            student = new RegularStudent(studName, studAge, studEmail, studPhone);
        } else {
            student = new HonorsStudent(studName, studAge, studEmail, studPhone);
        }

        studentManager.addStudent(student);
        student.displayStudentDetails();

        System.out.println("\nV Student added successfully!");
        System.out.println("All inputs validated with regex patterns");
    }

    // -------------------- PROMPT METHODS --------------------

    private String promptName() {
        while (true) {
            System.out.print("Enter the student Name: ");
            String input = scanner.nextLine().trim();
            try {
                ValidationUtils.validateName(input);
                System.out.println("V Valid Student Name");
                return input;
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private int promptAge() {
        while (true) {
            System.out.print("Enter student age: ");
            String input = scanner.nextLine().trim();
            try {
                int age = Integer.parseInt(input);
                if (age <= 0) {
                    System.out.println("X Age must be a positive integer.");
                } else {
                    return age;
                }
            } catch (NumberFormatException e) {
                System.out.println("X Invalid number format. Enter a positive integer for age.");
            }
        }
    }

    private String promptEmail() {
        while (true) {
            System.out.print("Enter student email: ");
            String input = scanner.nextLine().trim();
            try {
                ValidationUtils.validateEmail(input);
                System.out.println("V Valid Email Address");
                return input;
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private String promptPhone() {
        while (true) {
            System.out.print("Enter student phone: ");
            String input = scanner.nextLine().trim();
            try {
                ValidationUtils.validatePhone(input);
                System.out.println("V Valid Phone Number");
                return input;
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private int promptStudentType() {
        while (true) {
            System.out.println("\nStudent type:");
            System.out.println("1. Regular Student (Passing grade: 50%)");
            System.out.println("2. Honors Student (Passing grade: 60%, honors recognition)");
            System.out.print("Select type (1-2): ");
            String input = scanner.nextLine().trim();
            if (input.equals("1")) return 1;
            if (input.equals("2")) return 2;
            System.out.println("X Invalid choice. Please enter 1 or 2.");
        }
    }
}

