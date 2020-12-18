package com.shirbi.seamerchant;

import java.util.Random;

public class PriceTable {
    public int[][] prices = new int[State.NUM_STATES][Goods.NUM_GOODS_TYPES];
    static private Random mRand = new Random();

    public PriceTable() {
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
    }

    public void setSpecialPricesOnState(State state) {
        for (Goods goods: Goods.values()) {
            int price = getPrice(state, goods);
            if (mRand.nextBoolean()) {
                price *= 1.5;
            } else {
                price /= 1.5;
            }
            price = (price / 10) * 10;
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
