import Caching.CacheManager;
import Exceptions.InvalidGradeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GradeManagerTest {
private GradeManager gradeManager;
    CacheManager<String, Object> cacheManager = new CacheManager<>();

    @BeforeEach
    void setup() {
        gradeManager = new GradeManager(cacheManager);
    }

    private Subject core(String name) {
        return new CoreSubject(name, name.toUpperCase() + "101");
    }


    @Test
    void addValidGrade() throws InvalidGradeException {
        Grade g = new Grade("STU001", core("Math"), 85);
        gradeManager.addGrade(g);

        assertEquals(1, gradeManager.getGradeCount("STU001"));
    }
    @Test
void addInvalidGrade() throws InvalidGradeException {

        Exception ex = assertThrows(InvalidGradeException.class, () -> {
            Grade g = new Grade("STU001", core("Math"), -5);
            gradeManager.addGrade(g);
    });

    assertTrue(ex.getMessage().contains("between 0 and 100"));
}
    @Test
    void testAddGrade_InvalidAbove100() throws InvalidGradeException {

        Exception ex = assertThrows(InvalidGradeException.class, () -> {
            Grade g = new Grade("STU001", core("Math"), 150);
            gradeManager.addGrade(g);
        });

        assertTrue(ex.getMessage().contains("between 0 and 100"));
    }
    @Test
    void testGetGradesForStudent_NoGrades() {
        Grade[] result = gradeManager.getGradesForStudent("STU001");
        assertEquals(0, result.length);
    }
    @Test
    void testGetGradesForStudent_OrderNewestFirst() throws InvalidGradeException {
        Grade g1 = new Grade("STU001", core("Math"), 80);   // older
        Grade g2 = new Grade("STU001", core("Science"), 75); // newer

        gradeManager.addGrade(g1);
        gradeManager.addGrade(g2);

        Grade[] result = gradeManager.getGradesForStudent("STU001");

        assertEquals("Science", result[0].getSubject().getSubjectName());
        assertEquals("Math", result[1].getSubject().getSubjectName());
    }

}