package com.books.i18n;
import java.util.ListResourceBundle;
public class Messages_fr_FR extends ListResourceBundle {
    @Override protected Object[][] getContents() {
        return new Object[][] {
            {"label.title","Titre"},
            {"label.genre","Genre"},
            {"label.price","Prix"},
            {"catalog.localized","Catalogue (localisé)"},
            {"cart.order","Commande"},
            {"cart.discountedUnit","Prix unitaire remisé"},
            {"cart.promoPrice","Prix promo"},
            {"genre.MUSIC","Musique"},
            {"genre.HOUSE_AND_HOME","Maison & Habitat"},
            {"genre.COMPUTER_SCIENCE","Informatique"}
        };
    }
}
