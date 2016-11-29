package org.bootcamp.fiftytwo;

import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.utils.CardUtil;

import java.util.List;

/**
 * Created by shakiem on 11/12/16.
 */

public class TestCards {

    public static void main(String[] args){
        List<Card> deck = CardUtil.generateDeck(1, false);

        for (Card card : deck) {
            System.out.println(card);
        }

        deck = CardUtil.shuffleDeck(deck);

        System.out.println("----------------"+deck.size()+"-----------------------");

        for (Card card : deck) {
            System.out.println(card);
        }


    }
}
