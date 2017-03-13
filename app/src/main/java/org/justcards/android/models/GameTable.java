package org.justcards.android.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.justcards.android.utils.Constants;

import java.util.List;

/**
 * Created by baphna on 12/2/2016.
 */
@ParseClassName("GameTable")
public class GameTable extends ParseObject{

    private static DatabaseReference tableDatabaseReference, sinkDatabaseReference;

    private String gameName;
    private List<Card> cards;

    public GameTable() {
        super();
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public static void save(String gameName, List<Card> cards, boolean toSink) {
        GameTable gameTable = new GameTable();
        gameTable.setGameName(gameName);
        gameTable.setCards(cards);

        if(!toSink) {
            tableDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.TABLE_TAG + "_" + gameName);
            tableDatabaseReference.setValue(gameTable);
        } else {
            sinkDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.SINK_TAG + "_" + gameName);
            sinkDatabaseReference.setValue(gameTable);
        }
    }

}