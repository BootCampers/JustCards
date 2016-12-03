package org.bootcamp.fiftytwo.fragments;

import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.adapters.CardsAdapter;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.Constants;
import org.bootcamp.fiftytwo.views.OnTouchMoveListener;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.bootcamp.fiftytwo.utils.AppUtils.getParcelable;
import static org.bootcamp.fiftytwo.utils.AppUtils.isEmpty;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_LAYOUT_TYPE;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_PLAYER;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_X;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_Y;
import static org.bootcamp.fiftytwo.utils.Constants.TAG;

/**
 * Created by baphna on 11/11/2016.
 */
public class PlayerFragment extends CardsFragment{

    private int x;
    private int y;
    private User mPlayer;

    @BindView(R.id.ivPlayerAvatar) CircularImageView ivPlayerAvatar;
    @BindView(R.id.tvUserName) TextView tvUserName;
    @BindView(R.id.tvCardsCount) TextView tvCardsCount;
    @BindView(R.id.tvScore) TextView tvScore;

    public static PlayerFragment newInstance(List<Card> cards, final User player, final String tag, final String layoutType, int x, int y) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_CARDS, getParcelable(cards));
        args.putParcelable(PARAM_PLAYER, Parcels.wrap(player));
        args.putString(TAG, tag);
        args.putString(PARAM_LAYOUT_TYPE, layoutType);
        args.putInt(PARAM_X, x);
        args.putInt(PARAM_Y, y);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();

        List<Card> cards = new ArrayList<>();
        if (bundle != null) {
            cards = Parcels.unwrap(bundle.getParcelable(PARAM_CARDS));
            if (isEmpty(cards))
                cards = new ArrayList<>();
            mPlayer = Parcels.unwrap(bundle.getParcelable(PARAM_PLAYER));
            tag = bundle.getString(TAG);
            layoutType = bundle.getString(PARAM_LAYOUT_TYPE);
            x = bundle.getInt(PARAM_X);
            y = bundle.getInt(PARAM_Y);
        }
        mAdapter = new CardsAdapter(getContext(), cards, this, tag);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_player_with_cards, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (x > 0 && y > 0) {
            /*
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, Gravity.CENTER);
                view.setLayoutParams(params);
            */
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    x,
                    y,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT);
            view.setLayoutParams(params);

            view.setX(x);
            view.setY(y);
        }

        toggleCardsVisibility(mPlayer.isShowingCards());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tvUserName.setText(mPlayer.getDisplayName());

        Glide.with(getContext())
                .load(mPlayer.getAvatarUri())
                .error(R.drawable.ic_face)
                .into(ivPlayerAvatar);

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(mPlayer.getDisplayName());

        // Set Border
        ivPlayerAvatar.setBorderColor(color);
        ivPlayerAvatar.setBorderWidth(6);

        GradientDrawable countDrawable = (GradientDrawable) tvCardsCount.getBackground();
        countDrawable.setColor(color);
        tvCardsCount.setBackground(countDrawable);
        tvCardsCount.setAlpha(0.6f);
        tvCardsCount.setText(String.valueOf(mPlayer.getCards().size()));

        GradientDrawable scoreDrawable = (GradientDrawable) tvScore.getBackground();
        scoreDrawable.setColor(color);
        tvScore.setBackground(scoreDrawable);
        tvScore.setAlpha(0.6f);

        initCards();

        final ViewGroup container = (ViewGroup) view.getParent();
        view.setOnTouchListener(new OnTouchMoveListener(container));
    }

    public void cardCountChange(int newCount) {
        Log.d(Constants.TAG, PlayerFragment.class.getSimpleName()+"--" + mPlayer.getDisplayName() + "--cardCountChange--"+String.valueOf(newCount));
        tvCardsCount.setText(String.valueOf(newCount));
    }
    
}