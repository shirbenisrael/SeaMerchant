package com.shirbi.seamerchant;

import java.util.Random;

public enum Goods {
    WHEAT(0),
    OLIVES(1),
    COPPER(2);

    public static final int NUM_GOODS_TYPES = 3;
    private Random rand = new Random();

    static private int[] multiplier = {5,50,100};
    static private int[] minimum = {7, 7, 25};
    static private int[] numValues = {8, 7, 11};

    private final int value;
    Goods(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int generateRandom() {
        return (rand.nextInt(numValues[value]) + minimum[value]) * multiplier[value];
    }
}