package org.justcards.android.network;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.justcards.android.models.User;

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
     * Entry for game is deleted in DB.
     *
     * @param gameName the game number that needs to be deleted
     */
    public static void deleteGame(final String gameName) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(gameName);
        myRef.removeValue();
    }

    /**
     * Delete only this user from this game
     * @param gameName
     * @param user
     */
    public static void deleteGamesForUser(final String gameName, final User user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(gameName);
        myRef.orderByChild("userId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot children : dataSnapshot.getChildren()){
                    if(children.getValue(User.class).equals(user)){
                        Log.d(TAG, "Removed " + user.getDisplayName() + " from " + gameName);
                        children.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Failed to remove " + user.getDisplayName() + " from " + gameName);
            }
        });

    }
}

