import java.io.Serializable;

public abstract class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String studentId;
    private String name;
    private int age;
    private String email;
    private String phone;
    private String status = "Active";

    private static GradeManager gradeManager;
    private static int studentCounter = 0;

    public Student(String name, int age, String email, String phone) {
        studentCounter++;
        this.studentId = "STU" + String.format("%03d", studentCounter);

        this.name = name;
        this.age = age;
        this.email = email;
        this.phone = phone;
    }

    public abstract void displayStudentDetails();
    public abstract String getStudentType();
    public abstract double getPassingGrade();

    public double calculateAverageGrade() {
        if (gradeManager != null) {
            return gradeManager.calculateOverallAverage(this.studentId);
        }
        return 0.0;
    }

    public boolean isPassing(double avg) {
        return avg >= getPassingGrade();
    }

    public static void setGradeManager(GradeManager gm) {
        gradeManager = gm;
    }

    public static void setStudentCounter(int count) { studentCounter = count; }

    public String getStudentId() { return studentId; }

    public String getStatus() { return status; }

    public String getPhone() { return phone; }

    public String getEmail() { return email; }

    public int getAge() { return age; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
