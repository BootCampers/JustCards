package org.bootcamp.fiftytwo.models;

/**
 * Created by baphna on 11/11/2016.
 */
public class Card {

    private CardSuit suit;
    private CardRank rank;
    private String name;
    private boolean isJoker;

    public Card(CardSuit suit, CardRank rank){
        this.suit = suit;
        this.rank = rank;
        name = suit.name() + "_" + CardRank.get(rank);
    }

    public String getName() {
        return name;
    }

    public CardRank getRank(){
        return rank;
    }

    public CardSuit getSuit(){
        return suit;
    }

    public void setJoker(boolean joker) {
        isJoker = joker;
    }

    @Override
    public String toString(){
        return name;
    }
}