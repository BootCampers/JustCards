package org.bootcamp.fiftytwo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.Card;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Author: agoenka
 * Created At: 11/19/2016
 * Version: ${VERSION}
 */
public class PlayerCardsAdapter extends RecyclerView.Adapter<PlayerCardsAdapter.ViewHolder> {
    private final Context mContext;
    private final List<Card> mCards;

    public PlayerCardsAdapter(Context context, List<Card> cards) {
        this.mContext = context;
        this.mCards = cards;
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

    @Override
    public PlayerCardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_card, parent, false);
        return new PlayerCardsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlayerCardsAdapter.ViewHolder holder, int position) {
        Card card = mCards.get(position);

        holder.ivCard.setImageDrawable(null);
        Glide.with(holder.ivCard.getContext())
                .load(card.getDrawableBack())
                .bitmapTransform(new RoundedCornersTransformation(holder.ivCard.getContext(), 20, 0))
                .fitCenter()
                .into(holder.ivCard);
    }

    @Override
    public void onViewRecycled(PlayerCardsAdapter.ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.clear(holder.ivCard);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivCard) ImageView ivCard;
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
}