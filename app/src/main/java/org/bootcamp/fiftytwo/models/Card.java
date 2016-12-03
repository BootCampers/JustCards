package org.bootcamp.fiftytwo.models;

import android.content.Context;

import org.bootcamp.fiftytwo.R;
import org.parceler.Parcel;

/**
 * Created by baphna on 11/11/2016.
 */
@Parcel(analyze = Card.class)
public class Card {

    private String name;
    private boolean isJoker;
    private boolean isShowingFront;
    private boolean isViewAllowed;
    private transient boolean isSelected;

    public Card() {}

    public Card(CardSuit suit, CardRank rank){
        name = suit.name() + "_" + CardRank.get(rank);
    }

    public String getName() {
        return name;
    }

    public boolean isJoker() {
        return isJoker;
    }

    public boolean isShowingFront() {
        return isShowingFront;
    }

    public void setShowingFront(boolean showingFront) {
        isShowingFront = showingFront;
    }

    public boolean isViewAllowed() {
        return isViewAllowed;
    }

    public void setViewAllowed(boolean viewAllowed) {
        isViewAllowed = viewAllowed;
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

    public int getDrawableBack() {
        return R.drawable.back;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;
        return name != null ? name.equals(card.name) : card.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}