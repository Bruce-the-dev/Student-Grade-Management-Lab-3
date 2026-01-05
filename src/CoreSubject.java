public class CoreSubject extends Subject {

    private static final boolean MANDATORY = true;

    public CoreSubject(String subjectName, String subjectCode) {
        super(subjectName, subjectCode);
    }

    @Override
    public void displaySubjectDetails() {
        System.out.println("Core Subject Info:");
        System.out.println("Name: " + getSubjectName());
        System.out.println("Code: " + getSubjectCode());
    }

    @Override
    public String getSubjectType() {
        return "Core";
    }

    public boolean isMandatory() {
        return MANDATORY;
    }
}
