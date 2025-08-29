package com.books.i18n;
import java.util.ListResourceBundle;
public class Messages_en extends ListResourceBundle {
    @Override protected Object[][] getContents() {
        return new Object[][] {
            // Book labels
            {"label.title","Title"},
            {"label.genre","Genre"},
            {"label.price","Price"},
            {"catalog.localized","Catalog (localized)"},
            {"cart.order","Order"},
            {"cart.discountedUnit","Discounted unit"},
            {"cart.promoPrice","Promo price"},
            // Genres
            {"genre_COMPUTER_SCIENCE","Computer Science"},
            {"genre.MUSIC","Music"},
            {"genre.HOUSE_AND_HOME","House & Home"},
            {"genre.COMPUTER_SCIENCE","Computer Science"}
        };
    }
}
