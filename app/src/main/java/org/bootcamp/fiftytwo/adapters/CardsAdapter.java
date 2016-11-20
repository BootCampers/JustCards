package org.bootcamp.fiftytwo.adapters;

import android.content.ClipData;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by baphna on 11/11/2016.
 */
public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    private Context mContext;
    private List<Card> cards;
    private CardsListener cardsListener;
    private String tag; //used for chat and log

    public interface CardsListener {
        void setEmptyList(boolean visibility);

        void logActivity(String whoPosted, String details);
    }

    public List<Card> getCards() {
        return cards;
    }

    private String getTag() {
        return tag;
    }

    public CardsAdapter(Context mContext, List<Card> cards, CardsListener cardsListener, String tag) {
        this.mContext = mContext;
        if (cards == null) {
            this.cards = new ArrayList<>();
        } else {
            this.cards = cards;
        }
        this.cardsListener = cardsListener;
        this.tag = tag;
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Card card = cards.get(position);
        //holder.ivCard.setImageDrawable(ContextCompat.getDrawable(mContext, card.getDrawable(mContext)));
        holder.ivCard.setImageDrawable(card.getDrawableBack(mContext));
        holder.ivCard.setTag(position); //Needed for drag and drop

        holder.ivCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            }
        });
        holder.ivCard.setOnDragListener(new DragListener(cardsListener));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivCard) ImageView ivCard;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public DragListener getDragInstance() {
        if (cardsListener != null) {
            return new DragListener(cardsListener);
        } else {
            return null;
        }
    }

    private class DragListener implements View.OnDragListener {

        boolean isDropped = false;
        CardsListener cardsListener;

        DragListener(CardsListener cardsListener) {
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
                    // TODO: handle drag and drop to a player
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
            adapter.cards = cards;
            adapter.notifyDataSetChanged();
            if (cards.size() == 0) {
                adapter.cardsListener.setEmptyList(true);
            }
        }
    }

}