package org.bootcamp.fiftytwo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new SelectCardsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Card card = mCards.get(position);
        holder.ivCard.setImageResource(android.R.color.transparent);
        holder.ivCard.setImageDrawable(null);
        Glide.with(holder.ivCard.getContext())
                .load(card.getDrawable(getContext()))
                .fitCenter()
                .bitmapTransform(new RoundedCornersTransformation(holder.ivCard.getContext(), 20, 0))
                .into(holder.ivCard);
        holder.ivCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Clicked on Card", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.clear(holder.ivCard);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivCard) ImageView ivCard;
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}