import java.io.Serializable;

/**
 * Represents a general academic subject.
 * Core and Elective subjects will extend this class.
 */
public abstract class Subject implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String subjectName;       // Immutable after creation
    private final String subjectCode;       // Immutable after creation

    public static int subjectCount = 0;

    /**
     * Base subject constructor.
     */
    public Subject(String subjectName, String subjectCode) {
        subjectCount++;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
    }

    /** Display subject details in child classes */
    public abstract void displaySubjectDetails();

    /** Return type: "Core" or "Elective" */
    public abstract String getSubjectType();

    /** Getters (no setters to maintain immutability) */
    public String getSubjectCode() { return subjectCode; }
    public String getSubjectName() { return subjectName; }

    @Override
    public String toString() {
        return subjectName + " (" + subjectCode + ")";
    }
}
