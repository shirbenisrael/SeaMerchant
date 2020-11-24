package com.shirbi.seamerchant;

public class Logic {
    public PriceTable priceTable = new PriceTable();

    public void startNewGame() {
       priceTable.generateRandomPrices();
    }

}
