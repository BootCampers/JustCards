package org.bootcamp.fiftytwo.adapters;

import android.content.ClipData;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.views.OnCardsDragListener;
import org.bootcamp.fiftytwo.views.OnGestureListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.bootcamp.fiftytwo.utils.AppUtils.isEmpty;

/**
 * Created by baphna on 11/11/2016.
 */
public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    private Context mContext;
    private List<Card> mCards;
    private CardsListener cardsListener;
    private String tag; //used for chat and log

    public interface CardsListener {
        void setEmptyList(boolean visibility);

        void logActivity(String whoPosted, String details);
    }

    public CardsListener getCardsListener() {
        return cardsListener;
    }

    public List<Card> getCards() {
        return mCards;
    }

    public void setCards(List<Card> cards) {
        this.mCards = cards;
    }

    public String getTag() {
        return tag;
    }

    public CardsAdapter(Context context, List<Card> cards, CardsListener cardsListener, String tag) {
        this.mContext = context;
        if (cards == null) {
            this.mCards = new ArrayList<>();
        } else {
            this.mCards = cards;
        }
        this.cardsListener = cardsListener;
        this.tag = tag;
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Card card = mCards.get(position);
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
        holder.ivCard.setOnDragListener(new OnCardsDragListener(cardsListener));

        holder.ivCard.setOnTouchListener(new OnGestureListener(mContext) {
            @Override
            public void onDoubleTap(MotionEvent event) {
                if (card.isShowingFront()) {
                    Glide.with(mContext)
                            .load(card.getDrawableBack())
                            .into(holder.ivCard);
                    card.setShowingFront(false);
                } else {
                    Glide.with(mContext)
                            .load(card.getDrawable(mContext))
                            .into(holder.ivCard);
                    card.setShowingFront(true);
                }
                //TODO: log event and send broadcast
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivCard) ImageView ivCard;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public OnCardsDragListener getDragInstance() {
        if (cardsListener != null) {
            return new OnCardsDragListener(cardsListener);
        } else {
            return null;
        }
    }

    public void addAll(List<Card> cards) {
        if (!isEmpty(cards)) {
            mCards.addAll(cards);
            notifyDataSetChanged();
        }
    }
}