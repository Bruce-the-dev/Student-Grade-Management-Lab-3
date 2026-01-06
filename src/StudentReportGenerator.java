import Exceptions.GpaErrorException;
import Exceptions.LoggerHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

/**
 * Extended StudentReportGenerator with multiple report types
 */
public class StudentReportGenerator {

    private final GradeManager gradeManager;

    public StudentReportGenerator(GradeManager gradeManager) {
        this.gradeManager = gradeManager;
    }

    // Directories
    private final Path csvDir = Paths.get("./data/csv/");
    private final Path jsonDir = Paths.get("./data/json/");
    private final Path binaryDir = Paths.get("./data/binary/");

    private void createDirectories() throws IOException {
        Files.createDirectories(csvDir);
        Files.createDirectories(jsonDir);
        Files.createDirectories(binaryDir);
    }

    // Enum for report types
    public enum ReportType {
        SUMMARY,
        DETAILED,
        TRANSCRIPT,
        PERFORMANCE
    }

    /**
     * Export a report in one or more formats
     */
    public void exportReport(Student student, String reportName, Set<String> formats, ReportType type) {
        try {
            createDirectories();
        } catch (IOException e) {
            System.err.println("Failed to create directories: " + e.getMessage());
            return;
        }

        long totalSize = 0;
        long totalTime = 0;

        for (String format : formats) {
            try {
                switch (format.toUpperCase()) {
                    case "CSV":
                        long csvTime = exportCsv(student, reportName, type);
                        totalTime += csvTime;
                        totalSize += Files.size(csvDir.resolve(reportName + ".csv"));
                        break;
                    case "JSON":
                        long jsonTime = exportJson(student, reportName, type);
                        totalTime += jsonTime;
                        totalSize += Files.size(jsonDir.resolve(reportName + ".json"));
                        break;
                    case "BINARY":
                        long binTime = exportBinary(student, reportName, type);
                        totalTime += binTime;
                        totalSize += Files.size(binaryDir.resolve(reportName + ".dat"));
                        break;
                    default:
                        System.err.println("Unknown format: " + format);
                }
            } catch (IOException | GpaErrorException e) {
                System.err.println("Failed to export " + format + ": " + e.getMessage());
                LoggerHandler.log("Failed to export " + format + ": " + e.getLocalizedMessage());
            }
        }

        System.out.println("\nExport Performance Summary:");
        System.out.println("Total Time: " + totalTime + " ms");
        System.out.println("Total Size: " + (totalSize / 1024.0) + " KB");
    }

    // ========================= CSV Export =========================
    private long exportCsv(Student student, String reportName, ReportType type) throws IOException {
        Path path = csvDir.resolve(reportName + ".csv");
        Instant start = Instant.now();

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            switch (type) {
                case SUMMARY:
                    writeCsvSummary(student, writer);
                    break;
                case DETAILED:
                    writeCsvDetailed(student, writer);
                    break;
                case TRANSCRIPT:
                    writeCsvTranscript(student, writer);
                    break;
                case PERFORMANCE:
                    writeCsvPerformance(student, writer);
                    break;
            }
        } catch (GpaErrorException e) {
            throw new RuntimeException(e);
        }

        long duration = java.time.Duration.between(start, Instant.now()).toMillis();
        displayExportSuccess("CSV", path, type == ReportType.DETAILED ? gradeManager.getGradeCount(student.getStudentId()) : -1, duration);
        return duration;
    }

    // ========================= JSON Export =========================
    private long exportJson(Student student, String reportName, ReportType type) throws IOException, GpaErrorException {
        Path path = jsonDir.resolve(reportName + ".json");
        Instant start = Instant.now();

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            switch (type) {
                case SUMMARY:
                    writeJsonSummary(student, writer);
                    break;
                case DETAILED:
                    writeJsonDetailed(student, writer);
                    break;
                case TRANSCRIPT:
                    writeJsonTranscript(student, writer);
                    break;
                case PERFORMANCE:
                    writeJsonPerformance(student, writer);
                    break;
            }
        }

        long duration = java.time.Duration.between(start, Instant.now()).toMillis();
        displayExportSuccess("JSON", path, -1, duration);
        return duration;
    }

    // ========================= Binary Export =========================
    private long exportBinary(Student student, String reportName, ReportType type) throws IOException {
        Path path = binaryDir.resolve(reportName + ".dat");
        Instant start = Instant.now();

        try (OutputStream os = Files.newOutputStream(path);
             ObjectOutputStream oos = new ObjectOutputStream(os)) {

            // Create a report object that is fully Serializable
            StudentReport report = new StudentReport(
                    student,
                    gradeManager.getGradesForStudent(student.getStudentId()),
                    type
            );

            oos.writeObject(report);  // Write the StudentReport object
        }

        long duration = java.time.Duration.between(start, Instant.now()).toMillis();
        displayExportSuccess("Binary", path, -1, duration);
        return duration;
    }



    // ========================= CSV Writing Methods =========================
    private void writeCsvSummary(Student student, BufferedWriter writer) throws IOException {
        writer.write("Student ID,Name,Type\n");
        writer.write(String.format("%s,%s,%s\n", student.getStudentId(), student.getName(), student.getStudentType()));
        double avg = gradeManager.calculateOverallAverage(student.getStudentId());
        writer.write("Overall Average,Passing Status\n");
        writer.write(String.format("%.2f,%s\n", avg, avg >= student.getPassingGrade() ? "Passing" : "Failing"));
    }

    private void writeCsvDetailed(Student student, BufferedWriter writer) throws IOException {
        writer.write("Date,Subject,Type,Grade\n");
        Grade[] grades = gradeManager.getGradesForStudent(student.getStudentId());
        for (Grade g : grades) {
            writer.write(String.format("%s,%s,%s,%.2f\n",
                    g.getDate(),
                    g.getSubject().getSubjectName(),
                    g.getSubject().getSubjectType(),
                    g.getGrade()));
        }
    }

    private void writeCsvTranscript(Student student, BufferedWriter writer) throws IOException, GpaErrorException {
        writer.write("Transcript Report for " + student.getName() + "\n");
        writeCsvDetailed(student, writer);
        writer.write("GPA: " + String.format("%.2f", new GpaCalculator(gradeManager).calculateGPA(student.getStudentId())) + "\n");
    }

    private void writeCsvPerformance(Student student, BufferedWriter writer) throws IOException {
        writer.write("Performance Analytics\n");
        double coreAvg = gradeManager.calculateCoreAverage(student.getStudentId());
        double electiveAvg = gradeManager.calculateElectiveAverage(student.getStudentId());
        double overall = gradeManager.calculateOverallAverage(student.getStudentId());
        writer.write(String.format("Core Average,Elective Average,Overall Average\n%.2f,%.2f,%.2f\n", coreAvg, electiveAvg, overall));
    }

    // ========================= JSON Writing Methods =========================
    private void writeJsonSummary(Student student, BufferedWriter writer) throws IOException {
        writer.write("{\n");
        writer.write("\"studentId\": \"" + student.getStudentId() + "\",\n");
        writer.write("\"name\": \"" + student.getName() + "\",\n");
        writer.write("\"type\": \"" + student.getStudentType() + "\",\n");
        double avg = gradeManager.calculateOverallAverage(student.getStudentId());
        writer.write("\"overallAverage\": " + avg + ",\n");
        writer.write("\"status\": \"" + (avg >= student.getPassingGrade() ? "Passing" : "Failing") + "\"\n");
        writer.write("}\n");
    }

    private void writeJsonDetailed(Student student, BufferedWriter writer) throws IOException {
        writer.write("{\n\"studentId\": \"" + student.getStudentId() + "\",\n\"grades\": [\n");
        Grade[] grades = gradeManager.getGradesForStudent(student.getStudentId());
        for (int i = 0; i < grades.length; i++) {
            Grade g = grades[i];
            writer.write("{\"date\":\"" + g.getDate() + "\",\"subject\":\"" + g.getSubject().getSubjectName() + "\",\"type\":\"" + g.getSubject().getSubjectType() + "\",\"grade\":" + g.getGrade() + "}");
            if (i < grades.length - 1) writer.write(",");
            writer.write("\n");
        }
        writer.write("]\n}\n");
    }

    private void writeJsonTranscript(Student student, BufferedWriter writer) throws IOException, GpaErrorException {
        writeJsonDetailed(student, writer);
        writer.write("{\"GPA\": " + String.format("%.2f", new GpaCalculator(gradeManager).calculateGPA(student.getStudentId())) + "}\n");
    }

    private void writeJsonPerformance(Student student, BufferedWriter writer) throws IOException {
        writer.write("{\n\"performanceAnalytics\": {\n");
        double coreAvg = gradeManager.calculateCoreAverage(student.getStudentId());
        double electiveAvg = gradeManager.calculateElectiveAverage(student.getStudentId());
        double overall = gradeManager.calculateOverallAverage(student.getStudentId());
        writer.write("\"coreAverage\": " + coreAvg + ",\n");
        writer.write("\"electiveAverage\": " + electiveAvg + ",\n");
        writer.write("\"overallAverage\": " + overall + "\n");
        writer.write("}\n}\n");
    }

    // ========================= Export Success Info =========================
    private void displayExportSuccess(String format, Path path, int rowCount, long duration) throws IOException {
        long fileSize = Files.size(path);
        System.out.println("\n" + format + " Export completed");
        System.out.println("File: " + path.getFileName());
        System.out.println("Location: " + path.toAbsolutePath());
        System.out.println("Size: " + (fileSize / 1024.0) + " KB");
        if (rowCount > 0) System.out.println("Rows: " + rowCount);
        System.out.println("Time: " + duration + " ms");
    }

    // ========================= Serializable Wrapper =========================
    public static class StudentReport implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private final Student student;
        private final Grade[] grades;
        private final ReportType reportType;

        public StudentReport(Student student, Grade[] grades, ReportType reportType) {
            this.student = student;
            this.grades = grades;
            this.reportType = reportType;
        }

        public Student getStudent() {
            return student;
        }

        public Grade[] getGrades() {
            return grades;
        }

        public ReportType getReportType() {
            return reportType;
        }
    }

}
