package com.books.shop;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.books.model.*;
import com.books.util.CatalogIO;

public class Seed {
    public static List<BookItem> catalog() {
        try {
            Path csv = Path.of("data", "catalog.csv");
            List<BookItem> items = new ArrayList<>(CatalogIO.readCatalog(csv));
            if (items.isEmpty()) {
                items.add(new PhysicalBook("978-0134685991", "Effective Java", Genre.COMPUTER_SCIENCE, 55.00, 0.9));
                items.add(new PhysicalBook("978-0596009205", "Head First Java", Genre.COMPUTER_SCIENCE, 45.00, 1.0));
                items.add(new PhysicalBook("978-0321356680", "Thinking in Java", Genre.COMPUTER_SCIENCE, 50.00, 1.2));
                items.add(new PhysicalBook("978-0393356182", "How Music Works", Genre.MUSIC, 30.00, 0.8));
                items.add(new EBook("Atlas Shrugged", Genre.HOUSE_AND_HOME, 100.00, 5.0));
                CatalogIO.writeCatalog(csv, items);
            }
            return items;
        } catch (Exception e) {
            var list = new ArrayList<BookItem>();
            list.add(new PhysicalBook("978-0134685991", "Effective Java", Genre.COMPUTER_SCIENCE, 55.00, 0.9));
            list.add(new PhysicalBook("978-0596009205", "Head First Java", Genre.COMPUTER_SCIENCE, 45.00, 1.0));
            list.add(new PhysicalBook("978-0321356680", "Thinking in Java", Genre.COMPUTER_SCIENCE, 50.00, 1.2));
            list.add(new PhysicalBook("978-0393356182", "How Music Works", Genre.MUSIC, 30.00, 0.8));
            list.add(new EBook("Atlas Shrugged", Genre.HOUSE_AND_HOME, 100.00, 5.0));
            return list;
        }
    }
}
