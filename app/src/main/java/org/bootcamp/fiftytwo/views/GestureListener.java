package org.bootcamp.fiftytwo.views;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.adapters.CardsAdapter;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.utils.Constants;

import java.util.List;

/**
 * Created by baphna on 11/20/2016.
 */
public class GestureListener extends GestureDetector.SimpleOnGestureListener implements View.OnDragListener {

    private boolean isDropped = false;
    private CardsAdapter.CardsListener cardsListener;

    public GestureListener(CardsAdapter.CardsListener cardsListener) {
        this.cardsListener = cardsListener;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    // TODO: get which view is clicked
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(Constants.TAG, "Double tap " + e.toString());
        return true;
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
                // TODO: handle move if lists are empty
                if (v.getId() == R.id.ivCard || v.getId() == R.id.tvNoCards) {
                    isDropped = true;
                    CardsAdapter sourceAdapter = getSourceAdapter(event);
                    CardsAdapter targetAdapter = getTargetAdapter(v);
                    int sourcePosition = getSourcePosition(event);
                    int targetPosition = getTargetPosition(v);
                    Card movingCard = sourceAdapter.getCards().get(sourcePosition);

                    updateSource(sourceAdapter, sourcePosition);
                    updateTarget(targetAdapter, targetPosition, movingCard);

                    v.setVisibility(View.VISIBLE);
                    if (v.getId() == R.id.tvNoCards) {
                        cardsListener.setEmptyList(false);
                    }

                    // If source and target adapters are different then log otherwise this is shuffling within
                    if (!sourceAdapter.getTag().endsWith(targetAdapter.getTag())) {
                        Log.d(Constants.TAG, sourceAdapter.getTag() + "--" + targetAdapter.getTag());
                        cardsListener.logActivity("player", sourceAdapter.getTag() + "--" + targetAdapter.getTag() + "--" + movingCard.getName());
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

    private void updateSource(CardsAdapter adapter, int position) {
        List<Card> cards = adapter.getCards();
        cards.remove(position);
        updateCards(adapter, cards);
    }

    private void updateTarget(CardsAdapter adapter, int position, Card card) {
        List<Card> cards = adapter.getCards();
        if (position >= 0) {
            cards.add(position, card);
        } else {
            cards.add(card);
        }
        updateCards(adapter, cards);
    }

    private void updateCards(CardsAdapter adapter, List<Card> cards) {
        adapter.setCards(cards);
        adapter.notifyDataSetChanged();
        if (cards.size() == 0) {
            adapter.getCardsListener().setEmptyList(true);
        }
    }
}