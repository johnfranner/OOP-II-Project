package com.books.model;

public record EBook(String title, Genre genre, double price, double fileSizeMb) implements BookItem {
    @Override public String toString() {
        return "EBook{title=%s, genre=%s, price=%.2f}".formatted(title, genre.label(), price);
    }
}
