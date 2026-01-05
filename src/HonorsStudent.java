import java.io.Serializable;

public class HonorsStudent extends Student implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final double PASSING_GRADE = 60.0;

    public HonorsStudent(String name, int age, String email, String phone) {
        super(name, age, email, phone);
    }

    @Override
    public void displayStudentDetails() {
        System.out.println("\n=== Honors Student Details ===");
        System.out.println(" Student ID: " + getStudentId());
        System.out.println(" Name: " + getName());
        System.out.println(" Type: " + getStudentType());
        System.out.println(" Age: " + getAge());
        System.out.println(" Email: " + getEmail());
        System.out.println(" Passing Grade: " + getPassingGrade());
        System.out.println(" Honors Eligible: " + isHonorsEligible());
        System.out.println(" Status: " + getStatus());
    }

    public boolean isHonorsEligible() {
        return calculateAverageGrade() >= 85;
    }

    @Override
    public String getStudentType() {
        return "Honors";
    }

    @Override
    public double getPassingGrade() {
        return PASSING_GRADE;
    }
}
