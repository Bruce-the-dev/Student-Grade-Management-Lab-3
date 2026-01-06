import org.junit.jupiter.api.Test;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class RegexValidationTest {

    private static final Pattern STUDENT_ID =
            Pattern.compile("^STU\\d{4}$");

    private static final Pattern EMAIL =
            Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE =
            Pattern.compile("^\\+?\\d{10,15}$");


    @Test
    void invalidStudentId_lowercase_shouldFail() {
        assertFalse(STUDENT_ID.matcher("stu001").matches());
    }

    @Test
    void validStudentId_shouldPass() {
        assertTrue(STUDENT_ID.matcher("STU0001").matches());
    }


    @Test
    void invalidEmail_missingTLD_shouldFail() {
        assertFalse(EMAIL.matcher("john@uni").matches());
    }

    @Test
    void validEmail_shouldPass() {
        assertTrue(EMAIL.matcher("john@university.edu").matches());
    }


    @Test
    void invalidPhone_shouldFail() {
        assertFalse(PHONE.matcher("555-0123").matches());
    }

    @Test
    void validInternationalPhone_shouldPass() {
        assertTrue(PHONE.matcher("+250781234567").matches());
    }

    @Test
    void validLocalPhone_shouldPass() {
        assertTrue(PHONE.matcher("0781234567").matches());
    }
}
