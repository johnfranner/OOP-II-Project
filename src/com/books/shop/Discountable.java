package com.books.shop;

public interface Discountable {
    private static double clamp(double x) { return Math.max(0, Math.min(1, x)); }
    static double percent(double p) { return clamp(p); }
    default double applyDiscount(double price) { return price * (1 - percent(0.10)); }
}
