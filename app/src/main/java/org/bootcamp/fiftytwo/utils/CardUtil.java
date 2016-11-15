package org.bootcamp.fiftytwo.utils;

import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.CardRank;
import org.bootcamp.fiftytwo.models.CardSuit;
import org.bootcamp.fiftytwo.models.JokerSuit;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

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

    @SuppressWarnings("unused")
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
}