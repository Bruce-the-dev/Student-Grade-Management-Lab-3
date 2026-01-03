import Caching.CacheManager;
import Exceptions.GpaErrorException;
import Exceptions.InvalidGradeException;
import Exceptions.LoggerHandler;
import Exceptions.StudentNotFoundException;
import Statistics.CachedClassStatistics;

import java.util.Scanner;

public class Main {
   private static CacheManager<String, Object> cacheManager = new CacheManager<>();
    private static CacheManager<String, CachedClassStatistics> statsCache = new CacheManager<>();
    private static Scanner scanner = new Scanner(System.in);
    private static StudentManager studentManager = new StudentManager(cacheManager);
    private static GradeManager gradeManager = new GradeManager(cacheManager);
    private static GpaCalculator gpaCalculator = new GpaCalculator(gradeManager);

    // At class level
    private static final ClassStatisticsCalculator calculator =
            new ClassStatisticsCalculator(gradeManager, studentManager, statsCache);
    private static final ClassStatisticsPrinter statsPrinter =
            new ClassStatisticsPrinter(calculator);

    public static void main(String[] args) throws InvalidGradeException, StudentNotFoundException {
        initializeStudents();
        showMenu();
    }

    private static void showMenu() throws StudentNotFoundException, InvalidGradeException {
        boolean running = true;

        while (running) {
            System.out.println("\n╔═══════════════════════════════════════════════════╗");
            System.out.println("║   STUDENT GRADE MANAGEMENT - MAIN MENU            ║ " +
                               "\n║      Advanced Edition v3.0]                       ║");
            System.out.println("╚═══════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("""
                    STUDENT MANAGEMENT
                    1. Add Student (with validation)
                    2. View Students
                    3. Record Grade
                    4. View Grade Report
                    
                    FILE OPERATIONS
                    5. Export Grade Report (CSV/JSON/Binary) [ENHANCED]
                    6. Import Data (Multi-format support)
                    7. Bulk Import Grades
                    
                    ANALYTICS & REPORTING
                    8. Calculate Student GPA
                    9. View Class Statistics
                    10. Real-Time Statistics Dashboard [NEW]
                    11. Generate Batch Reports  	   [NEW]
                    
                    
                    SEARCH & QUERY
                    12. Search Students (Advanced)	   [ENHANCED]
                    13. Pattern-Based Search	   [NEW]
                    14. Query Grade History  	   [NEW]
                    
                    ADVANCED FEATURES
                    15. Schedule Automated Tasks	   [NEW]
                    16. View System Performance	   [NEW]
                    17. Cache Management	 	   [NEW]
                    18. Audit Trail Viewer		   [NEW]
                    
                    19. Exit""");
            System.out.println();
            System.out.print("Enter choice: ");

            int choice = getIntInput();

            switch (choice) {

                case 1:
                    StudentMenuHandler handler =
                            new StudentMenuHandler(studentManager, scanner);
                    handler.addStudentMenu();
                    break;

                case 2:
                    viewStudentsMenu();
                    break;

                case 3:
                    GradeInputHandler gradeMenu =
                            new GradeInputHandler(gradeManager, studentManager, scanner);
                    gradeMenu.addGradeMenu();
                    break;

                case 4:
                    viewGradeReport();
                    break;

                // FILE OPERATIONS
                case 5:
//                    ExportGradesMenu exportMenu =
//                            new ExportGradesMenu(studentManager, gradeManager, scanner);
//                    exportMenu.showExportMenu();
                    break;

                case 6:
//                    importDataMenu(); // implement this method
                    break;

                case 7:
//                    BulkImportMenu bulkImportMenu= new BulkImportMenu(studentManager, gradeManager,scanner);
//                    bulkImportMenu.showImportMenu();
                    break;

                // ANALYTICS & REPORTING
                case 8:
                    calculateGPA();
                    break;

                case 9:
                    statsPrinter.print();
                    break;
                case 10:
                    RealTimeDashboard dashboard = new RealTimeDashboard(studentManager, gradeManager);
                    dashboard.start();
                  break;

                case 11:
                    BatchReportMenuHandler batchMenu = new BatchReportMenuHandler(scanner, studentManager, gradeManager);
                    batchMenu.showBatchReportMenu();

                    break;

                // SEARCH & QUERY
                case 12:
                    searchStudent();
                    break;

                case 13:
//                    System.out.println("Not yet implemented");
                    PatternBasedSearchMenu patternSearchMenu =
                            new PatternBasedSearchMenu(studentManager,gradeManager, scanner);

                    patternSearchMenu.showMenu();
                    break;

                case 14:
                    System.out.println("Not yet implemented");
//                    queryGradeHistory();
                    break;

                // ADVANCED FEATURES
                case 15:
                    System.out.println("Not yet implemented");
//                    scheduleAutomatedTasks();
                    break;

                case 16:
                    System.out.println("Not yet implemented");
//                    viewSystemPerformance();
                    break;

                case 17:

                    break;

                case 18:
                    System.out.println("Not yet implemented");
//                    viewAuditTrail();
                    break;

                // EXIT
                case 19:
                    System.out.println("\nExiting system. Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println(
                            "\nInvalid choice. Please enter a number between 1 and 19."
                    );
            }

        }

        scanner.close();
    }

    private static void calculateGPA() {
        System.out.println("\nCALCULATE GPA");
        System.out.println("═══════════════════════════════════════════════");

        scanner.nextLine();
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();

        Student student;
        try {
            student = studentManager.findStudent(studentId);
        } catch (StudentNotFoundException snf) {
            System.out.println("❌ " + snf.getMessage());
            return;
        }

        try {

            System.out.println("\nGPA REPORT");
            System.out.println("---------------------------------------------");
            System.out.println("Name:         " + student.getName());
            System.out.println("Student ID:   " + student.getStudentId());
            gpaCalculator.displayGPAReport(studentId);
            int rank = gpaCalculator.getRankInClass(studentId, studentManager);
            int total = studentManager.getStudentCount();
            System.out.println( "Rank: " + rank + " out of " + total);

        } catch (GpaErrorException | StudentNotFoundException snf) {
            System.out.println("❌ ERROR: " + snf.getMessage());
            LoggerHandler.log("❌ ERROR Logged: " + snf.getMessage());
        }catch (Exception e){
            System.out.println("Error: "+e.getMessage());
            LoggerHandler.log("Error logged: "+e.getMessage());

        }
    }


    private static void searchStudent() throws StudentNotFoundException {
        System.out.println("""
                Search options:
                1. By Student ID
                2. By Name (partial match)
                3. By Grade Range
                4. By Student Type
                
                Select option (1-4):""");
        int choice = getIntInput();
        switch (choice) {
            case 1:
                System.out.println("Enter the student's ID: ");
                String studId = scanner.next();

                try {
                    Student s = studentManager.findStudent(studId);
                    System.out.println("───────────────────────────────────────────────────────────────");
                    System.out.printf("%-10s %-20s %-9s %-8s%n", "STU ID", "Name", "TYPE", "AVG");
                    System.out.printf("%-10s %-20s %-9s %-8s%n", s.getStudentId(), s.getName(), s.getStudentType(), s.calculateAverageGrade());


                } catch (StudentNotFoundException snf) {
                    System.out.println("Error" + snf.getMessage());
                    LoggerHandler.log(snf.getMessage());
                }
                break;
            case 2:
                System.out.println("Enter the Name (partial or full): ");
                String studName = scanner.next();
                Student[] matches = studentManager.findStudentByName(studName);
                if (matches.length == 0) {
                    System.out.println("\n❌ No students match that name.");
                    LoggerHandler.log("SearchByName — No match for: " + studName);
                } else {
                    System.out.println("Search Results: \n");
                    System.out.println("───────────────────────────────────────────────────────────────");
                    System.out.printf("%-10s %-20s %-9s %-8s%n", "STU ID", "Name", "TYPE", "AVG");

                    for (Student s : matches) {

                    System.out.printf("%-10s %-20s %-9s %.2f%n",s.getStudentId(),s.getName(),s.getStudentType(),s.calculateAverageGrade());
                    }
                }
                break;

            case 3:
                System.out.println("Search by Grade range");
                System.out.println("Enter the minimum range: ");
                double minGrade = scanner.nextDouble();
                System.out.println("Enter the maximum range: ");
                double maxGrade = scanner.nextDouble();
                Student [] gradeMatch = studentManager.searchByGradeRange(minGrade, maxGrade,gradeManager);
                if (gradeMatch.length == 0) {
                    System.out.println("\n❌ No students in that grade.");
                    LoggerHandler.log("Search By Grade range — No match for: " + minGrade+" and "+maxGrade);
                } else {
                    System.out.println("Search Results: \n");
                    System.out.println("───────────────────────────────────────────────────────────────");
                    System.out.printf("%-10s %-20s %-9s %-8s%n", "STU ID", "Name", "TYPE", "AVG");

                    for (Student match : gradeMatch) {

                        System.out.printf("%-10s %-20s %-9s %.2f%n",match.getStudentId(),match.getName(),match.getStudentType(),match.calculateAverageGrade());
                    }
                }
                break;
            case 4:
                System.out.println("""
                        Choose a number for the Student Type you are searching for
                        Types Available to choose from :
                         1.Regular
                         2.Honors\s""");

                int choiceType = scanner.nextInt();
                String studType = "";
                if (choiceType==1){
                    studType="Regular";
                }else if (choiceType==2){
                    studType="Honors";
                }else {
                    System.out.println("\n❌ Wrong choice try again.");
                    LoggerHandler.log("SearchByStudentType Error — Wrong choice: " + choiceType);
                }

                Student[] studTypes = studentManager.searchByStudentType(studType);
                if (studTypes.length == 0) {
                    System.out.println("\n❌ No students found in that studTypes.");
                    LoggerHandler.log("SearchByStudentType Error — No match for: " + studType);
                } else {
                    System.out.println("Search Results: \n");
                    System.out.println("───────────────────────────────────────────────────────────────");
                    System.out.printf("%-10s %-20s %-9s %-8s%n", "STU ID", "Name", "TYPE", "AVG");

                    for (Student s : studTypes) {

                        System.out.printf("%-10s %-20s %-9s %.2f%n",s.getStudentId(),s.getName(),s.getStudentType(),s.calculateAverageGrade());
                    }
                }

                break;
            default:
                System.out.println("invalid choice");
                break;
        }
    }

    private static void viewGradeReport() {

        System.out.println("\nVIEW GRADE REPORT");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println();

        scanner.nextLine(); // Clear buffer
        System.out.print("Enter student ID: ");
        String studentId = scanner.nextLine();
        try {


            Student student = studentManager.findStudent(studentId);
            if (student == null) {
                System.out.println("\nError: Student not found.");
                return;
            }
            System.out.printf("%-8s | %-12s | %-15s | %-10s | %-6s%n",
                    "GRD ID", "DATE", "SUBJECT", "TYPE", "GRADE");
            System.out.println("-------------------------------------------------------------");
            gradeManager.viewGradesByStudent(studentId);
            System.out.println("total number of subjects: " + gradeManager.getGradeCount(studentId));

        } catch (StudentNotFoundException snf) {
            System.out.println("ERROR: " + snf.getMessage());
        }
    }


    private static int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static void viewStudentsMenu() {
        System.out.println("\nSTUDENT LISTING");
        System.out.println("───────────────────────────────────────────────────────────────────────────");

        // Table header
        System.out.printf("%-10s | %-20s | %-10s | %-10s | %-10s%n",
                "STU ID", "NAME", "TYPE", "AVG GRADE", "STATUS");
        System.out.println("───────────────────────────────────────────────────────────────────────────");


        for (int i = 0; i < studentManager.getStudentCount(); i++) {
            Student student = studentManager.getStudentByIndex(i);

            if (student != null) {
                // Calculate average grade
                double avg = gradeManager.calculateOverallAverage(student.getStudentId());

                // Determine status
                String status;
                if (avg == 0) {
                    status = "No Grades";
                } else if (student.isPassing(avg)) {
                    status = "Passing";
                } else {
                    status = "Failing";
                }

                // Format average grade
                String avgStr = String.format("%.1f%%", avg);

                System.out.printf("%-10s | %-20s | %-10s | %-10s | %-10s%n",
                        student.getStudentId(),
                        student.getName(),
                        student.getStudentType(),
                        avgStr,
                        status);


                int enrolledSubjects = countEnrolledSubjects(student.getStudentId());
                System.out.print("           | Enrolled Subjects: " + enrolledSubjects +
                        " | Passing Grade: " + (int) student.getPassingGrade() + "%");

                if (student instanceof HonorsStudent hs) {
                    if (hs.isHonorsEligible()) {
                        System.out.print(" | Honors Eligible");
                    }
                }

                System.out.println();
                System.out.println();
            }
        }

        System.out.println("───────────────────────────────────────────────────────────────────────────");
        System.out.println();
        System.out.println("Total Students: " + studentManager.getStudentCount());
        System.out.printf("Average Class Grade: %.1f%%%n", studentManager.getAverageClassGrade(gradeManager));
        System.out.println();
        System.out.print("Press Enter to continue...");
        scanner.nextLine();

    }

    private static void initializeStudents() throws InvalidGradeException {

        // Student 1: Alice Johnson (Regular)
        Student alice = new RegularStudent("Alice Johnson", 20, "alice.johnson@university.edu", "+1-555-0001");
        studentManager.addStudent(alice);
        addInitialGrades(alice.getStudentId(), 78.5, 5);

        // Student 2: Bob Smith (Honors)
        Student bob = new HonorsStudent("Bob JoHnson", 21, "bob.smith@university.edu", "+12-999-0002");
        studentManager.addStudent(bob);
        addInitialGrades(bob.getStudentId(), 85.2, 6);

        // Student 3: Carol Martinez (Regular)
        Student carol = new RegularStudent("Carol Martinez", 19, "carol.martinez@school.edu", "+1-555-0003");
        studentManager.addStudent(carol);
        addInitialGrades(carol.getStudentId(), 45.0, 4);

        // Student 4: David Chen (Honors)
        Student david = new HonorsStudent("David Chen", 22, "david.chen@gmail.com", "+250-789-1011");
        studentManager.addStudent(david);
        addInitialGrades(david.getStudentId(), 92.8, 6);

        // Student 5: Emma Wilson (Regular)
        Student emma = new RegularStudent("Emma Wilson", 20, "emma.wilson@school.edu", "+4-367-1115");
        studentManager.addStudent(emma);
        addInitialGrades(emma.getStudentId(), 67.3, 5);
    }

    private static void addInitialGrades(String studentId, double targetAverage, int numSubjects) throws InvalidGradeException {
        Subject[] subjects = {
                new CoreSubject("Mathematics", "MATH101"),
                new CoreSubject("English", "ENG101"),
                new CoreSubject("Science", "SCI101"),
                new ElectiveSubject("Music", "MUS101"),
                new ElectiveSubject("Art", "ART101"),
                new ElectiveSubject("Physical Education", "PE101")
        };

        double totalNeeded = targetAverage * numSubjects;
        double sum = 0;


        for (int i = 0; i < numSubjects - 1; i++) {
            sum += targetAverage;


            gradeManager.addGrade(new Grade(studentId, subjects[i], targetAverage));
        }

        double lastGrade = totalNeeded - sum;

        if (lastGrade > 100) {
            lastGrade = 100;
        } else if (lastGrade < 0) {
            lastGrade = 0;
        }

        gradeManager.addGrade(new Grade(studentId, subjects[numSubjects - 1], lastGrade));
        Student.setGradeManager(gradeManager);
    }

    private static int countEnrolledSubjects(String studentId) {
        return gradeManager.getGradeCount(studentId);
    }


}