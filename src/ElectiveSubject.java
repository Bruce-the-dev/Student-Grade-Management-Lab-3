public class ElectiveSubject extends Subject {

    private static final boolean MANDATORY = false;

    public ElectiveSubject(String subjectName, String subjectCode) {
        super(subjectName, subjectCode);
    }

    @Override
    public void displaySubjectDetails() {
        System.out.println("Elective Subject Info:");
        System.out.println("Name: " + getSubjectName());
        System.out.println("Code: " + getSubjectCode());
    }

    @Override
    public String getSubjectType() {
        return "Elective";
    }

    public boolean isMandatory() {
        return MANDATORY;
    }
}
