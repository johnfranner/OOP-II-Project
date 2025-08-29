package com.books.model;

// Comparable by ISBN for sorting demo
public record PhysicalBook(String isbn, String title, Genre genre, double price, double weightKg)
        implements BookItem, Comparable<PhysicalBook> {

    @Override public int compareTo(PhysicalBook other) {
        return this.isbn.compareTo(other.isbn());
    }

    @Override public String toString() {
        return "PhysicalBook{isbn=%s, title=%s, genre=%s, price=%.2f}"
                .formatted(isbn, title, genre.label(), price);
    }
}
