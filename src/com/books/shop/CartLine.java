package com.books.shop;

import com.books.model.BookItem;

public class CartLine implements Discountable {
    private final BookItem item;
    private int qty;

    public CartLine(BookItem item, int qty) { this.item = item; this.qty = qty; }
    public BookItem item() { return item; }
    public int qty() { return qty; }
    public void setQty(int q) { this.qty = q; }

    @Override public String toString() { return item + " x" + qty; }
}
