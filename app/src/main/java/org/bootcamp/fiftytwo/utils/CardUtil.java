package org.bootcamp.fiftytwo.utils;

import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.CardRank;
import org.bootcamp.fiftytwo.models.CardSuit;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Created by shakiem on 11/12/16.
 */

public class CardUtil {
    public static List<Card> generateDeck(int numberOfDecks){
        List<Card> deck = new ArrayList();
        for(int i = 0; i < numberOfDecks; i++){
            deck.addAll(generateDeck());
        }
        return deck;
    }

    private static List<Card>generateDeck(){
        List<Card> deck = new ArrayList<>();

        for (CardSuit cardSuit : CardSuit.values()) {
            for (CardRank cardRank : CardRank.values()){
                deck.add(new Card(cardSuit, cardRank));
            }
        }

        return deck;
    }

    public static List<Card> shuffleDeck(List<Card> deck){
        List<Card> shuffledDeck = new ArrayList<>(deck);
        BitSet usedPositions = new BitSet(deck.size());

        //todo: Shak, write shuffling alorithm
        // basically randomize the location of each card in the deck
        //using the usedPositions to mark places already set

        return shuffledDeck;
    }
}
