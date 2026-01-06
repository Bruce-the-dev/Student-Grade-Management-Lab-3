//import org.junit.jupiter.api.*;
//import java.io.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class BulkImporterTest {
//
//    private StudentManager studentManager;
//    private GradeManager gradeManager;
//    private BulkImport importer;
//
//    @BeforeEach
//    void setup() {
//        studentManager = new StudentManager();
//        gradeManager = new GradeManager();
//        importer = new BulkImporter(studentManager, gradeManager);
//    }
//
//    private Student makeStudent() {
//        return new RegularStudent("Alice", 20, "Alice" + "@mail.com", "12345");
//    }
//
//    private File makeTempCSV(String content) throws IOException {
//        File temp = File.createTempFile("bulk", ".csv");
//        FileWriter writer = new FileWriter(temp);
//        writer.write(content);
//        writer.close();
//        return temp;
//    }
// @Test
//    void testImportGrades_ValidRows() throws Exception {
//
//        Student s = makeStudent();
//        studentManager.addStudent(s);
//
//        File csv = makeTempCSV("""
//                %s,Math,Core,90
//                %s,Science,Core,80
//                """.formatted(s.getStudentId(), s.getStudentId()));
//
//        importer.importGrades(csv.getAbsolutePath());
//
//        assertEquals(2, gradeManager.getGradeCount(s.getStudentId()));
//    }
//
//    @Test
//    void testImportGrades_StudentNotFound() throws Exception {
//
//        File csv = makeTempCSV("""
//                STU999,Math,Core,85
//                """);
//
//        importer.importGrades(csv.getAbsolutePath());
//
//        assertEquals(0, gradeManager.getGradeCount("STU999"));
//    }
//
//    @Test
//    void testImportGrades_InvalidGrade() throws Exception {
//
//        Student s = makeStudent();
//        studentManager.addStudent(s);
//
//        File csv = makeTempCSV("""
//                %s,Math,Core,200
//                """.formatted(s.getStudentId()));
//
//        importer.importGrades(csv.getAbsolutePath());
//
//        assertEquals(0, gradeManager.getGradeCount(s.getStudentId()));
//    }
//
//    @Test
//    void testImportGrades_InvalidSubject() throws Exception {
//
//        Student s = makeStudent();
//        studentManager.addStudent(s);
//
//        File csv = makeTempCSV("""
//                %s,Math,UnknownType,90
//                """.formatted(s.getStudentId()));
//
//        importer.importGrades(csv.getAbsolutePath());
//
//        assertEquals(0, gradeManager.getGradeCount(s.getStudentId()));
//    }
//
//    @Test
//    void testImportGrades_FileNotFound() {
//        assertThrows(IOException.class, () -> {
//            importer.importGrades("C:/this/path/does/not/exist.csv");
//        });
//    }
//
//    @Test
//    void testImportGrades_MixedRows() throws Exception {
//
//        Student s1 = makeStudent();
//        studentManager.addStudent(s1);
//
//        File csv = makeTempCSV("""
//                %s,Math,Core,90
//                %s,Science,Core,abc
//                STU999,Math,Core,80
//                %s,Art,Elective,75
//                """.formatted(
//                s1.getStudentId(),
//                s1.getStudentId(),
//                s1.getStudentId()
//        ));
//
//        importer.importGrades(csv.getAbsolutePath());
//
//        assertEquals(2, gradeManager.getGradeCount(s1.getStudentId()));
//    }
//}
