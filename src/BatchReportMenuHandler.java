import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class BatchReportMenuHandler {

    private final Scanner scanner;
    private final StudentManager studentManager;
    private final GradeManager gradeManager;

    public BatchReportMenuHandler(Scanner scanner, StudentManager studentManager, GradeManager gradeManager) {
        this.scanner = scanner;
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
    }

    public void showBatchReportMenu() {
        System.out.println("\nBATCH REPORT GENERATION");
        System.out.println("══════════════════════════════════════════");

        Student[] allStudents = studentManager.getAllStudents();
        if (allStudents.length == 0) {
            System.out.println("No students available.");
            return;
        }

        // List all students
        System.out.println("Available Students:");
        for (int i = 0; i < allStudents.length; i++) {
            System.out.printf("%d. %s (%s)\n", i + 1, allStudents[i].getName(), allStudents[i].getStudentId());
        }

        System.out.println("\nEnter the numbers of students to generate reports for (comma-separated, e.g., 1,3,5):");
        scanner.nextLine(); // consume leftover newline
        String input = scanner.nextLine();
        List<Student> selectedStudents = parseStudentSelection(input, allStudents);
        if (selectedStudents.isEmpty()) {
            System.out.println("No valid students selected. Aborting.");
            return;
        }

        int numThreads = promptThreadCount();
        Set<String> formats = promptFormats();
        StudentReportGenerator.ReportType reportType = promptReportType();

        // Generate reports
        BatchReportGenerator batchGenerator = new BatchReportGenerator(new StudentReportGenerator(gradeManager));
        batchGenerator.generateReports(selectedStudents, formats, reportType, numThreads);
    }

    private List<Student> parseStudentSelection(String input, Student[] allStudents) {
        String[] selections = input.split(",");
        return Arrays.stream(selections)
                .map(String::trim)
                .map(s -> {
                    try {
                        int idx = Integer.parseInt(s) - 1;
                        if (idx >= 0 && idx < allStudents.length) return allStudents[idx];
                    } catch (NumberFormatException ignored) {}
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private int promptThreadCount() {
        System.out.print("Enter number of threads (2-8): ");
        int numThreads = scanner.nextInt();
        scanner.nextLine(); // consume newline
        if (numThreads < 2 || numThreads > 8) numThreads = Math.min(Math.max(numThreads, 2), 8);
        return numThreads;
    }

    private Set<String> promptFormats() {
        System.out.println("""
            Select export formats (comma-separated):
            1. CSV
            2. JSON
            3. Binary
            Example: 1,2 for CSV + JSON""");
        String formatInput = scanner.nextLine();
        String[] formatSelections = formatInput.split(",");
        Set<String> formats = new HashSet<>();
        for (String f : formatSelections) {
            switch (f.trim()) {
                case "1" -> formats.add("CSV");
                case "2" -> formats.add("JSON");
                case "3" -> formats.add("BINARY");
                default -> System.out.println("Invalid format selection: " + f);
            }
        }
        return formats;
    }

    private StudentReportGenerator.ReportType promptReportType() {
        System.out.println("""
            Select report type:
            1. Summary Report
            2. Detailed Report
            3. Transcript Format
            4. Performance Analytics""");
        int reportTypeChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        return switch (reportTypeChoice) {
            case 1 -> StudentReportGenerator.ReportType.SUMMARY;
            case 2 -> StudentReportGenerator.ReportType.DETAILED;
            case 3 -> StudentReportGenerator.ReportType.TRANSCRIPT;
            case 4 -> StudentReportGenerator.ReportType.PERFORMANCE;
            default -> {
                System.out.println("Invalid report type. Defaulting to SUMMARY.");
                yield StudentReportGenerator.ReportType.SUMMARY;
            }
        };
    }
}
