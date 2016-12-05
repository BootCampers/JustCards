package org.bootcamp.fiftytwo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by baphna on 12/4/2016.
 */
public class RoundWinnersAdapter extends RecyclerView.Adapter<RoundWinnersAdapter.ViewHolder> {

    List<User> winners = new ArrayList<>();

    public RoundWinnersAdapter(List<User> winners) {
        this.winners = winners;
    }


    @Override
    public RoundWinnersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_winners, parent, false);
        return new RoundWinnersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RoundWinnersAdapter.ViewHolder holder, int position) {
        User user = winners.get(position);
        holder.tvPlayerName.setText(user.getDisplayName());
        Glide.with(holder.ivPlayerAvatar.getContext())
                .load(user.getAvatarUri())
                .error(R.drawable.ic_face)
                .into(holder.ivPlayerAvatar);
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(user.getDisplayName());

        // Set Border
        holder.ivPlayerAvatar.setBorderColor(color);
        holder.ivPlayerAvatar.setBorderWidth(Constants.CIRCULAR_BORDER_WIDTH);
    }

    @Override
    public int getItemCount() {
        return winners.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivPlayerAvatar)
        CircularImageView ivPlayerAvatar;
        @BindView(R.id.tvPlayerName)
        TextView tvPlayerName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
