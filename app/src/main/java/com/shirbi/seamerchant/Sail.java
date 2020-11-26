package com.shirbi.seamerchant;

public class Sail {
    State mDestination;
    State mSource;

    public Sail(Logic logic, State destination) {
        mDestination = destination;
        mSource = logic.mCurrentState;
    }
}
