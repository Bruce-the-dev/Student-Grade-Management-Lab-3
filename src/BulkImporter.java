import Exceptions.InvalidGradeException;
import Exceptions.StudentNotFoundException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;

public class BulkImporter {

    private final StudentManager studentManager;
    private final GradeManager gradeManager;

    private final Path logDir = Paths.get("./data/logs/");
    private final Path logFile = logDir.resolve("import_log.txt");

    public BulkImporter(StudentManager studentManager, GradeManager gradeManager) {
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
    }

    public void importGrades(String filePath) throws IOException {

        Path csvPath = Paths.get(filePath);
        Files.createDirectories(logDir);

        int successCount = 0;
        int failureCount = 0;
        int totalLines = 0;

        Instant start = Instant.now();

        try (
                BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8);
                BufferedWriter log = Files.newBufferedWriter(
                        logFile,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                )
        ) {

            log.write("IMPORT LOG\n------------------------------\n");

            String line;
            while ((line = reader.readLine()) != null) {
                totalLines++;

                String[] parts = line.split(",");

                // CSV must have 4 values
                if (parts.length != 4) {
                    log.write("Invalid format: " + line + "\n");
                    failureCount++;
                    continue;
                }

                String studentId = parts[0].trim();
                String subjectName = parts[1].trim();
                String subjectType = parts[2].trim();
                double gradeValue;

                try {
                    gradeValue = Double.parseDouble(parts[3].trim());
                } catch (NumberFormatException e) {
                    log.write("Invalid grade number: " + line + "\n");
                    failureCount++;
                    continue;
                }

                try {
                    studentManager.findStudent(studentId);

                    Subject subject;
                    if (subjectType.equalsIgnoreCase("Core")) {
                        subject = new CoreSubject(
                                subjectName,
                                subjectName.substring(0, 3).toUpperCase() + "101"
                        );
                    } else if (subjectType.equalsIgnoreCase("Elective")) {
                        subject = new ElectiveSubject(
                                subjectName,
                                subjectName.substring(0, 3).toUpperCase() + "201"
                        );
                    } else {
                        log.write("Invalid subject type: " + line + "\n");
                        failureCount++;
                        continue;
                    }

                    Grade newGrade = new Grade(studentId, subject, gradeValue);
                    gradeManager.addGrade(newGrade);

                    successCount++;

                } catch (InvalidGradeException | StudentNotFoundException e) {
                    log.write("Error: " + e.getMessage() + " | Line: " + line + "\n");
                    failureCount++;
                }
            }

            log.write("\nSUMMARY\n-----------------------------\n");
            log.write("Total rows        : " + totalLines + "\n");
            log.write("Successful imports: " + successCount + "\n");
            log.write("Failed imports    : " + failureCount + "\n");

        }

        long duration = Duration.between(start, Instant.now()).toMillis();

        System.out.println("\nBULK IMPORT COMPLETE");
        System.out.println("File: " + csvPath.toAbsolutePath());
        System.out.println("Total rows: " + totalLines);
        System.out.println("Successful: " + successCount);
        System.out.println("Failed: " + failureCount);
        System.out.println("Time taken: " + duration + " ms");
        System.out.println("Log saved to: " + logFile.toAbsolutePath());
    }

        // =================== Binary Import ===================
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

            } catch (IOException | ClassNotFoundException | InvalidGradeException | StudentNotFoundException e) {
                System.out.println("❌ Failed to read binary file: " + e.getMessage());
            }
    }
    }


