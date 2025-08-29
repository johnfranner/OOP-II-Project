package com.books.shop;
public class ThresholdPromotion extends Promotion {
    private final double threshold, percent;
    public ThresholdPromotion(double threshold, double percent){
        this.threshold = threshold; this.percent = percent;
    }
    @Override public double apply(double price) {
        return (price >= threshold) ? price * (1 - percent) : price;
    }
}
