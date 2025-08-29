package com.books.model;

// Sealed interface, book-focused
public sealed interface BookItem permits PhysicalBook, EBook {
    String title();
    Genre genre();
    double price();
}
