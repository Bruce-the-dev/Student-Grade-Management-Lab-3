//import Audit.AuditLogger;
//import Caching.CacheManager;
//import Exceptions.InvalidGradeException;
//import Statistics.CachedClassStatistics;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class ClassStatisticsCalculatorTest {
//    CacheManager<String, Object> cacheManager = new CacheManager<>();
//    private static AuditLogger auditLogger;
//    private GradeManager gradeManager;
//    private StudentManager studentManager;
//    private ClassStatisticsCalculator calculator;
//    private Student makeStd(String name) {
//        return new RegularStudent(name, 20, name.toLowerCase() + "@mail.com", "12345");
//    }
//    private Subject core(String name) {
//        return new CoreSubject(name, name.toUpperCase() + "101");
//    }
//
//    @BeforeEach
//    void setUp() throws InvalidGradeException {
//        gradeManager = new GradeManager(cacheManager, auditLogger);
//        studentManager = new StudentManager(cacheManager, auditLogger);
//        CacheManager<String, CachedClassStatistics> statsCache = new CacheManager<>();
//        calculator = new ClassStatisticsCalculator(gradeManager, studentManager,statsCache);
//
//        // Add test students
//        studentManager.addStudent(makeStd("vite"));
//
//        studentManager.addStudent(makeStd("steve"));
//        studentManager.addStudent(makeStd("S03"));
//
//        // Add test grades
//        gradeManager.addGrade(new Grade("S01", core("Mathematics"), 85));
//        gradeManager.addGrade(new Grade("S01", core("English"), 90));
//        gradeManager.addGrade(new Grade("S02", core("Science"), 95));
//        gradeManager.addGrade(new Grade("S03", core("History"), 70));
//    }
//
//    @Test
//    void testCalculateMean() {
//        List<Double> values = new ArrayList<>(List.of(85.0, 90.0, 95.0, 70.0));
//        double mean = calculator.calculateMean(values);
//        assertEquals(85.0, mean, 0.01);
//    }
//
//    @Test
//    void testCalculateMedian() {
//        List<Double> valuesEven = new ArrayList<>(List.of(70.0, 85.0, 90.0, 95.0));
//        assertEquals(87.5, calculator.calculateMedian(valuesEven), 0.01);
//
//        List<Double> valuesOdd = List.of(70.0, 85.0, 95.0);
//        assertEquals(85, calculator.calculateMedian(valuesOdd), 0.01);
//    }
//
//    @Test
//    void testCalculateMode() {
//        List<Double>values = List.of(90.0, 85.0, 90.0, 70.0);
//        assertEquals(90, calculator.calculateMode(values), 0.01);
//    }
//
//    @Test
//    void testCalculateStdDev() {
//        List<Double> values = List.of(70.0, 85.0, 90.0, 95.0);
//        double mean = calculator.calculateMean(values);
//        double std = calculator.calculateStdDev(values, mean);
//
//        assertEquals(9.354143466934854, std, 0.0001);
//    }
//
//    @Test
//    void testGetAllGrades() {
//        Grade[] allGrades = calculator.getAllGrades().toArray(new Grade[0]);
//        assertEquals(4, allGrades.length);
//    }
//}
