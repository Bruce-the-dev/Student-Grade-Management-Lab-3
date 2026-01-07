import Exceptions.LoggerHandler;
import Exceptions.StudentNotFoundException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ExportGradesMenu {

    private final StudentManager studentManager;
    private final GradeManager gradeManager;
    private final Scanner scanner;

    public ExportGradesMenu(StudentManager studentManager, GradeManager gradeManager, Scanner scanner) {
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
        this.scanner = scanner;
    }

    public void showExportMenu() {
        scanner.nextLine(); // consume leftover newline
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();

        try {
            Student student = studentManager.findStudent(studentId);
            System.out.println("Student: " + student.getStudentId() + " - " + student.getName());
            System.out.println("Type: " + student.getStudentType());
            System.out.println("Total Grades: " + gradeManager.getGradeCount(studentId));

            System.out.println("""
                    Export Format:
                    1. CSV (Comma-Separated Values)
                    2. JSON (JavaScript Object Notation)
                    3. Binary (Serialized Object)
                    4. All formats
                    Select format (1-4):""");
            int formatChoice = scanner.nextInt();
            scanner.nextLine();

            System.out.println("""
                    Report Type:
                    1. Summary Report
                    2. Detailed Report
                    3. Transcript Format
                    4. Performance Analytics
                    Select type (1-4):""");
            int reportType = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter filename (without extension): ");
            String baseFileName = scanner.nextLine().trim();

            StudentReportGenerator.ReportType reportTypeEnum;
            switch(reportType) {
                case 1 -> reportTypeEnum = StudentReportGenerator.ReportType.SUMMARY;
                case 2 -> reportTypeEnum = StudentReportGenerator.ReportType.DETAILED;
                case 3 -> reportTypeEnum = StudentReportGenerator.ReportType.TRANSCRIPT;
                case 4 -> reportTypeEnum = StudentReportGenerator.ReportType.PERFORMANCE;
                default -> {
                    System.out.println("Invalid report type selected.");
                    return;
                }
            }

            Set<String> formats = new HashSet<>();
            switch (formatChoice) {
                case 1 -> formats.add("CSV");
                case 2 -> formats.add("JSON");
                case 3 -> formats.add("BINARY");
                case 4 -> formats.addAll(Arrays.asList("CSV", "JSON", "BINARY"));
                default -> {
                    System.out.println("Invalid format selected.");
                    return;
                }
            }

            StudentReportGenerator generator = new StudentReportGenerator(gradeManager);
            generator.exportReport(student, baseFileName, formats, reportTypeEnum);

            LoggerHandler.log("Report exported for " + studentId);

        } catch ( StudentNotFoundException e) {
            System.out.println("❌ " + e.getMessage());
            LoggerHandler.log("Failed report export — " + e.getMessage());
        }
    }
}
