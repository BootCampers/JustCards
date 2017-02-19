package org.justcards.android.models;

/**
 * Created by shakiem on 11/12/16.
 */
public enum CardRank {
    ACE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
    JACK,
    QUEEN,
    KING;

    public static String get(CardRank rank) {
        switch (rank) {
            case ACE:
                return "ACE";
            case TWO:
                return "2";
            case THREE:
                return "3";
            case FOUR:
                return "4";
            case FIVE:
                return "5";
            case SIX:
                return "6";
            case SEVEN:
                return "7";
            case EIGHT:
                return "8";
            case NINE:
                return "9";
            case TEN:
                return "10";
            case JACK:
                return "JACK";
            case QUEEN:
                return "QUEEN";
            case KING:
                return "KING";
            default:
                return "";
        }
    }
}