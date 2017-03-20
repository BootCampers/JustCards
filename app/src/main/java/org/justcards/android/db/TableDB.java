package org.justcards.android.db;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.justcards.android.models.Card;
import org.justcards.android.models.User;

import java.util.List;

import static android.text.TextUtils.isEmpty;
import static org.justcards.android.utils.AppUtils.getList;
import static org.justcards.android.utils.Constants.SINK_TAG;
import static org.justcards.android.utils.Constants.TABLE_TAG;

/**
 * Created by baphna on 12/2/2016.
 */
public class TableDB {

    private static final String TAG = TableDB.class.getSimpleName();

    private DatabaseReference mTableDbRef;
    private DatabaseReference mSinkDbRef;
    private OnEventListener mListener;

    public interface OnEventListener {
        void handleDealTable(final List<Card> cards);

        void handleDealSink(final List<Card> cards);
    }

    private TableDB setTableDbRef(DatabaseReference tableDbRef) {
        this.mTableDbRef = tableDbRef;
        return this;
    }

    private TableDB setSinkDbRef(DatabaseReference sinkDbRef) {
        this.mSinkDbRef = sinkDbRef;
        return this;
    }

    private TableDB setListener(OnEventListener listener) {
        this.mListener = listener;
        return this;
    }

    private static String getTableObjectName(final String gameName) {
        return gameName + "_" + TABLE_TAG;
    }

    private static String getSinkObjectName(final String gameName) {
        return gameName + "_" + SINK_TAG;
    }

    private static TableDB build() {
        return new TableDB();
    }

    public static TableDB getInstance(final String gameName) {
        return TableDB
                .build()
                .setTableDbRef(FirebaseDatabase.getInstance().getReference(getTableObjectName(gameName)))
                .setSinkDbRef(FirebaseDatabase.getInstance().getReference(getSinkObjectName(gameName)));
    }

    public TableDB observeOn(OnEventListener listener) {
        setListener(listener);

        mTableDbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousKey) {
                String key = dataSnapshot.getKey();
                Card card = dataSnapshot.getValue(Card.class);
                Log.d(TAG, "onChildAdded: tableDatabaseReference: key: " + key + " : card: " + card.getName());
                if (mListener != null) {
                    mListener.handleDealTable(getList(card));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousKey) {

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

        mSinkDbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                Card card = dataSnapshot.getValue(Card.class);
                Log.d(TAG, "onChildAdded: sinkDatabaseReference: key: " + key + " : card: " + card.getName());
                if (mListener != null) {
                    mListener.handleDealSink(getList(card));
                }
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

        return this;
    }

    public void pushCards(List<Card> cards, boolean toSink) {
        DatabaseReference dbRef = toSink ? mSinkDbRef : mTableDbRef;
        dbRef.orderByKey()
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
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
        DatabaseReference dbRef = fromSink ? mSinkDbRef : mTableDbRef;
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