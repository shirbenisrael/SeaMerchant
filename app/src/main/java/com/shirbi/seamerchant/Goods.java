package com.shirbi.seamerchant;

import java.util.Random;

public enum Goods {
    WHEAT(0),
    OLIVES(1),
    COPPER(2);

    public static final int NUM_GOODS_TYPES = 3;
    static private Random rand = new Random();

    static private int[] multiplier = {5,50,100};
    static private int[] minimum = {7, 7, 25};
    static private int[] numValues = {8, 7, 11};

    static private int[] lowMinimum = {4, 4, 15};
    static private int[] lowNumValues = {3, 3, 5};

    static private int[] highMinimum = {15, 14, 36};
    static private int[] highNumValues = {3, 3, 10};

    static private int[] merchantMinimum = {4, 5, 20};
    static private int[] merchantNumValues = {12, 12, 21};

    private static final int[] mWideButtonImageId = {
            R.drawable.wide_wheat_button, R.drawable.wide_olives_button, R.drawable.wide_copper_button};

    private static final int[] mMainImageId = {
            R.drawable.wheat, R.drawable.olives, R.drawable.copper};

    private static final int[] mStringId = {R.string.WHEAT, R.string.OLIVES, R.string.COPPER};

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

    public int generateRandomLow() {
        return (rand.nextInt(lowNumValues[value]) + lowMinimum[value]) * multiplier[value];
    }

    public int generateRandomHigh() {
        return (rand.nextInt(highNumValues[value]) + highMinimum[value]) * multiplier[value];
    }

    public int generateRandomMerchant() {
        return (rand.nextInt(merchantNumValues[value]) + merchantMinimum[value]) * multiplier[value];
    }

    static public Goods generateRandomType() {
        return values()[rand.nextInt(Goods.NUM_GOODS_TYPES)];
    }

    public int toWideButtonId() {
        return mWideButtonImageId[value];
    }

    public int toMainImageId() {
        return mMainImageId[value];
    }

    public int toStringId() {
        return mStringId[value];
    }

    static final Goods generateRandomGoods() {
        return values()[rand.nextInt(NUM_GOODS_TYPES)];
    }
}
