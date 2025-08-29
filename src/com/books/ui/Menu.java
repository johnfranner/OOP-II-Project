package com.books.ui;

import java.util.Scanner;

public final class Menu {
    private Menu(){}
    public static int prompt(Scanner sc) {
        System.out.println();
        System.out.println("=== Books Demo ===");
        System.out.println("1) Browse & filter books (Streams)");
        System.out.println("2) Cart & discount (OOP + interface defaults + promotions)");
        System.out.println("3) Sales by country (Maps) + extremes");
        System.out.println("4) Export summary report (NIO.2) and display");
        System.out.println("5) Localise strings (ResourceBundle)");
        System.out.println("6) Refresh prices concurrently (ExecutorService)");
        System.out.println("7) Shipment ETAs from CSV (ZonedDateTime + report)");
        System.out.println("8) Sorting demo (Comparable by ISBN, Comparator by price)");
        System.out.println("9) Manage catalogue (add/remove/edit)");
        System.out.println("10) Manage shipments (add/remove)");
        System.out.println("11) Exit");
        System.out.print("Choose: ");
        String s = sc.nextLine().trim();
        if (s.isEmpty()) return 0;
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
    }
}
