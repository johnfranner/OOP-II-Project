package com.books.util;

import com.books.model.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public final class CatalogIO {
    private CatalogIO(){}

    public static List<BookItem> readCatalog(Path csv) throws IOException {
        if (!Files.exists(csv)) return List.of();
        return Files.lines(csv)
                .skip(1)
                .filter(line -> !line.isBlank())
                .map(CatalogIO::parseRow)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static BookItem parseRow(String line) {
        // CSV header: type,title,genre,price,isbn,weightKg,fileSizeMb
        String[] p = line.split(",", -1);
        if (p.length < 7) return null;
        String type = p[0].trim();
        String title = p[1].trim();
        Genre genre = Genre.valueOf(p[2].trim());
        double price = Double.parseDouble(p[3].trim());
        String isbn = p[4].trim();
        String weight = p[5].trim();
        String fileSize = p[6].trim();
        if ("PHYSICAL".equalsIgnoreCase(type)) {
            double w = weight.isEmpty() ? 0.0 : Double.parseDouble(weight);
            return new PhysicalBook(isbn, title, genre, price, w);
        } else {
            double fs = fileSize.isEmpty() ? 0.0 : Double.parseDouble(fileSize);
            return new EBook(title, genre, price, fs);
        }
    }

    public static void writeCatalog(Path csv, List<BookItem> items) throws IOException {
        Files.createDirectories(csv.getParent());
        var sb = new StringBuilder();
        sb.append("type,title,genre,price,isbn,weightKg,fileSizeMb\n");
        for (BookItem b : items) {
            if (b instanceof PhysicalBook pb) {
                sb.append(String.join(",",
                        "PHYSICAL",
                        escape(pb.title()),
                        pb.genre().name(),
                        Double.toString(pb.price()),
                        escape(pb.isbn()),
                        Double.toString(pb.weightKg()),
                        ""));
                sb.append("\n");
            } else if (b instanceof EBook eb) {
                sb.append(String.join(",",
                        "EBOOK",
                        escape(eb.title()),
                        eb.genre().name(),
                        Double.toString(eb.price()),
                        "",
                        "",
                        Double.toString(eb.fileSizeMb())));
                sb.append("\n");
            }
        }
        Files.writeString(csv, sb.toString());
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace(",", " ");
    }
}
