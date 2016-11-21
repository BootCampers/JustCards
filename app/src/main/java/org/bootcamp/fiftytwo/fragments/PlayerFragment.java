package org.bootcamp.fiftytwo.fragments;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.adapters.CardsAdapter;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.User;
import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.bootcamp.fiftytwo.utils.CardUtil.getParcelable;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_PLAYER;
import static org.bootcamp.fiftytwo.utils.Constants.TAG;
import static org.bootcamp.fiftytwo.utils.Constants.X;
import static org.bootcamp.fiftytwo.utils.Constants.Y;

/**
 * Created by baphna on 11/11/2016.
 */
public class PlayerFragment extends CardsListFragment {

    private int x;
    private int y;
    private User mPlayer;

    @BindView(R.id.ivPlayerAvatar) CircularImageView ivPlayerAvatar;
    @BindView(R.id.tvUserName) TextView tvUserName;

    public static PlayerFragment newInstance(final List<Card> cards, final User player, final String tag, int x, int y) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_CARDS, getParcelable(cards));
        args.putParcelable(PARAM_PLAYER, Parcels.wrap(player));
        args.putString(TAG, tag);
        args.putInt(X, x);
        args.putInt(Y, y);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCards = Parcels.unwrap(bundle.getParcelable(PARAM_CARDS));
            mPlayer = Parcels.unwrap(bundle.getParcelable(PARAM_PLAYER));
            tag = bundle.getString(TAG);
            x = bundle.getInt(X);
            y = bundle.getInt(Y);
        }
        mAdapter = new CardsAdapter(getContext(), mCards, this, tag);
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

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tvUserName.setText(mPlayer.getName());

        Glide.with(getContext())
                .load(mPlayer.getAvatarUri())
                .error(R.drawable.ic_face)
                .into(ivPlayerAvatar);

        initCards();

        final ViewGroup container = (ViewGroup) view.getParent();
        view.setOnTouchListener(getTouchListener(container));
    }

    private View.OnTouchListener getTouchListener(final ViewGroup container) {
        return new View.OnTouchListener() {
            float dX, dY;
            float newX, newY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        newX = event.getRawX() + dX;
                        newY = event.getRawY() + dY;

                        if (newX < 0)
                            newX = 0;
                        if (newY < 0)
                            newY = 0;

                        if (newX + view.getWidth() > container.getWidth())
                            newX = container.getWidth() - view.getWidth();
                        if (newY + view.getHeight() > container.getHeight())
                            newY = container.getHeight() - view.getHeight();

                        view.animate()
                                .x(newX)
                                .y(newY)
                                .setDuration(0)
                                .start();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        };
    }
}