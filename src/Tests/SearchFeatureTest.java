import Caching.CacheManager;
import Exceptions.InvalidGradeException;
import Exceptions.StudentNotFoundException;
import Statistics.CachedClassStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SearchFeatureTest {
    CacheManager<String, Object> cacheManager = new CacheManager<>();
    private StudentManager studentManager;
    private GradeManager gradeManager;

    @BeforeEach
    void setup() {
        studentManager = new StudentManager(cacheManager);
        gradeManager = new GradeManager(cacheManager);
    }

    private Student createRegularStudent(String name, int age) {
        return new RegularStudent(name, age, name.toLowerCase() + "@mail.com", "12345");
    }

    private Student createHonorsStudent(String name, int age) {
        return new HonorsStudent(name, age, name.toLowerCase() + "@mail.com", "12345");
    }
    private Subject core(String name) {
        return new CoreSubject(name, name.substring(0,3).toUpperCase() + "101");
    }

    private Subject elective(String name) {
        return new ElectiveSubject(name, name.substring(0,3).toUpperCase() + "101");
    }
    @Test
    void testSearchById_Found() throws StudentNotFoundException {
        Student s = createRegularStudent("Alice", 20);
        studentManager.addStudent(s);

        Student found = studentManager.findStudent(s.getStudentId());

        assertNotNull(found);
        assertEquals("Alice", found.getName());
    }

    @Test
    void testSearchById_NotFound() {
        assertThrows(StudentNotFoundException.class, () -> {
            studentManager.findStudent("STU999");
        });
    }
    @Test
    void testSearchByName_PartialMatch() {
        Student s1 = createRegularStudent("Alice", 20);
        Student s2 = createRegularStudent("Alicia", 21);
        Student s3 = createHonorsStudent("Bob", 22);

        studentManager.addStudent(s1);
        studentManager.addStudent(s2);
        studentManager.addStudent(s3);

        Student[] results = studentManager.findStudentByName("Ali");

        assertEquals(2, results.length);
        assertEquals("Alice", results[0].getName());
        assertEquals("Alicia", results[1].getName());
    }
    @Test
    void testSearchByGradeRange() throws InvalidGradeException {
        // Create students so searchByGradeRange has actual data
        Student s1 = createRegularStudent("Alice", 20);  // ID = STU001
        Student s2 = createRegularStudent("Bob", 20);    // ID = STU002
        Student s3 = createRegularStudent("Charlie", 20);// ID = STU003

        studentManager.addStudent(s1);
        studentManager.addStudent(s2);
        studentManager.addStudent(s3);

        // Grades
        Grade g1 = new Grade(s1.getStudentId(), core("Math"), 85);        // in range
        Grade g2 = new Grade(s2.getStudentId(), core("Math"), 80);        // in range
        Grade g3 = new Grade(s3.getStudentId(), core("Science"), 50);     // out of range

        Grade g6 = new Grade(s1.getStudentId(), elective("Music"), 85);        // in range
        Grade g5 = new Grade(s2.getStudentId(), elective("Art"), 80);        // in range
        Grade g4 = new Grade(s3.getStudentId(), elective("Music"), 50);     // out of range

        gradeManager.addGrade(g1);
        gradeManager.addGrade(g2);
        gradeManager.addGrade(g3);

        gradeManager.addGrade(g6);
        gradeManager.addGrade(g5);
        gradeManager.addGrade(g4);
        Student[] results = studentManager.searchByGradeRange(60, 90, gradeManager);

        assertEquals(2, results.length);
        assertTrue(List.of(results).contains(s1));
        assertTrue(List.of(results).contains(s2));
        assertFalse(List.of(results).contains(s3));
    }
    @Test
    void testSearchByType_Regular() {
        Student s1 = createRegularStudent("Alice", 20);
        Student s2 = createRegularStudent("Bob", 21);
        Student s3 = createHonorsStudent("Charlie", 22);

        studentManager.addStudent(s1);
        studentManager.addStudent(s2);
        studentManager.addStudent(s3);

        Student[] regularStudents = studentManager.searchByStudentType("Regular");

        assertEquals(2, regularStudents.length);
        assertEquals("Alice", regularStudents[0].getName());
        assertEquals("Bob", regularStudents[1].getName());
    }

    @Test
    void testSearchByType_Honors() {
        Student s1 = createRegularStudent("Alice", 20);
        Student s2 = createHonorsStudent("Bob", 21);
        Student s3 = createHonorsStudent("Charlie", 22);

        studentManager.addStudent(s1);
        studentManager.addStudent(s2);
        studentManager.addStudent(s3);

        Student[] honorsStudents = studentManager.searchByStudentType("Honors");

        assertEquals(2, honorsStudents.length);
        assertEquals("Bob", honorsStudents[0].getName());
        assertEquals("Charlie", honorsStudents[1].getName());
    }

    @Test
    void testSearchByType_Invalid() {
        Student s1 = createRegularStudent("Alice", 20);
        studentManager.addStudent(s1);

        Student[] results = studentManager.searchByStudentType("Unknown");

        assertEquals(0, results.length);
    }
}
