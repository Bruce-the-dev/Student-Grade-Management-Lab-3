import java.io.Serializable;

public abstract class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    // ===============================
    // 🔹 Fields
    // ===============================
    private final String studentId;
    private String name;
    private int age;
    private String email;
    private String phone;
    private String status = "Active";

    private static GradeManager gradeManager;
    private static int studentCounter = 0;

    // ===============================
    // 🔹 Constructor
    // ===============================
    public Student(String name, int age, String email, String phone) {
        studentCounter++;
        this.studentId = "STU" + String.format("%03d", studentCounter);

        this.name = name;
        this.age = age;
        this.email = email;
        this.phone = phone;
    }

    // ===============================
    // 🔹 Abstract Methods
    // ===============================
    public abstract void displayStudentDetails();
    public abstract String getStudentType();
    public abstract double getPassingGrade();

    // ===============================
    // 🔹 Grade-related Helpers
    // ===============================
    public double calculateAverageGrade() {
        if (gradeManager != null) {
            return gradeManager.calculateOverallAverage(this.studentId);
        }
        return 0.0;
    }

    public boolean isPassing(double avg) {
        return avg >= getPassingGrade();
    }

    // ===============================
    // 🔹 Static Manager Link
    // ===============================
    public static void setGradeManager(GradeManager gm) {
        gradeManager = gm;
    }

    // ===============================
    // 🔹 Getters / Setters
    // ===============================
    public static int getStudentCounter() { return studentCounter; }
    public static void setStudentCounter(int count) { studentCounter = count; }

    public String getStudentId() { return studentId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
