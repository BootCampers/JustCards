package org.bootcamp.fiftytwo.models;

/**
 * Created by baphna on 11/11/2016.
 */

public class Card {

    private String name;
    private CardRank rank;
    private CardSuit suit;

    public Card() {
    }

    public Card(String name) {
        this.name = name;
    }

    public Card(CardSuit suit, CardRank rank){
        this.suit = suit;
        this.rank = rank;
        name = rank.name() + suit.name();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CardRank getRank(){
        return rank;
    }

    public CardSuit getSuit(){
        return suit;
    }

    @Override
    public String toString(){
        return name;
    }
}
