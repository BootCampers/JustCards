package org.bootcamp.fiftytwo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.Card;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by baphna on 11/11/2016.
 */

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    private Context mContext;
    private List<Card> cards;

    public CardsAdapter() {
    }

    public CardsAdapter(Context mContext, List<Card> cards) {
        this.mContext = mContext;
        this.cards = cards;
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
}
