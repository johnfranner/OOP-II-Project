package com.books.concurrent;

import java.util.concurrent.Callable;
import com.books.model.BookItem;
import com.books.util.Books;

public class PriceTask implements Callable<Double> {
    private final BookItem item;
    public PriceTask(BookItem item){ this.item = item; }

    @Override public Double call() throws Exception {
        Thread.sleep(100);
        return Books.priceOf(item) * 0.95;
    }
}
