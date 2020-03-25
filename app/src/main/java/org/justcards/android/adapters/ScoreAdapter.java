package org.justcards.android.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.justcards.android.R;
import org.justcards.android.models.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by baphna on 11/26/2016.
 */
public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {

    private final List<User> users;

    public ScoreAdapter(List<User> users) {
        this.users = users;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvPlayerName.setText(user.getDisplayName());
        holder.tvScore.setText(String.valueOf(user.getScore()));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ibAdd) ImageButton ibAdd;
        @BindView(R.id.ibReduce) ImageButton ibReduce;
        @BindView(R.id.tvPlayerName) TextView tvPlayerName;
        @BindView(R.id.tvScore) TextView tvScore;

        int currentScore = 0;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.ibAdd)
        synchronized void increaseScore() {
            currentScore = Integer.parseInt(tvScore.getText().toString());
            currentScore++;
            tvScore.setText(String.valueOf(currentScore));
            users.get(getAdapterPosition()).setScore(currentScore);
        }

        @OnClick(R.id.ibReduce)
        synchronized void decreaseScore() {
            currentScore = Integer.parseInt(tvScore.getText().toString());
            currentScore--;
            tvScore.setText(String.valueOf(currentScore));
            users.get(getAdapterPosition()).setScore(currentScore);
        }
    }
}