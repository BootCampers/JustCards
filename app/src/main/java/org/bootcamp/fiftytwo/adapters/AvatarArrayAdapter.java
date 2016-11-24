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

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by baphna on 11/24/2016.
 */
public class AvatarArrayAdapter extends RecyclerView.Adapter<AvatarArrayAdapter.ViewHolder>{

    private Context mContext;
    private HashMap<String, Boolean> mAvatars;
    private String[] mAvatarUrls;
    private OnAvatarSelectedListener mListener;

    public interface OnAvatarSelectedListener {
        void onSelectedAvatar(String avatarUrl);
    }

    public AvatarArrayAdapter(Context context, HashMap<String, Boolean> avatars,
                              OnAvatarSelectedListener listener) {
        this.mContext = context;
        this.mAvatars = avatars;
        mListener = listener;
        mAvatarUrls = (String[]) mAvatars.keySet().toArray(new String[mAvatars.keySet().size()]);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String url = mAvatarUrls[position];
        Glide.with(holder.ivCard.getContext())
                .load(url)
                .centerCrop()
                .into(holder.ivCard);

        if(mAvatars.get(url) == true) {
            holder.ivSelected.setVisibility(View.VISIBLE);
            holder.flSelected.setSelected(true);
        } else {
            holder.ivSelected.setVisibility(View.INVISIBLE);
            holder.flSelected.setSelected(false);
        }
        holder.flSelected.setTag(url);
    }

    @Override
    public int getItemCount() {
        return mAvatars.size();
    }

    @Override
    public void onViewRecycled(AvatarArrayAdapter.ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.clear(holder.ivCard);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivCard) ImageView ivCard;
        @BindView(R.id.ivSelected) ImageView ivSelected;
        @BindView(R.id.flSelectCard)
        FrameLayout flSelected;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.flSelectCard)
        public void avatarSelected(){
            String selectedUrl = mAvatarUrls[getAdapterPosition()];
            mListener.onSelectedAvatar(selectedUrl);
            boolean isAlreadySelected = mAvatars.get(selectedUrl);
            if(isAlreadySelected == true){
                //make this unselected
                mAvatars.put(selectedUrl, false);
                notifyItemChanged(getAdapterPosition());
            } else {
                //make everything unselected and this as selected
                for (String url: mAvatars.keySet()) {
                    mAvatars.put(url, false);
                }
                mAvatars.put(selectedUrl, true);
                notifyDataSetChanged();
                //alternate is to remmeber last selected and toggle that
            }
        }
    }
}
