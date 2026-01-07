import Audit.AuditLogger;
import Caching.CacheManager;
import Exceptions.GpaErrorException;
import Exceptions.InvalidGradeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GpaCalculatorTest {
    CacheManager<String, Object> cacheManager = new CacheManager<>();
private AuditLogger auditLogger;
    private GradeManager gradeManager;
    private GpaCalculator gpaCalc;

    @BeforeEach
    void setup() {
        gradeManager = new GradeManager(cacheManager, auditLogger);
        gpaCalc = new GpaCalculator(gradeManager);
    }

    private Subject core(String name) {
        return new CoreSubject(name, name.substring(0, 3).toUpperCase() + "101");
    }

    private Grade makeGrade(String subject, double value) throws InvalidGradeException {
        return new Grade("STU001", core(subject), value);
    }

    @Test
    void testConvertToGPA() {
        assertEquals(4.0, gpaCalc.convertToGPA(95));
        assertEquals(3.7, gpaCalc.convertToGPA(90));
        assertEquals(3.0, gpaCalc.convertToGPA(84));
        assertEquals(2.0, gpaCalc.convertToGPA(73));
        assertEquals(0.0, gpaCalc.convertToGPA(50));
    }

    @Test
    void testGetLetterGrade() {
        assertEquals("A", gpaCalc.getLetterGrade(4.0));
        assertEquals("B+", gpaCalc.getLetterGrade(3.3));
        assertEquals("C-", gpaCalc.getLetterGrade(1.7));
        assertEquals("F", gpaCalc.getLetterGrade(0.0));
    }

    @Test
    void testCalculateGPA_Valid() throws InvalidGradeException, GpaErrorException {
        gradeManager.addGrade(makeGrade("Math", 90));  // → 3.7 GPA
        gradeManager.addGrade(makeGrade("Science", 80)); // → 2.7 GPA

        double gpa = gpaCalc.calculateGPA("STU001");

        assertEquals(3.2, gpa);
    }

    @Test
    void testCalculateGPA_NoGrades() {
        assertThrows(GpaErrorException.class, () -> {
            gpaCalc.calculateGPA("STU001");
        });
    }
}
