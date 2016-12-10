package org.bootcamp.fiftytwo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.Card;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class SelectCardsAdapter extends RecyclerView.Adapter<SelectCardsAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Card> mCards;

    public SelectCardsAdapter(Context context, List<Card> cards) {
        this.mContext = context;
        this.mCards = cards;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_card, parent, false);
        return new SelectCardsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Card card = mCards.get(position);

        holder.ivCard.setImageDrawable(null);
        Glide.with(getContext())
                .load(card.getDrawable(getContext()))
                .bitmapTransform(new RoundedCornersTransformation(getContext(), 20, 0))
                .fitCenter()
                .crossFade()
                .into(holder.ivCard);

        if(card.isSelected()) {
            holder.ivSelected.setVisibility(View.VISIBLE);
            holder.flSelected.setSelected(true);
        } else {
            holder.ivSelected.setVisibility(View.INVISIBLE);
            holder.flSelected.setSelected(false);
        }
        holder.flSelected.setTag(card);

        holder.flSelected.setOnClickListener(v -> {
            Card c = (Card) v.getTag();
            if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                if (holder.ivSelected.getVisibility() == View.VISIBLE) {
                    holder.ivSelected.setVisibility(View.INVISIBLE);
                    v.setSelected(false);
                    c.setSelected(false);
                } else {
                    holder.ivSelected.setVisibility(View.VISIBLE);
                    v.setSelected(true);
                    c.setSelected(true);
                }
            }
        });
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.clear(holder.ivCard);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivCard) ImageView ivCard;
        @BindView(R.id.ivSelected) ImageView ivSelected;
        @BindView(R.id.flSelectCard) FrameLayout flSelected;
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void addAll(List<Card> cards) {
        int currentSize = getItemCount();
        mCards.addAll(cards);
        notifyItemRangeInserted(currentSize, mCards.size() - currentSize);
    }

    public List<Card> getCards() {
        return mCards;
    }
}