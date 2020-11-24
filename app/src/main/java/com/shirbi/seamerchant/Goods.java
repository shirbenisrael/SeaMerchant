package com.shirbi.seamerchant;

public enum Goods {
    WHEAT(0),
    OLIVES(1),
    COPPER(2);

    public static final int NUM_GOODS_TYPES = 3;

    private final int value;
    Goods(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
