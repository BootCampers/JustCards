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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by baphna on 11/11/2016.
 */

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    private Context mContext;
    private List<Card> cards;
    Listener listener;
    private String tag; //used for chat and log

    public List<Card> getCards() {
        return cards;
    }

    public CardsAdapter() {
    }

    public CardsAdapter(Context mContext, List<Card> cards, Listener listener, String tag) {
        this.mContext = mContext;
        this.cards = cards;
        this.listener = listener;
        this.tag = tag;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Card card = cards.get(position);
        //TODO set image as per card
        holder.ivCard.setImageDrawable(mContext.getDrawable(R.drawable.back));
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
        holder.ivCard.setOnDragListener(new DragListener(listener));

    }

    public interface Listener {
        void setEmptyList(boolean visibility);
        void logActivity(String whoPosted, String details);
    }

    public String getTag(){
        return tag;
    }

    public DragListener getDragInstance() {
        if (listener != null) {
            return new DragListener(listener);
        } else {
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivCard)
        ImageView ivCard;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class DragListener implements View.OnDragListener {

        boolean isDropped = false;
        Listener listener;

        public DragListener(Listener listener) {
            this.listener = listener;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;

                case DragEvent.ACTION_DRAG_ENTERED:
                    //v.setBackgroundColor(Color.LTGRAY);
                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    //v.setBackgroundColor(Color.YELLOW);
                    break;

                case DragEvent.ACTION_DROP:

                    isDropped = true;
                    int positionSource = -1;
                    int positionTarget = -1;

                    View viewSource = (View) event.getLocalState();

                    //Handling only drag drop between lists for now
                    //TODO: handle drag and drop to a player
                    //TODO: handle move if lists are empty
                    if (v.getId() == R.id.ivCard || v.getId() == R.id.tvNoCards) {

                        //Card's parent is Linearlayout and it's parent is recyclerview
                        RecyclerView target;
                        if (v.getId() == R.id.tvNoCards) {
                            target = (RecyclerView)
                                    v.getRootView().findViewById(R.id.rvCardsList);
                        } else {
                            target = (RecyclerView) v.getParent().getParent();
                            positionTarget = (int) v.getTag();
                        }

                        RecyclerView source = (RecyclerView) viewSource.getParent().getParent();
                        CardsAdapter adapterSource = (CardsAdapter) source.getAdapter();
                        positionSource = (int) viewSource.getTag();

                        Card movingCard = (Card) adapterSource.getCards().get(positionSource);
                        List<Card> cardListSource = adapterSource.getCards();

                        cardListSource.remove(positionSource);
                        adapterSource.updateCardsList(cardListSource, adapterSource);

                        CardsAdapter adapterTarget = (CardsAdapter) target.getAdapter();
                        List<Card> cardListTarget = adapterTarget.getCards();
                        if (positionTarget >= 0) {
                            cardListTarget.add(positionTarget, movingCard);
                        } else {
                            cardListTarget.add(movingCard);
                        }
                        adapterTarget.updateCardsList(cardListTarget, adapterTarget);
                        v.setVisibility(View.VISIBLE);

                        if (v.getId() == R.id.tvNoCards) {
                            listener.setEmptyList(false);
                        }

                        //If source and target adapters are different then log
                        //otherwise this is shuffling within
                        if(!adapterSource.getTag().endsWith(adapterTarget.getTag())) {
                            Log.d(Constants.TAG, adapterSource.getTag() + "--" + adapterTarget.getTag());
                            listener.logActivity("player" ,adapterSource.getTag() + "--" + adapterTarget.getTag()
                                    + "--" + movingCard.getName());
                        }

                    }
                    break;

                case DragEvent.ACTION_DRAG_ENDED:
                    //v.setBackgroundColor(0);
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

    }

    public void updateCardsList(List<Card> cardList, CardsAdapter adapter) {
        this.cards = cardList;
        adapter.notifyDataSetChanged();
        if(cardList.size() == 0){
            listener.setEmptyList(true);
        }
    }
}
