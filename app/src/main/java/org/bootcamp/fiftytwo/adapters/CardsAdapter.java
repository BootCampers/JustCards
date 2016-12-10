package org.bootcamp.fiftytwo.adapters;

import android.content.ClipData;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.AnimationUtils;
import org.bootcamp.fiftytwo.utils.RuleUtils;
import org.bootcamp.fiftytwo.views.OnCardsDragListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.bootcamp.fiftytwo.utils.AppUtils.isEmpty;
import static org.bootcamp.fiftytwo.utils.Constants.TAG;
import static org.bootcamp.fiftytwo.utils.RuleUtils.isCardViewable;
import static org.bootcamp.fiftytwo.utils.RuleUtils.isToggleCardBroadcastRequired;

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
        void exchange(String fromTag, String toTag, int fromPosition, int toPosition, Card card);
        void logActivity(String whoPosted, String fromAvatar, String details);
        void cardCountChange(int newCount);
        void toggleCard(Card card, int position, String onTag);
    }

    public List<Card> getCards() {
        return mCards;
    }

    public void setCards(List<Card> cards) {
        this.mCards = cards;
        notifyDataSetChanged();
    }

    public String getTag() {
        return tag;
    }

    public Context getContext() {
        return mContext;
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

        Glide.with(mContext)
                .load(card.isShowingFront() ? card.getDrawable(mContext) : card.getDrawableBack())
                .crossFade()
                .into(holder.ivCard);

        holder.ivCard.setTag(position); //Needed for drag and drop

        holder.ivCard.setOnLongClickListener(view -> {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            // noinspection deprecation
            view.startDrag(data, shadowBuilder, view, 0);
            view.setVisibility(View.INVISIBLE);
            return true;
        });
        holder.ivCard.setOnDragListener(new OnCardsDragListener(cardsListener));

        holder.ivCard.setOnClickListener(view -> {
            card.setViewAllowed(isCardViewable(mContext, card, getTag()));
            if (!card.isViewAllowed()) {
                RuleUtils.handleNotAllowed(mContext, "This card is not allowed to be flipped!");
            } else {
                AnimationUtils.animateFlip(mContext, holder.ivCard, () -> {
                    Glide.with(mContext)
                            .load(card.isShowingFront() ? card.getDrawableBack() : card.getDrawable(mContext))
                            .into(holder.ivCard);

                    card.setShowingFront(!card.isShowingFront());
                    if (card.isShowingFront()) {
                        User self = User.getCurrentUser(mContext);
                        cardsListener.logActivity(self.getDisplayName(), self.getAvatarUri(), "Looking at cards in the " + tag + " section");
                    }
                    if (isToggleCardBroadcastRequired(getTag())) {
                        cardsListener.toggleCard(card, (int) holder.ivCard.getTag(), getTag());
                    }
                });
            }
        });
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.clear(holder.ivCard);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivCard) public ImageView ivCard;

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

    public void add(Card card, int position) {
        if (position >= 0 && position < getItemCount()) {
            mCards.add(position, card);
        } else {
            Log.d(TAG, "Problem here " + position);
            mCards.add(card);
        }
        notifyDataSetChanged();
        cardsListener.setEmptyList(isEmpty(mCards));
        cardsListener.cardCountChange(mCards.size());
    }

    public Card remove(int position) {
        Card card = null;
        if (position < getItemCount()) {
            card = mCards.remove(position);
            notifyDataSetChanged();
            cardsListener.setEmptyList(isEmpty(mCards));
        }
        cardsListener.cardCountChange(mCards.size());
        return card;
    }

    public void addAll(List<Card> cards) {
        if (!isEmpty(cards)) {
            mCards.addAll(cards);
            notifyDataSetChanged();
            cardsListener.setEmptyList(getItemCount() == 0);
        }
        cardsListener.cardCountChange(mCards.size());
    }

    public void removeAll(List<Card> cards) {
        if (!isEmpty(cards)) {
            mCards.removeAll(cards);
            notifyDataSetChanged();
            cardsListener.setEmptyList(getItemCount() == 0);
        }
        cardsListener.cardCountChange(mCards.size());
    }

    public void clear() {
        mCards.clear();
        notifyDataSetChanged();
        cardsListener.setEmptyList(getItemCount() == 0);
        cardsListener.cardCountChange(mCards.size());
    }
}