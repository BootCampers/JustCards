package org.justcards.android.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.justcards.android.R;
import org.justcards.android.fragments.ChatAndLogFragment;
import org.justcards.android.models.ChatLog;
import org.justcards.android.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatAndLogAdapter extends RecyclerView.Adapter<ChatAndLogAdapter.ViewHolder> {

    private final List<ChatLog> mValues;
    private final ChatAndLogFragment.OnChatAndLogListener mListener;

    public ChatAndLogAdapter(List<ChatLog> items, ChatAndLogFragment.OnChatAndLogListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tvContent.setText(mValues.get(position).getDetails());

        Glide.with(holder.ivAvatar.getContext())
                .load(holder.mItem.getFromAvatar())
                .centerCrop()
                .into(holder.ivAvatar);

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(mValues.get(position).getFrom());

        // Set Border
        holder.ivAvatar.setBorderColor(color);
        holder.ivAvatar.setBorderWidth(Constants.CIRCULAR_BORDER_WIDTH);

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onChat(holder.mItem);
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final View mView;
        ChatLog mItem;

        @BindView(R.id.ivAvatar) CircularImageView ivAvatar;
        @BindView(R.id.tvContent) TextView tvContent;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvContent.getText().toString() + "'";
        }
    }
}