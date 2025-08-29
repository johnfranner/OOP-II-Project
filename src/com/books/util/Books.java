package com.books.util;

import com.books.model.*;

public final class Books {
    private Books(){}
    public static String titleOf(BookItem b) { return b.title(); }
    public static Genre genreOf(BookItem b)  { return b.genre(); }
    public static double priceOf(BookItem b) { return b.price(); }
}
