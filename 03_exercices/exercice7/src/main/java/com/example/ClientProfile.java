package com.example;

public enum ClientProfile {
    STANDARD(0),
    PREMIUM(10),
    VIP(20);

    private final int discountPercent;

    ClientProfile(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }
}
