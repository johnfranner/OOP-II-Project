package com.books.i18n;

import com.books.model.Genre;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public final class I18n {
    private I18n(){}

    public static String genreLabel(Genre g, ResourceBundle rb) {
        String key = "genre." + g.name();
        try {
            return rb.getString(key);
        } catch (Exception e) {
            // fallback to enum name if missing
            return g.name();
        }
    }

    public static String fmtPrice(double price, Locale locale) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
        return nf.format(price);
    }
}
