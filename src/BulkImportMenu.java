import java.io.IOException;
import java.util.Scanner;

public class BulkImportMenu {
    private final Scanner scanner;
    private final StudentManager studentManager;
    private final GradeManager gradeManager;

    public BulkImportMenu(StudentManager studentManager, GradeManager gradeManager, Scanner scanner) {
        this.scanner = scanner;
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
    }

    public void showImportMenu() {
        System.out.println("\nBULK IMPORT MENU");
        System.out.println("──────────────────────────");
        System.out.println("Select import type:");
        System.out.println("1. CSV file");
        System.out.println("2. Binary file");
        System.out.print("Enter choice (1-2): ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume leftover newline

        System.out.print("Enter file path: ");
        String path = scanner.nextLine().trim();

        BulkImporter importer = new BulkImporter(studentManager, gradeManager);

        try {
            switch (choice) {
                case 1 -> importer.importGrades(path);      // CSV import
                case 2 -> importer.importBinaryFile(path); // Binary import
                default -> System.out.println("❌ Invalid choice");
            }
        } catch (IOException e) {
            System.out.println("❌ File error: " + e.getMessage());
        }
    }
}
