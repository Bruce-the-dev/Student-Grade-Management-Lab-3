package Validations;

import Exceptions.ValidationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ValidationUtils {

    // Prevent instantiation
    private ValidationUtils() { }

    // Precompiled patterns
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("STU\\d{3}");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z]+(['\\s-][a-zA-Z]+)*$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9.%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "(\\(\\d{3}\\)\\d{3}-\\d{4})" + // (123)456-7890
                    "|(\\d{3}-\\d{3}-\\d{4})" +     // 123-456-7890
                    "|(\\+1-\\d{3}-\\d{3}-\\d{4})" +// +1-123-456-7890
                    "|(\\d{10})"                     // 1234567890
    );
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$"); // YYYY-MM-DD
    private static final Pattern COURSE_CODE_PATTERN = Pattern.compile("^[A-Z]{3}\\d{3}$"); // MAT101
    private static final Pattern GRADE_PATTERN = Pattern.compile("^(100|[1-9]?\\d)$"); // 0-100


    public static void validateStudentId(String input) throws ValidationException {
        matchPattern(STUDENT_ID_PATTERN, input, "Student ID", "STU001, STU042, STU999");
    }

    public static void validateName(String input) throws ValidationException {
        matchPattern(NAME_PATTERN, input, "Name", "John Smith, Mary-Jane O'Connor");
    }

    public static void validateEmail(String input) throws ValidationException {
        matchPattern(EMAIL_PATTERN, input, "Email", "john.smith@university.edu, jsmith@college.org");
    }

    public static void validatePhone(String input) throws ValidationException {
        matchPattern(PHONE_PATTERN, input, "Phone Number",
                "(123)456-7890, 123-456-7890, +1-123-456-7890, 1234567890");
    }

    public static void validateDate(String input) throws ValidationException {
        matchPattern(DATE_PATTERN, input, "Date", "2024-11-03");
    }

    public static void validateCourseCode(String input) throws ValidationException {
        matchPattern(COURSE_CODE_PATTERN, input, "Course Code", "MAT101, ENG203, PHY205");
    }

    public static void validateGrade(String input) throws ValidationException {
        matchPattern(GRADE_PATTERN, input, "Grade", "0, 75, 100");
    }


    private static void matchPattern(Pattern pattern, String input, String fieldName, String examples)
            throws ValidationException {
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            String message = String.format(
                    "X VALIDATION ERROR: Invalid %s format%n" +
                            "Pattern required: %s%n" +
                            "Examples: %s%n" +
                            "Your input: %s",
                    fieldName,
                    readablePattern(fieldName),
                    examples,
                    input
            );
            throw new ValidationException(message);
        }
    }

    // Converts technical regex into human-readable description
    private static String readablePattern(String fieldName) {
        return switch (fieldName) {
            case "Student ID" -> "STU### (STU followed by exactly 3 digits)";
            case "Name" -> "Only letters, spaces, hyphens, and apostrophes";
            case "Email" -> "username@domain.extension";
            case "Phone Number" -> "Accepted patterns: (123)456-7890, 123-456-7890, +1-123-456-7890, 1234567890";
            case "Date" -> "YYYY-MM-DD";
            case "Course Code" -> "Three uppercase letters followed by three digits (e.g., MAT101)";
            case "Grade" -> "Integer between 0 and 100";
            default -> "Pattern not defined";
        };
    }
}

