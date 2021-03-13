package com.shirbi.seamerchant;

import java.util.Random;

public class PriceTable {
    public int[][] prices = new int[State.NUM_STATES][Goods.NUM_GOODS_TYPES];
    static private Random mRand = new Random();
    Logic mLogic;

    public PriceTable(Logic logic) {
        mLogic = logic;
        for (int[] state_prices : prices) {
            for (Goods goods: Goods.values()) {
                state_prices[goods.getValue()] = 0;
            }
        }
    }

    public void generateRandomPrices() {
        for (int[] state_prices : prices) {
            for (Goods goods: Goods.values()) {
                state_prices[goods.getValue()] = goods.generateRandom();
            }
        }

        setSpecialPricesOnState(State.GREECE);
        if (mLogic.hasMedal(Medal.EGYPT_WHEAT)) {
            setSpecialPricesOnState(State.EGYPT);
        }
        if (mLogic.hasMedal(Medal.BDS_TURKEY)) {
            setSpecialPricesOnState(State.TURKEY);
        }
    }

    public int setSpecialPrice(int originalPrice) {
        int price = originalPrice;
        if (mRand.nextBoolean()) {
            price *= 1.5;
        } else {
            price /= 1.5;
        }
        price = (price / 10) * 10;

        return price;
    }

    public void setSpecialPricesOnState(State state) {
        for (Goods goods: Goods.values()) {
            int price = getPrice(state, goods);
            price = setSpecialPrice(price);
            setPrice(state, goods, price);
        }
    }

    public int getPrice(State state, Goods goods) {
        return prices[state.getValue()][goods.getValue()];
    }

    public void setPrice(State state, Goods goods, int price) {
        prices[state.getValue()][goods.getValue()] = price;
    }
}
