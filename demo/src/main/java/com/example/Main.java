package com.example;

import java.io.File;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        File file = new File("form3.pdf");
        File output = new File("form3_filled.pdf");
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== PDF Form Operations ===");
        System.out.println("1. List PDF Fields");
        System.out.println("2. Fill PDF Fields");
        System.out.println("3. List Fields then Fill");
        System.out.print("Choose an option (1-3): ");

        try {
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\n--- Listing PDF Fields ---");
                    ListPdfFieldsPdfBox.listFields(file);
                    break;
                case 2:
                    System.out.println("\n--- Filling PDF Fields ---");
                    FillPdfFields.fillFields(file,output);
                    break;
                case 3:
                    System.out.println("\n--- Step 1: Listing Fields ---");
                    ListPdfFieldsPdfBox.listFields(file);
                    System.out.println("\n--- Step 2: Filling Fields ---");
                    FillPdfFields.fillFields(file, output);
                    break;
                default:
                    System.out.println("Invalid choice. Running both operations...");
                    System.out.println("\n--- Listing Fields ---");
                    ListPdfFieldsPdfBox.listFields(file);
                    System.out.println("\n--- Filling Fields ---");
                    FillPdfFields.fillFields(file, output);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}