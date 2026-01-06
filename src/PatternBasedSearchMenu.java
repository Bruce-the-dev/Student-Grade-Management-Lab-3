import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class PatternBasedSearchMenu {

    private final StudentManager studentManager;
    private final GradeManager gradeManager;
    private final Scanner scanner;

    public PatternBasedSearchMenu(StudentManager studentManager,
                                  GradeManager gradeManager,
                                  Scanner scanner) {
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
        this.scanner = scanner;
    }

    public void showMenu() {
        System.out.println("\nPATTERN-BASED SEARCH");
        System.out.println("""
                1. Email Domain Pattern
                2. Phone Area Code Pattern
                3. Student ID Pattern
                4. Name Pattern (regex)
                5. Custom Regex Pattern
                """);

        System.out.print("Select type (1-5): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter regex pattern: ");
        String regex = scanner.nextLine();

        performSearch(regex,choice);
    }

    private void performSearch(String regex,int choice) {
        Student[] matches = new Student[studentManager.getStudentCount()];
        int matchCount = 0;

        Pattern pattern;
        try {
            pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            System.out.println("❌ Invalid regex pattern.");
            return;
        }

        long start = System.currentTimeMillis();

        for (int i = 0; i < studentManager.getStudentCount(); i++) {
            Student s = studentManager.getStudentByIndex(i);

            switch (choice) {
                case 1 -> { if (pattern.matcher(s.getEmail()).find()) matches[matchCount++] = s; }
                case 2 -> { if (pattern.matcher(s.getPhone()).find()) matches[matchCount++] = s; }
                case 3 -> { if (pattern.matcher(s.getStudentId()).find()) matches[matchCount++] = s; }
                case 4 -> { if (pattern.matcher(s.getName()).find()) matches[matchCount++] = s; }
                case 5 -> { // custom regex searches all fields
                    if (pattern.matcher(s.getName()).find()
                            || pattern.matcher(s.getStudentId()).find()
                            || pattern.matcher(s.getEmail()).find()
                            || pattern.matcher(s.getPhone()).find()) {
                        matches[matchCount++] = s;
                    }
                }
            }

        }

        long duration = System.currentTimeMillis() - start;

        displayResults(matches, matchCount);
        displayStats(studentManager.getStudentCount(), matchCount, duration);
        showBulkActions(matches, matchCount);
    }

    private void displayResults(Student[] matches, int count) {
        System.out.println("\nSEARCH RESULTS (" + count + " found)\n");

        System.out.printf("%-10s | %-20s | %-25s%n",
                "STU ID", "NAME", "EMAIL");
        System.out.println("-----------------------------------------------------------");

        for (int i = 0; i < count; i++) {
            Student s = matches[i];
            System.out.printf("%-10s | %-20s | %-25s%n",
                    s.getStudentId(),
                    s.getName(),
                    s.getEmail());
        }
    }

    private void displayStats(int total, int matches, long time) {
        System.out.println("\nPattern Match Statistics:");
        System.out.println("Total Students Scanned: " + total);
        System.out.println("Matches Found: " + matches);
        System.out.println("Search Time: " + time + " ms");
    }

    private void showBulkActions(Student[] matches, int count) {
        boolean inBulkMenu = true;

        while (inBulkMenu) {
            System.out.println("""
                
                Actions:
                1. Export search results
                2. Generate reports for matched students
                3. New search with different pattern
                4. Return to main menu
                """);

            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> exportSearchResults(matches, count); // new feature
                case 2 -> generateReports(matches, count); // bulk reports
                case 3 -> showMenu(); // new search
                case 4 -> inBulkMenu = false; // exit bulk menu
                default -> System.out.println("Invalid choice.");
            }
        }
    }
    private void generateReports(Student[] matched, int count) {
        if (count == 0) {
            System.out.println("No students to generate reports for.");
            return;
        }

        System.out.println("\nGENERATING REPORTS FOR MATCHED STUDENTS\n");

        for (int i = 0; i < count; i++) {
            Student s = matched[i];

            System.out.println("────────────────────────────────────────");
            System.out.println("Student: " + s.getName());
            System.out.println("ID: " + s.getStudentId());

            gradeManager.viewGradesByStudent(s.getStudentId());
            System.out.println("Total Subjects: "
                    + gradeManager.getGradeCount(s.getStudentId()));
        }

        System.out.println("\n✔ Reports generated successfully.");
    }
    private void exportSearchResults(Student[] matches, int count) {
        if (count == 0) {
            System.out.println("No students to export.");
            return;
        }

        System.out.print("Enter base report name (without extension): ");
        String reportName = scanner.nextLine().trim();
        StudentReportGenerator generate= new StudentReportGenerator(gradeManager);
            for (int i = 0; i < count; i++) {
                Student student = matches[i];
        Set<String> formats = Set.of("CSV");
        try {
            generate.exportReport(student, reportName + "_" + student.getStudentId(), formats, StudentReportGenerator.ReportType.SUMMARY);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

            }

            System.out.println("✔ Search results exported successfully.");

        }


}
