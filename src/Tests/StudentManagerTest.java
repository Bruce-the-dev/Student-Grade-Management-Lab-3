import static org.junit.jupiter.api.Assertions.*;

import Audit.AuditLogger;
import Caching.CacheManager;
import Exceptions.StudentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StudentManagerTest {

    private StudentManager studentManager;
    CacheManager<String, Object> cacheManager = new CacheManager<>();
private AuditLogger auditLogger;
    @BeforeEach
    void setup() {
        studentManager = new StudentManager(cacheManager, auditLogger);
        Student.setStudentCounter(0);
    }

    private Student makeStd(String name) {
        return new RegularStudent(name, 20, name.toLowerCase() + "@mail.com", "12345");
    }

    @Test
    void testAddStudent() {
        studentManager.addStudent(makeStd("Alice"));
        assertEquals(1, studentManager.getStudentCount());
    }

    @Test
    void testFindStudent_Found() throws StudentNotFoundException {
        Student s1 = makeStd("Alice");
        studentManager.addStudent(s1);

        Student found = studentManager.findStudent("STU001");

        assertNotNull(found);
        assertEquals("Alice", found.getName());
    }

    @Test
    void testFindStudent_NotFound() {
        assertThrows(StudentNotFoundException.class, () -> {
            studentManager.findStudent("STU999");
        });
    }

    @Test
    void testFindStudentByName_PartialMatch() {
        studentManager.addStudent(makeStd("Alice"));
        studentManager.addStudent(makeStd("Alicia"));
        studentManager.addStudent(makeStd("Bob"));

        Student[] results = studentManager.findStudentByName("Ali");

        assertEquals(2, results.length);
        assertEquals("Alice", results[0].getName());
        assertEquals("Alicia", results[1].getName());
    }

    @Test
    void testGetStudentByIndex() {
        studentManager.addStudent(makeStd("Alice"));
        studentManager.addStudent(makeStd("Bob"));

        Student s0 = studentManager.getStudentByIndex(0);
        Student s1 = studentManager.getStudentByIndex(1);

        assertEquals("Alice", s0.getName());
        assertEquals("Bob", s1.getName());
    }

    @Test
    void testGetStudentByIndex_Invalid() {
        studentManager.addStudent(makeStd("Alice"));

        assertNull(studentManager.getStudentByIndex(-1));
        assertNull(studentManager.getStudentByIndex(99));
    }
}
