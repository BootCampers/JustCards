package org.bootcamp.fiftytwo.views;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.adapters.CardsAdapter;
import org.bootcamp.fiftytwo.models.Card;

import static org.bootcamp.fiftytwo.utils.Constants.DEALER_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.PLAYER_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.SINK_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.TABLE_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.TAG;

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

                    if (isMoveAllowed(sourceAdapter, targetAdapter)) {
                        int sourcePosition = getSourcePosition(event);
                        int targetPosition = getTargetPosition(v);
                        Card movingCard = sourceAdapter.getCards().get(sourcePosition);

                        sourceAdapter.remove(sourcePosition);
                        targetAdapter.add(movingCard, targetPosition);

                        v.setVisibility(View.VISIBLE);
                        if (v.getId() == R.id.tvNoCards) {
                            cardsListener.setEmptyList(false);
                        }

                        cardsListener.publish(sourceAdapter.getTag(), targetAdapter.getTag(), sourcePosition, targetPosition, movingCard);

                        // If source and target adapters are different then log otherwise this is shuffling within
                        if (!sourceAdapter.getTag().endsWith(targetAdapter.getTag())) {
                            Log.d(TAG, sourceAdapter.getTag() + "--" + targetAdapter.getTag());
                            cardsListener.logActivity(sourceAdapter.getTag(), "", sourceAdapter.getTag() + "--" + targetAdapter.getTag() + "--" + movingCard.getName());
                        }
                    }
                } else if (v.getId() == R.id.ivSink) {
                    isDropped = true;
                    CardsAdapter sourceAdapter = getSourceAdapter(event);

                    if (isSinkMoveAllowed(sourceAdapter)) {
                        int sourcePosition = getSourcePosition(event);
                        Card movingCard = sourceAdapter.getCards().get(sourcePosition);
                        sourceAdapter.remove(sourcePosition);
                        Log.d(TAG, sourceAdapter.getTag() + "--" + SINK_TAG);

                        cardsListener.publish(sourceAdapter.getTag(), SINK_TAG, sourcePosition, 0, movingCard);
                        cardsListener.logActivity(sourceAdapter.getTag(), "", sourceAdapter.getTag() + "--" + SINK_TAG + "--" + movingCard.getName());
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

    private boolean isMoveAllowed(final CardsAdapter source, final CardsAdapter target) {
        String sourceTag = source.getTag();
        String targetTag = target.getTag();
        if (!TextUtils.isEmpty(sourceTag) && !TextUtils.isEmpty(targetTag)) {
            if (sourceTag.equalsIgnoreCase(TABLE_TAG) && targetTag.equalsIgnoreCase(TABLE_TAG)) {
                Log.w(TAG, "isMoveAllowed: Attempted to move cards within " + sourceTag + " and " + targetTag + " which is not allowed");
                Toast.makeText(source.getContext(), "This move is not allowed", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private boolean isSinkMoveAllowed(final CardsAdapter source) {
        String sourceTag = source.getTag();
        return !TextUtils.isEmpty(sourceTag) && (sourceTag.equalsIgnoreCase(TABLE_TAG) || sourceTag.equalsIgnoreCase(PLAYER_TAG) || sourceTag.equalsIgnoreCase(DEALER_TAG));
    }
}