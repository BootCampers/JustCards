package org.bootcamp.fiftytwo.utils;

import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.CardRank;
import org.bootcamp.fiftytwo.models.CardSuit;
import org.bootcamp.fiftytwo.models.JokerSuit;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

import static org.bootcamp.fiftytwo.utils.AppUtils.isEmpty;

/**
 * Created by shakiem on 11/12/16.
 */
public class CardUtil {

    public static List<Card> generateDeck(int numberOfDecks, boolean includeJokers) {
        List<Card> deck = new ArrayList<>();
        for (int i = 0; i < numberOfDecks; i++) {
            deck.addAll(generateDeck(includeJokers));
        }
        return deck;
    }

    private static List<Card> generateDeck(boolean includeJokers) {
        List<Card> deck = new ArrayList<>();

        for (CardSuit cardSuit : CardSuit.values()) {
            for (CardRank cardRank : CardRank.values()) {
                deck.add(new Card(cardSuit, cardRank));
            }
        }

        if (includeJokers) {
            deck.addAll(generateJokers());
        }

        return deck;
    }

    private static List<Card> generateJokers() {
        List<Card> jokers = new ArrayList<>();

        for (JokerSuit jokerSuit : JokerSuit.values()) {
            jokers.add(Card.getJoker(jokerSuit));
        }

        return jokers;
    }

    public static List<Card> selectDefaults(final List<Card> deck) {
        for (Card card : deck) {
            if (!card.isJoker()) {
                card.setSelected(true);
            }
        }
        return deck;
    }

    public static List<Card> getSelected(final List<Card> deck) {
        List<Card> cards = new ArrayList<>();
        for (Card card : deck) {
            if (card.isSelected()) {
                cards.add(card);
            }
        }
        return cards;
    }

    public static synchronized List<Card> draw(final List<Card> cards, final int count, boolean drawFromEnd) {
        List<Card> drawnCards = null;
        if (!isEmpty(cards) && cards.size() >= count && count > 0) {
            drawnCards = new ArrayList<>();
            int start = drawFromEnd ? cards.size() - count : 0;
            int end = drawFromEnd ? cards.size() : count;
            for (; start < end; start++) {
                drawnCards.add(cards.get(start));
            }
        }
        return drawnCards;
    }

    public static List<Card> shuffleDeck(List<Card> deck) {
        List<Card> shuffledDeck = new ArrayList<>(deck);
        BitSet usedPositions = new BitSet(deck.size());
        Random rand = new Random();
        int newPosition;

        for (Card card : deck) {
            newPosition = rand.nextInt(deck.size());
            if (!usedPositions.get(newPosition)) { //if card is not already at position, add it
                shuffledDeck.set(newPosition, card);
                usedPositions.set(newPosition);
            } else { // else find next available position and add it.
                while (usedPositions.get(newPosition)) {
                    newPosition = ++newPosition % deck.size();
                }
                shuffledDeck.set(newPosition, card);
                usedPositions.set(newPosition);
            }
        }

        return shuffledDeck;
    }

    public static void setShowingFront(List<Card> cards) {
        if (!isEmpty(cards)) {
            for (Card card : cards) {
                card.setShowingFront(false);
            }
        }
    }
}