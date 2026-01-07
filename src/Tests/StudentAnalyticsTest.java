import java.util.*;

import Audit.AuditLogger;
import Caching.CacheManager;
import Exceptions.GpaErrorException;
import Exceptions.InvalidGradeException;

public class StudentAnalyticsTest {

    public static void main(String[] args) throws  InvalidGradeException {

        CacheManager<String, Object> cache = new CacheManager<>();
        AuditLogger auditLogger = new AuditLogger();

        StudentManager studentManager = new StudentManager(cache, auditLogger);
        GradeManager gradeManager = new GradeManager(cache, auditLogger);
        GpaCalculator gpaCalculator = new GpaCalculator(gradeManager);
        StudentAnalytics analytics = new StudentAnalytics(studentManager, gradeManager, gpaCalculator);

        HonorsStudent h1 = new HonorsStudent("Alice", 18, "alice@example.com", "1234");
        HonorsStudent h2 = new HonorsStudent("Bob", 19, "bob@example.com", "2345");
        RegularStudent r1 = new RegularStudent("Charlie", 18, "charlie@example.com", "3456");
        RegularStudent r2 = new RegularStudent("Diana", 20, "diana@example.com", "4567");

        studentManager.addStudent(h1);
        studentManager.addStudent(h2);
        studentManager.addStudent(r1);
        studentManager.addStudent(r2);

        Random rand = new Random();
        for (Student s : studentManager.getAllStudents()) {
            for (int i = 0; i < 5; i++) {
//                alternate between core and elective
                boolean isCore = i % 2 == 0;

                Subject subject;
                if (isCore) {
                    subject = new CoreSubject("CoreSubject" + (i + 1), "C" + (i + 1));
                } else {
                    subject = new ElectiveSubject("ElectiveSubject" + (i + 1), "E" + (i + 1));
                }
                Grade g = new Grade(s.getStudentId(),
                        subject,
                        60 + rand.nextInt(41)); // 60-100
                gradeManager.addGrade(g);
            }
        }

        System.out.println("Honors Students Eligible:");
        analytics.findHonorsStudents().forEach(s -> System.out.println(" - " + s.getName()));

        System.out.println("\nTop 2 Students by GPA:");
        analytics.topNStudents(2).forEach(s -> {
            try {
                System.out.printf(" - %s (GPA: %.2f)%n", s.getName(), gpaCalculator.calculateGPA(s.getStudentId()));
            } catch (GpaErrorException e) {
                System.out.println(" - " + s.getName() + " (no GPA)");
            }
        });

        System.out.println("\nUnique Courses:");
        analytics.extractUniqueCourseCodes().forEach(System.out::println);

        System.out.println("\nAverage Grade Per Subject:");
        analytics.averageGradePerSubject().forEach((subj, avg) ->
                System.out.printf(" - %s: %.2f%n", subj, avg)
        );

        long sequentialTime = analytics.measureExecutionTime(false);
        long parallelTime = analytics.measureExecutionTime(true);

        System.out.println("\nExecution Time Comparison:");
        System.out.println("Sequential Stream: " + sequentialTime + " ms");
        System.out.println("Parallel Stream  : " + parallelTime + " ms");
    }
}
