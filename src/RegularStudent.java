import java.io.Serializable;

public class RegularStudent extends Student implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final double PASSING_GRADE = 50.0;

    public RegularStudent(String name, int age, String email, String phone) {
        super(name, age, email, phone);
    }

    @Override
    public void displayStudentDetails() {
        System.out.println("\n=== Regular Student Details ===");
        System.out.println(" Student ID: " + getStudentId());
        System.out.println(" Name: " + getName());
        System.out.println(" Type: " + getStudentType());
        System.out.println(" Age: " + getAge());
        System.out.println(" Email: " + getEmail());
        System.out.println(" Passing Grade: " + getPassingGrade());
        System.out.println(" Status: " + getStatus());
    }

    @Override
    public String getStudentType() {
        return "Regular";
    }

    @Override
    public double getPassingGrade() {
        return PASSING_GRADE;
    }
}
