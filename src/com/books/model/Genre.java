package com.books.model;

public enum Genre {
    HOUSE_AND_HOME("House and Home"),
    MUSIC("Music"),
    COMPUTER_SCIENCE("Computer Science");

    private final String label;
    Genre(String label){ this.label = label; }
    public String label(){ return label; }
}
