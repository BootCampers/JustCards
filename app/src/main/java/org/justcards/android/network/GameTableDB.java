package org.justcards.android.network;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.justcards.android.models.Card;
import org.justcards.android.models.User;

import java.util.List;

import static android.text.TextUtils.isEmpty;
import static org.justcards.android.utils.Constants.SINK_TAG;
import static org.justcards.android.utils.Constants.TABLE_TAG;

/**
 * Created by baphna on 12/2/2016.
 */
public class GameTableDB {

    private static final String TAG = GameTableDB.class.getSimpleName();
    private DatabaseReference tableDatabaseReference;
    private DatabaseReference sinkDatabaseReference;

    private static GameTableDB build() {
        return new GameTableDB();
    }

    private GameTableDB setTableDatabaseReference(DatabaseReference tableDatabaseReference) {
        this.tableDatabaseReference = tableDatabaseReference;
        return this;
    }

    private GameTableDB setSinkDatabaseReference(DatabaseReference sinkDatabaseReference) {
        this.sinkDatabaseReference = sinkDatabaseReference;
        return this;
    }

    public static GameTableDB getInstance(final String gameName) {
        return GameTableDB
                .build()
                .setTableDatabaseReference(FirebaseDatabase.getInstance().getReference(getTableObjectName(gameName)))
                .setSinkDatabaseReference(FirebaseDatabase.getInstance().getReference(getSinkObjectName(gameName)));
    }

    public static String getTableObjectName(final String gameName) {
        return gameName + "_" + TABLE_TAG;
    }

    public static String getSinkObjectName(final String gameName) {
        return gameName + "_" + SINK_TAG;
    }

    public void save(List<Card> cards, boolean toSink) {
        DatabaseReference dbRef = toSink ? sinkDatabaseReference : tableDatabaseReference;

        Query tableCardsQuery = dbRef.orderByKey().limitToLast(1);
        tableCardsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int keyNum = (dataSnapshot.getValue() == null) ? -1 : 0;
                for (DataSnapshot cardSnapShot : dataSnapshot.getChildren()) {
                    String key = cardSnapShot.getKey();
                    keyNum = isEmpty(key) ? -1 : Integer.valueOf(key);
                }
                for (Card card : cards) {
                    dbRef.child(String.valueOf(++keyNum)).setValue(card);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError != null) {
                    Log.e(TAG, "Firebase table cards query failed.");
                } else {
                    Log.d(TAG, "Firebase table cards query succeeded.");
                }
            }
        });
    }

    public void clear(boolean fromSink, final Context context) {
        DatabaseReference dbRef = fromSink ? sinkDatabaseReference : tableDatabaseReference;
        if (User.getCurrentUser(context).isDealer()) {
            dbRef.removeValue((databaseError, databaseReference) -> {
                if (databaseError != null) {
                    Log.e(TAG, "Firebase table cards delete all failed.");
                } else {
                    Log.d(TAG, "Firebase table cards delete all succeeded.");
                }
            });
        }
    }

    void clearAll(final Context context) {
        clear(false, context); // Remove Table
        clear(true, context); // Remove Sink
    }
}