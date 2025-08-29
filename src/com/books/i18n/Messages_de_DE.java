package com.books.i18n;
import java.util.ListResourceBundle;
public class Messages_de_DE extends ListResourceBundle {
    @Override protected Object[][] getContents() {
        return new Object[][] {
            {"label.title","Titel"},
            {"label.genre","Genre"},
            {"label.price","Preis"},
            {"catalog.localized","Katalog (lokalisiert)"},
            {"cart.order","Bestellung"},
            {"cart.discountedUnit","Rabattierter Stückpreis"},
            {"cart.promoPrice","Aktionspreis"},
            {"genre.MUSIC","Musik"},
            {"genre.HOUSE_AND_HOME","Haus & Heim"},
            {"genre.COMPUTER_SCIENCE","Informatik"}
        };
    }
}
