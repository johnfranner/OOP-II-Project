package com.books;

import com.books.model.*;
import com.books.shop.*;
import com.books.util.*;
import com.books.concurrent.*;
import com.books.ui.Menu;
import com.books.i18n.I18n;

import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.UUID;
import java.util.function.Supplier;

public class App {
    public static void main(String[] args) throws Exception {
        List<BookItem> items = Seed.catalog();
        Scanner sc = new Scanner(System.in);

        // Non-interactive demo mode
        if (args.length > 0 && "--demo".equals(args[0])) {
            browseAndFilter(items);
            addToCart(items);
            salesByCountryBooks();
            exportReport(items);
            shipmentEtas();
            sortingDemo(items);
            return;
        }

        boolean running = true;
        while (running) {
            switch (Menu.prompt(sc)) {
                case 1 -> browseAndFilter(items);
                case 2 -> addToCart(items);
                case 3 -> salesByCountryBooks();
                case 4 -> exportReport(items);
                case 5 -> localise(sc, items);
                case 6 -> concurrentPrices(items);
                case 7 -> shipmentEtas();
                case 8 -> sortingDemo(items);
                case 9 -> manageCatalog(sc, items);
                case 10 -> manageShipments(sc);
                case 11 -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
        System.out.println("Bye.");
    }

    private static void browseAndFilter(List<BookItem> items) {
        System.out.println("-- Filter and sort all computer science books --");
        items.stream().filter(b -> b.genre() == Genre.COMPUTER_SCIENCE)
                .map(Books::titleOf).sorted().forEach(System.out::println);

        System.out.println("-- All books by ascending price --");
        items.stream().sorted(Comparator.comparingDouble(Books::priceOf))
                .forEach(System.out::println);

        long expensive = items.stream().filter(b -> Books.priceOf(b) > 50).count();
        System.out.println("Number of books with a price > 50: " + expensive);

        var byGenre = items.stream().collect(Collectors.groupingBy(
                Books::genreOf, Collectors.mapping(Books::titleOf, Collectors.toList())
        ));
        System.out.println("Grouped by genre: " + byGenre);

        var titleToPrice = items.stream()
            .collect(java.util.stream.Collectors.toMap(
                BookItem::title, BookItem::price, (a,b) -> a));
        System.out.println("title->price: " + titleToPrice);

        var byPriceTier = items.stream()
            .collect(java.util.stream.Collectors.partitioningBy(b -> b.price() > 50.0));
        System.out.println("premium(>50): " + byPriceTier.get(true));
        System.out.println("standard(<=50): " + byPriceTier.get(false));
    }

    private static void addToCart(List<BookItem> items) {
        Supplier<String> orderId = () -> "ORD-" + UUID.randomUUID();
        Supplier<java.time.ZonedDateTime> pricedAt = java.time.ZonedDateTime::now;
        System.out.println("Order " + orderId.get() + " priced at " + pricedAt.get());

        CartLine line = new CartLine(items.get(0), 2);
        double unit = Books.priceOf(line.item());
        double discounted = new Discountable(){}.applyDiscount(unit);
        Promotion promo = new ThresholdPromotion(50.0, 0.15); // 15% if >= €50
        double promoPrice = promo.apply(unit);
        System.out.println("Cart: " + line + "; discounted unit: " + discounted + "; promo price: " + promoPrice);
    }

    private static void salesByCountryBooks() {
        Map<String,Integer> titleToSales = Map.of(
                "Effective Java", 49000,
                "Head First Java", 15000,
                "How Music Works", 25000
        );
        Map<String,String> titleToCountry = Map.of(
                "Effective Java", "Germany",
                "Head First Java", "Japan",
                "How Music Works", "USA"
        );
        Map<String,Integer> salesByCountry = new TreeMap<>();
        titleToSales.forEach((title, sales) -> {
            String country = titleToCountry.get(title);
            if (country == null) return;
            if (salesByCountry.containsKey(country)) {
                int current = salesByCountry.get(country);
                salesByCountry.put(country, current + sales);
            } else {
                salesByCountry.put(country, sales);
            }
        });
        salesByCountry.forEach((c,n) -> System.out.println("country: " + c + "; numSales:" + n));
        int max = Collections.max(salesByCountry.values());
        int min = Collections.min(salesByCountry.values());
        salesByCountry.forEach((c,n) -> {
            if (n == max) System.out.println("Country with the most sales: " + c + " " + max);
            else if (n == min) System.out.println("Country with the least sales: " + c + " " + min);
        });
    }

    private static void exportReport(List<BookItem> items) {
        try {
            double avg = items.stream().mapToDouble(Books::priceOf).average().orElse(0.0);
            var lines = List.of(
                    "Catalog size: " + items.size(),
                    "Average price: " + avg,
                    "Generated at: " + ZonedDateTime.now()
            );
            Path report = IOUtil.writeReport(lines);
            System.out.println("Wrote: " + report.toAbsolutePath());
            IOUtil.printFile(report);
        } catch (Exception e) {
            System.out.println("Error writing report: " + e.getMessage());
        }
    }

    private static void localise(Scanner sc, java.util.List<BookItem> items) {
        System.out.println("(G)erman, (F)rench or English (any other key)");
        String choice = sc.nextLine().trim().toLowerCase();

        Locale locale = switch (choice) {
            case "g" -> new Locale("de","DE");
            case "f" -> new Locale("fr","FR");
            default  -> Locale.getDefault();
        };

        ResourceBundle rb = ResourceBundle.getBundle("com.books.i18n.Messages", locale);

        System.out.println(rb.getString("catalog.localized"));
        for (BookItem b : items) {
            String gLabel   = I18n.genreLabel(b.genre(), rb);
            String priceTxt = I18n.fmtPrice(b.price(), locale);
            System.out.println(
                rb.getString("label.title") + ": " + b.title()
                + " | " + rb.getString("label.genre") + ": " + gLabel
                + " | " + rb.getString("label.price") + ": " + priceTxt
            );
        }
    }


    private static void concurrentPrices(List<BookItem> items) {
        System.out.println("-- Concurrency (PriceTask) --");
        ExecutorService pool = Executors.newFixedThreadPool(3);
        try {
            var futures = pool.invokeAll(items.stream().map(PriceTask::new).toList());
            for (Future<Double> f : futures) System.out.println("remote price: " + f.get());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void shipmentEtas() {
        try {
            var csv = Path.of("data", "shipments.csv");
            var shipments = IOUtil.readShipments(csv);
            if (shipments.isEmpty()) {
                System.out.println("No shipments found at " + csv.toAbsolutePath());
                return;
            }
            List<String> lines = new ArrayList<>();
            for (Shipment s : shipments) {
                var arrival = IOUtil.eta(s.originTz(), s.destTz(), s.departureLocal(), s.transit());
                String line = String.format(Locale.ROOT,
                        "Shipment %s: depart %s (%s), arrive %s (%s) local.",
                        s.id(),
                        s.departureLocal(),
                        s.originTz(),
                        arrival.toLocalDateTime(),
                        s.destTz());
                System.out.println(line);
                lines.add(line);
            }
            var report = IOUtil.writeReport(lines);
            System.out.println("Shipment report written to: " + report.toAbsolutePath());
        } catch (Exception e) {
            System.out.println("Error reading shipments: " + e.getMessage());
        }
    }

    private static void sortingDemo(List<BookItem> items) {
        System.out.println("-- Sorting demo (books-only) --");
        var physical = items.stream().filter(b -> b instanceof PhysicalBook).map(b -> (PhysicalBook)b)
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.sort(physical); // natural order by ISBN
        System.out.println("By ISBN (Comparable):");
        physical.forEach(System.out::println);
        System.out.println("By price (Comparator):");
        physical.sort(Comparator.comparingDouble(PhysicalBook::price));
        physical.forEach(System.out::println);
    }

    // ===== Manage catalogue (add/remove/edit) =====
    private static void manageCatalog(Scanner sc, List<BookItem> items) {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Manage Catalogue ---");
            System.out.println("(A)dd book   (R)emove book   (E)dit book   (L)ist   (Q)uit");
            System.out.print("Choose: ");
            String s = sc.nextLine().trim().toLowerCase();
            if (s.isEmpty()) continue;
            char c = s.charAt(0);
            switch (c) {
                case 'a' -> addBookFlow(sc, items);
                case 'r' -> removeBookFlow(sc, items);
                case 'e' -> editBookFlow(sc, items);
                case 'l' -> listBooks(items);
                case 'q' -> back = true;
                default  -> System.out.println("Unknown option.");
            }
        }
    }

    private static void listBooks(List<BookItem> items) {
        System.out.println("Catalogue has " + items.size() + " item(s):");
        items.forEach(System.out::println);
    }

    private static void addBookFlow(Scanner sc, List<BookItem> items) {
        System.out.print("Type (P)hysical or (E)Book? ");
        String t = sc.nextLine().trim().toLowerCase();
        boolean physical = !t.isEmpty() && t.charAt(0) == 'p';

        String title = promptNonEmpty(sc, "Title");
        Genre genre = promptGenre(sc);
        double price = promptDouble(sc, "Price");

        if (physical) {
            String isbn = promptNonEmpty(sc, "ISBN");
            double weight = promptDouble(sc, "Weight (kg)");
            items.add(new PhysicalBook(isbn, title, genre, price, weight));
            System.out.println("Added PhysicalBook: " + title + " [" + isbn + "]");
        } else {
            double fileSize = promptDouble(sc, "File size (MB)");
            items.add(new EBook(title, genre, price, fileSize));
            System.out.println("Added EBook: " + title);
        }
        persist(items);
    }

    private static void removeBookFlow(Scanner sc, List<BookItem> items) {
        System.out.print("Enter ISBN (physical) or Title (any) to remove: ");
        String key = sc.nextLine().trim();
        if (key.isEmpty()) { System.out.println("Nothing entered."); return; }

        for (int i = 0; i < items.size(); i++) {
            BookItem b = items.get(i);
            if (b instanceof PhysicalBook pb && pb.isbn().equalsIgnoreCase(key)) {
                items.remove(i);
                System.out.println("Removed PhysicalBook with ISBN: " + key);
                persist(items);
                return;
            }
        }
        for (int i = 0; i < items.size(); i++) {
            BookItem b = items.get(i);
            if (b.title().equalsIgnoreCase(key)) {
                items.remove(i);
                System.out.println("Removed: " + b);
                persist(items);
                return;
            }
        }
        System.out.println("No matching book found for: " + key);
    }

    private static void editBookFlow(Scanner sc, List<BookItem> items) {
        System.out.print("Enter ISBN (physical) or Title (any) to edit: ");
        String key = sc.nextLine().trim();
        if (key.isEmpty()) { System.out.println("Nothing entered."); return; }

        for (int i = 0; i < items.size(); i++) {
            BookItem b = items.get(i);
            if (b instanceof PhysicalBook pb && pb.isbn().equalsIgnoreCase(key)) {
                String newTitle = promptOptional(sc, "New title (blank = keep)", pb.title());
                Genre newGenre = promptGenreOptional(sc, pb.genre());
                Double newPrice = promptDoubleOptional(sc, "New price (blank = keep)", pb.price());
                Double newWeight = promptDoubleOptional(sc, "New weight kg (blank = keep)", pb.weightKg());
                items.set(i, new PhysicalBook(pb.isbn(), newTitle, newGenre, newPrice, newWeight));
                System.out.println("Updated: " + items.get(i));
                persist(items);
                return;
            }
            if (b.title().equalsIgnoreCase(key)) {
                if (b instanceof EBook eb) {
                    String newTitle = promptOptional(sc, "New title (blank = keep)", eb.title());
                    Genre newGenre = promptGenreOptional(sc, eb.genre());
                    Double newPrice = promptDoubleOptional(sc, "New price (blank = keep)", eb.price());
                    Double newSize = promptDoubleOptional(sc, "New file size MB (blank = keep)", eb.fileSizeMb());
                    items.set(i, new EBook(newTitle, newGenre, newPrice, newSize));
                } else if (b instanceof PhysicalBook pb2) {
                    String newTitle = promptOptional(sc, "New title (blank = keep)", pb2.title());
                    Genre newGenre = promptGenreOptional(sc, pb2.genre());
                    Double newPrice = promptDoubleOptional(sc, "New price (blank = keep)", pb2.price());
                    Double newWeight = promptDoubleOptional(sc, "New weight kg (blank = keep)", pb2.weightKg());
                    items.set(i, new PhysicalBook(pb2.isbn(), newTitle, newGenre, newPrice, newWeight));
                }
                System.out.println("Updated: " + items.get(i));
                persist(items);
                return;
            }
        }
        System.out.println("No matching book found for: " + key);
    }

    private static Genre promptGenre(Scanner sc) {
        System.out.println("Choose genre:");
        Genre[] values = Genre.values();
        for (int i = 0; i < values.length; i++) {
            System.out.println("  " + (i+1) + ") " + values[i].name() + " (" + values[i].label() + ")");
        }
        while (true) {
            System.out.print("Enter 1-" + values.length + ": ");
            String s = sc.nextLine().trim();
            try {
                int idx = Integer.parseInt(s);
                if (idx >= 1 && idx <= values.length) return values[idx-1];
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid choice.");
        }
    }

    private static String promptNonEmpty(Scanner sc, String label) {
        while (true) {
            System.out.print(label + ": ");
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("Please enter a value.");
        }
    }

    private static double promptDouble(Scanner sc, String label) {
        while (true) {
            System.out.print(label + ": ");
            String s = sc.nextLine().trim();
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                System.out.println("Enter a number (e.g., 49.99).");
            }
        }
    }

    private static String promptOptional(Scanner sc, String label, String current) {
        System.out.print(label + " [" + current + "]: ");
        String s = sc.nextLine().trim();
        return s.isEmpty() ? current : s;
    }

    private static Genre promptGenreOptional(Scanner sc, Genre current) {
        System.out.println("Choose genre (blank = keep " + current.name() + "):");
        Genre[] values = Genre.values();
        for (int i = 0; i < values.length; i++) {
            System.out.println("  " + (i+1) + ") " + values[i].name() + " (" + values[i].label() + ")");
        }
        System.out.print("Enter 1-" + values.length + " or blank: ");
        String s = sc.nextLine().trim();
        if (s.isEmpty()) return current;
        try {
            int idx = Integer.parseInt(s);
            if (idx >= 1 && idx <= values.length) return values[idx-1];
        } catch (NumberFormatException ignored) {}
        System.out.println("Invalid choice, keeping " + current.name());
        return current;
    }

    private static Double promptDoubleOptional(Scanner sc, String label, double current) {
        System.out.print(label + " [" + current + "]: ");
        String s = sc.nextLine().trim();
        if (s.isEmpty()) return current;
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { System.out.println("Invalid number, keeping " + current); return current; }
    }

    private static void persist(List<BookItem> items) {
        try {
            java.nio.file.Path csv = java.nio.file.Path.of("data", "catalog.csv");
            CatalogIO.writeCatalog(csv, items);
        } catch (Exception e) {
            System.out.println("Warning: could not save catalog.csv: " + e.getMessage());
        }
    }

    // ===== Manage shipments (add/remove) =====
    private static void manageShipments(Scanner sc) {
        java.nio.file.Path csv = java.nio.file.Path.of("data", "shipments.csv");
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Manage Shipments ---");
            System.out.println("(A)dd   (R)emove   (L)ist   (Q)uit");
            System.out.print("Choose: ");
            String s = sc.nextLine().trim().toLowerCase();
            if (s.isEmpty()) continue;
            char c = s.charAt(0);
            try {
                switch (c) {
                    case 'a' -> {
                        String id = promptNonEmpty(sc, "ID");
                        String origin = promptNonEmpty(sc, "Origin TZ (e.g., Europe/Dublin)");
                        String dest = promptNonEmpty(sc, "Dest TZ (e.g., Europe/Zurich)");
                        java.time.LocalDateTime dep = java.time.LocalDateTime.parse(promptNonEmpty(sc, "Departure (YYYY-MM-DDThh:mm)"));
                        String dur = promptNonEmpty(sc, "Duration HH:MM");
                        String[] hm = dur.split(":");
                        java.time.Duration transit = java.time.Duration.ofHours(Integer.parseInt(hm[0])).plusMinutes(Integer.parseInt(hm[1]));

                        var list = com.books.util.IOUtil.readShipments(csv);
                        list = new java.util.ArrayList<>(list);
                        list.add(new com.books.model.Shipment(id, origin, dest, dep, transit));
                        com.books.util.IOUtil.writeShipments(csv, list);
                        System.out.println("Added shipment " + id);
                    }
                    case 'r' -> {
                        String id = promptNonEmpty(sc, "ID to remove");
                        var list = new java.util.ArrayList<>(com.books.util.IOUtil.readShipments(csv));
                        boolean removed = list.removeIf(sh -> sh.id().equalsIgnoreCase(id));
                        com.books.util.IOUtil.writeShipments(csv, list);
                        System.out.println(removed ? "Removed " + id : "No shipment found: " + id);
                    }
                    case 'l' -> {
                        var list = com.books.util.IOUtil.readShipments(csv);
                        if (list.isEmpty()) System.out.println("No shipments.");
                        else list.forEach(System.out::println);
                    }
                    case 'q' -> back = true;
                    default -> System.out.println("Unknown option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
