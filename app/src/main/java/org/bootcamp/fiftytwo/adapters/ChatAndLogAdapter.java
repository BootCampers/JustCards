package org.bootcamp.fiftytwo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.fragments.ChatAndLogFragment;
import org.bootcamp.fiftytwo.models.ChatLog;

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

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onChat(holder.mItem);
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final View mView;
        ChatLog mItem;
        @BindView(R.id.ivAvatar) ImageView ivAvatar;
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