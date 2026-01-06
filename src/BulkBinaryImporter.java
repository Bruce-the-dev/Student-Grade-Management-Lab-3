import Exceptions.InvalidGradeException;
import Exceptions.StudentNotFoundException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BulkBinaryImporter {

    private final StudentManager studentManager;
    private final GradeManager gradeManager;

    public BulkBinaryImporter(StudentManager studentManager, GradeManager gradeManager) {
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
    }

    public void importBinaryFile(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            System.out.println("❌ File not found: " + filePath);
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {

            Object obj = ois.readObject();
            if (obj instanceof StudentReportGenerator.StudentReport report) {

                Student student = report.getStudent();
                // Make sure student exists
                if (studentManager.findStudent(student.getStudentId()) == null) {
                    studentManager.addStudent(student);
                }

                Grade[] grades = report.getGrades();
                for (Grade g : grades) {
                    gradeManager.addGrade(g);
                }

                System.out.println("✔ Successfully imported " + grades.length + " grades for " + student.getName());

            } else {
                System.out.println("❌ Invalid binary file format: " + filePath);
            }

        } catch (IOException | ClassNotFoundException | InvalidGradeException |
                 StudentNotFoundException e) {
            System.out.println("❌ Failed to read binary file: " + e.getMessage());
        }
    }
}
