package org.justcards.android.db;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.justcards.android.models.User;

import java.util.List;

import cz.msebera.android.httpclient.util.TextUtils;

import static org.justcards.android.utils.AppUtils.getList;
import static org.justcards.android.utils.AppUtils.isEmpty;

/**
 * Author: agoenka
 * Created At: 3/19/2017
 * Version: 1.1
 */
public class UsersDB {

    private static final String TAG = UsersDB.class.getSimpleName();

    private DatabaseReference mUsersDbRef;
    private OnEventListener mListener;
    private ChildEventListener mChildEventListener;

    public interface OnEventListener {
        void handlePlayersAdded(final List<User> players);

        void handlePlayersRemoved(final List<User> players);

        void handlePlayerChanged(final User player);
    }

    private UsersDB setUsersDbRef(DatabaseReference usersDbRef) {
        this.mUsersDbRef = usersDbRef;
        return this;
    }

    private UsersDB setListener(OnEventListener listener) {
        this.mListener = listener;
        return this;
    }

    private static UsersDB build() {
        return new UsersDB();
    }

    public static UsersDB getInstance(final String gameName) {
        return UsersDB
                .build()
                .setUsersDbRef(FirebaseDatabase.getInstance().getReference(gameName));
    }

    public UsersDB observeOn(OnEventListener listener) {
        setListener(listener);

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousKey) {
                //Get previously joined players
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "onChildAdded " + user.getDisplayName() + " " + dataSnapshot.getKey());
                if (mListener != null) {
                    mListener.handlePlayersAdded(getList(user));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousKey) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "onChildChanged " + user.getDisplayName() + " " + dataSnapshot.getKey());
                if (mListener != null) {
                    mListener.handlePlayerChanged(user);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "onChildRemoved " + user.getDisplayName() + "--" + dataSnapshot.getKey());
                if (mListener != null) {
                    mListener.handlePlayersRemoved(getList(user));
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousKey) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "onChildMoved " + user.getDisplayName() + " " + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getDetails());
            }
        };

        mUsersDbRef.addChildEventListener(mChildEventListener);

        return this;
    }

    public void observeOff() {
        if (mChildEventListener != null) {
            mUsersDbRef.removeEventListener(mChildEventListener);
        }
    }

    public void save(final User user) {
        if (user != null && !TextUtils.isEmpty(user.getUserId())) {
            mUsersDbRef.child(user.getUserId()).setValue(user);
        }
    }

    public void save(final List<User> users) {
        if (!isEmpty(users)) {
            for (User user : users) {
                save(user);
            }
        }
    }

    public void setScore(final User user) {
        if (user != null && !TextUtils.isEmpty(user.getUserId())) {
            mUsersDbRef.child(user.getUserId()).child("score").setValue(user.getScore());
        }
    }

    public void setShowingCards(final User user) {
        if (user != null && !TextUtils.isEmpty(user.getUserId())) {
            mUsersDbRef.child(user.getUserId()).child("showingCards").setValue(user.isShowingCards());
        }
    }

    public void setActive(final User user) {
        if (user != null && !TextUtils.isEmpty(user.getUserId())) {
            mUsersDbRef.child(user.getUserId()).child("active").setValue(user.isActive());
        }
    }
}