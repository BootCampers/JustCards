package org.bootcamp.fiftytwo.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import org.bootcamp.fiftytwo.R;
import org.parceler.Parcel;

/**
 * Created by baphna on 11/11/2016.
 */
@Parcel(analyze = Card.class)
public class Card {

    private CardSuit suit;
    private CardRank rank;
    private String name;
    private boolean isJoker;
    private boolean isSelected;

    private Card() {}

    public Card(CardSuit suit, CardRank rank){
        this.suit = suit;
        this.rank = rank;
        name = suit.name() + "_" + CardRank.get(rank);

    }

    @SuppressWarnings("unused")
    public CardSuit getSuit(){
        return suit;
    }

    @SuppressWarnings("unused")
    public CardRank getRank(){
        return rank;
    }

    public String getName() {
        return name;
    }

    public boolean isJoker() {
        return isJoker;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getDrawable(Context context) {
        return context.getResources().getIdentifier(name.toLowerCase(), "drawable", context.getPackageName());
    }

    public Drawable getDrawableBack(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.back);
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