package org.bootcamp.fiftytwo.models;

/**
 * Created by shakiem on 11/12/16.
 */
public enum CardSuit {
    CLUBS("clubs"),
    DIAMONDS("diamonds"),
    HEARTS("hearts"),
    SPADES("spades");

    private String name;

    CardSuit(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }
}
