package Audit;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class AuditMenu {
    private final AuditLogger auditLogger;

    public AuditMenu(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    public void print() {
        boolean auditMenuActive = true;
        Scanner sc = new Scanner(System.in);

        while (auditMenuActive) {
            System.out.println("\nAUDIT TRAIL MENU");
            System.out.println("1. View last N entries");
            System.out.println("2. Filter by operation type");
            System.out.println("3. Filter by thread ID");
            System.out.println("4. Filter by date range");
            System.out.println("5. Show audit statistics");
            System.out.println("6. Return to main menu");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("How many recent entries? ");
                    int n = sc.nextInt();
                    sc.nextLine();
                    auditLogger.printRecentEntries(n);
                }
                case 2 -> {
                    System.out.println("Select Operation Type:");
                    for (OperationType type : OperationType.values()) {
                        System.out.println("- " + type.name());
                    }

                    System.out.print("Enter operation type: ");
                    String input = sc.nextLine().toUpperCase();

                    try {
                        OperationType selectedType = OperationType.valueOf(input);
                        auditLogger.printByOperation(selectedType);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid operation type.");
                    }
                }
                case 3 -> {
                    System.out.print("Enter thread ID/name: ");
                    String thread = sc.nextLine();
                    auditLogger.printByThread(thread);
                }
                case 4 -> {
                    System.out.print("Enter start date (YYYY-MM-DD): ");
                    String startInput = sc.nextLine();

                    System.out.print("Enter end date (YYYY-MM-DD): ");
                    String endInput = sc.nextLine();

                    try {
                        LocalDate startDate = LocalDate.parse(startInput);
                        LocalDate endDate = LocalDate.parse(endInput);

                        auditLogger.printByDateRange(startDate, endDate);

                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                    }
                }
                case 5 -> auditLogger.printStatistics();
                case 6 -> auditMenuActive = false;
                default -> System.out.println("Invalid choice!");
            }
        }
    }
}
