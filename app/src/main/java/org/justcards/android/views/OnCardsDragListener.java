package org.justcards.android.views;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;

import org.justcards.android.R;
import org.justcards.android.adapters.CardsAdapter;
import org.justcards.android.models.Card;
import org.justcards.android.models.User;

import static org.justcards.android.utils.Constants.SINK_TAG;
import static org.justcards.android.utils.Constants.TAG;
import static org.justcards.android.utils.RuleUtils.isCardMoveAllowed;
import static org.justcards.android.utils.RuleUtils.isCardSinkDropAllowed;

/**
 * Author: agoenka
 * Created At: 11/21/2016
 * Version: ${VERSION}
 */
public class OnCardsDragListener implements View.OnDragListener {

    private boolean isDropped = false;
    private CardsAdapter.CardsListener cardsListener;

    public OnCardsDragListener(CardsAdapter.CardsListener cardsListener) {
        this.cardsListener = cardsListener;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                break;
            case DragEvent.ACTION_DROP:
                // Handling only drag drop between lists for now
                if (v.getId() == R.id.ivCard || v.getId() == R.id.tvNoCards) {
                    isDropped = true;
                    CardsAdapter sourceAdapter = getSourceAdapter(event);
                    CardsAdapter targetAdapter = getTargetAdapter(v);

                    if (isCardMoveAllowed(sourceAdapter, targetAdapter)) {
                        int sourcePosition = getSourcePosition(event);
                        int targetPosition = getTargetPosition(v);
                        Card movingCard = sourceAdapter.getCards().get(sourcePosition);

                        sourceAdapter.remove(sourcePosition);
                        targetAdapter.add(movingCard, targetPosition);

                        v.setVisibility(View.VISIBLE);
                        if (v.getId() == R.id.tvNoCards) {
                            cardsListener.setEmptyList(false);
                        }

                        cardsListener.exchange(sourceAdapter.getTag(), targetAdapter.getTag(), sourcePosition, targetPosition, movingCard);

                        // If source and target adapters are different then log otherwise this is shuffling within
                        if (!sourceAdapter.getTag().endsWith(targetAdapter.getTag())) {
                            Log.d(TAG, sourceAdapter.getTag() + "--" + targetAdapter.getTag());
                            User self = User.getCurrentUser(v.getContext());
                            if(movingCard.isShowingFront()){
                                cardsListener.logActivity(self.getDisplayName(), self.getAvatarUri(),
                                        self.getDisplayName() + " moved " + movingCard.getName().toLowerCase().replace("_", " ") +
                                                " to " + targetAdapter.getTag().toLowerCase().replace("_", " ") );
                            } else {
                                cardsListener.logActivity(self.getDisplayName(), self.getAvatarUri(),
                                        self.getDisplayName() + " moved a card to " +
                                                targetAdapter.getTag().toLowerCase().replace("_", " "));
                            }
                        }
                    }
                } else if (v.getId() == R.id.ivSink) {
                    isDropped = true;
                    CardsAdapter sourceAdapter = getSourceAdapter(event);

                    if (isCardSinkDropAllowed(sourceAdapter)) {
                        int sourcePosition = getSourcePosition(event);
                        Card movingCard = sourceAdapter.getCards().get(sourcePosition);
                        sourceAdapter.remove(sourcePosition);
                        Log.d(TAG, sourceAdapter.getTag() + "--" + SINK_TAG);

                        cardsListener.exchange(sourceAdapter.getTag(), SINK_TAG, sourcePosition, 0, movingCard);
                        User self = User.getCurrentUser(v.getContext());
                        if(movingCard.isShowingFront()){
                            cardsListener.logActivity(self.getDisplayName(), self.getAvatarUri(),
                                    self.getDisplayName() + " moved " + movingCard.getName().toLowerCase().replace("_", " ") +
                                            " to sink");
                        } else {
                            cardsListener.logActivity(self.getDisplayName(), self.getAvatarUri(),
                                    self.getDisplayName() + " moved a card to sink");
                        }
                    }
                }
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                break;
            default:
                break;
        }

        if (!isDropped) {
            View vw = (View) event.getLocalState();
            vw.setVisibility(View.VISIBLE);
        }

        return true;
    }

    private CardsAdapter getSourceAdapter(DragEvent e) {
        View view = (View) e.getLocalState();
        RecyclerView source = (RecyclerView) view.getParent().getParent();
        return (CardsAdapter) source.getAdapter();
    }

    private CardsAdapter getTargetAdapter(View v) {
        // Card's parent is a LinearLayout and it's parent is a RecyclerView
        RecyclerView target =
                v.getId() == R.id.tvNoCards
                        ? (RecyclerView) ((ViewGroup) v.getParent()).findViewById(R.id.rvCardsList)
                        : (RecyclerView) v.getParent().getParent();
        return (CardsAdapter) target.getAdapter();
    }

    private int getSourcePosition(DragEvent event) {
        View view = (View) event.getLocalState();
        return (int) view.getTag();
    }

    private int getTargetPosition(View v) {
        return v.getId() != R.id.tvNoCards ? (int) v.getTag() : -1;
    }
}