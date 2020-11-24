package com.shirbi.seamerchant;

public class PriceTable {
    public int[][] prices = new int[State.NUM_STATES][Goods.NUM_GOODS_TYPES];

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
    }

    public int getPrice(State state, Goods goods) {
        return prices[state.getValue()][goods.getValue()];
    }
}