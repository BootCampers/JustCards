package org.justcards.android.network;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.justcards.android.interfaces.Callback;
import org.justcards.android.models.User;

import java.util.List;

/**
 * Created by baphna on 2/22/2017.
 *
 * Entries will be like this:
 *   -Game1
 *      --user1
 *      --user2
 *   -Game2
 *      --user2
 *      --user2
 */

public class FirebaseDB {

    private static final String TAG = FirebaseDB.class.getSimpleName();

    public interface OnGameExistsListener {
        void onGameExistsResult(final boolean result);
    }

    public static void checkGameExists(final String gameName, final FirebaseDB.OnGameExistsListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(gameName);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    //game exists
                    Log.d(TAG, gameName + " Exist");
                    listener.onGameExistsResult(true);
                } else {
                    //game does not exist
                    Log.d(TAG, gameName + " does Not exist");
                    listener.onGameExistsResult(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Existing players can continue to play the game even after dealer leaves. No one else can join.
     *
     * @param gameName the game number that needs to be deleted
     */
    public static void deleteGame(final String gameName) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(gameName);
        myRef.removeValue();
    }

    public static void findUsers(final Context context, final String gameName, final Callback<List<User>> callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(gameName);
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG,"added");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}

