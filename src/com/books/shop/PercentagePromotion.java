package com.books.shop;
public class PercentagePromotion extends Promotion {
    private final double percent; // 0.10 = 10%
    public PercentagePromotion(double percent){ this.percent = percent; }
    @Override public double apply(double price) { return price * (1 - percent); }
}
