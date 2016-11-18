package org.bootcamp.fiftytwo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bootcamp.fiftytwo.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by baphna on 11/17/2016.
 */
public class GamesListAdapter extends RecyclerView.Adapter<GamesListAdapter.ViewHolder> {

    private Context mContext;
    private List<String> games;
    private String selectedChannelName = null;

    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public String getSelectedChannelName() {
        return selectedChannelName;
    }

    public GamesListAdapter(Context mContext, List<String> games) {
        this.mContext = mContext;
        this.games = games;

    }

    @Override
    public GamesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_games_around, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GamesListAdapter.ViewHolder holder, int position) {
        holder.tvGameName.setText(games.get(position));
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvGameName)
        TextView tvGameName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.tvGameName)
        public void gameSelected(View view){
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(itemView, position);
                }
            }
            selectedChannelName = tvGameName.getText().toString();
        }
    }
}
