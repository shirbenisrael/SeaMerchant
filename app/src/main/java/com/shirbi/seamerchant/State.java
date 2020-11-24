package com.shirbi.seamerchant;

public enum State {
    EGYPT(0),
    ISRAEL(1),
    TURKEY(2),
    CYPRUS(3),
    GREECE(4);

    public static final int NUM_STATES = 5;

    private final int value;
    State(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
