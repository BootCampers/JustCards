package org.bootcamp.fiftytwo.models;

import android.content.Context;

/**
 * Created by baphna on 11/11/2016.
 */
public class Card {

    private CardSuit suit;
    private CardRank rank;
    private String name;
    private boolean isJoker;

    private Card() {}

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

    public int getDrawable(Context context) {
        return context.getResources().getIdentifier(name.toLowerCase(), "drawable", context.getPackageName());
    }

    public static Card getJoker(JokerSuit suit) {
        Card card = new Card();
        card.name = suit.name() + "_" + "joker";
        card.isJoker = true;
        return card;
    }

    @Override
    public String toString(){
        return name;
    }
}