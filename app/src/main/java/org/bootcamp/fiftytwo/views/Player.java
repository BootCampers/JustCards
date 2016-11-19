package org.bootcamp.fiftytwo.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.User;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Author: agoenka
 * Created At: 11/18/2016
 * Version: ${VERSION}
 */
public class Player {

    private Player() {
        //no instance
    }

    public static ViewGroup addPlayer(Context context, final ViewGroup container, final User player, int resId, int x, int y) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final ViewGroup playerLayout = (ViewGroup) inflater.inflate(resId, null);
        setPlayerAttributes(playerLayout, player);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                x,
                y,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);

        playerLayout.setX(x);
        playerLayout.setY(y);

        container.addView(playerLayout, params);

        playerLayout.setOnTouchListener(new View.OnTouchListener() {
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
        });

        return playerLayout;
    }

    public static ViewGroup addPlayers(Context context, final ViewGroup container, final List<User> players, int resId) {
        View decorView = ((Activity) context).getWindow().getDecorView();
        int screenWidth = decorView.getWidth();
        int screenHeight = decorView.getHeight();

        double startX = screenWidth * .04;
        double endX = screenWidth * .8;
        double y = screenHeight * .15;
        double x = startX;
        double rangeX = endX - startX;
        double incX = rangeX/(players.size() + 1);

        ViewGroup layout = null;

        for (User player : players) {
            x += incX;
            layout = addPlayer(context, container, player, resId, (int) x, (int) y);
        }

        return layout;
    }

    private static void setPlayerAttributes(ViewGroup playerLayout, User player) {
        TextView tvUserName = (TextView) playerLayout.findViewById(R.id.tvUserName);
        CircularImageView ivPlayerAvatar = (CircularImageView) playerLayout.findViewById(R.id.ivPlayerAvatar);

        if (!TextUtils.isEmpty(player.getName())) {
            tvUserName.setText(player.getName());
        }

        Glide.with(tvUserName.getContext())
                .load(player.getAvatarUri())
                .error(R.drawable.ic_face)
                .into(ivPlayerAvatar);
    }
}