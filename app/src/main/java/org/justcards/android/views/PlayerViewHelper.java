package org.justcards.android.views;

import android.graphics.Point;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.Display;
import android.view.View;

import org.justcards.android.fragments.PlayerFragment;
import org.justcards.android.models.User;

import java.util.List;

import static org.justcards.android.utils.Constants.LAYOUT_TYPE_STAGGERED_HORIZONTAL;
import static org.justcards.android.utils.Constants.PLAYER_TAG;

/**
 * Author: agoenka
 * Created At: 11/18/2016
 * Version: ${VERSION}
 */
public class PlayerViewHelper {

    private static final String TAG = PlayerViewHelper.class.getSimpleName();

    private PlayerViewHelper() {
        //no instance
    }

    public static void addPlayers(@NonNull final FragmentActivity activity, final int containerResId, final List<User> players, final int currentNoOfPlayers) {
        Position position = getPosition(activity, currentNoOfPlayers);
        int x = position.x;
        FragmentManager fm = activity.getSupportFragmentManager();

        for (User player : players) {
            Fragment playerFragment = getPlayerFragment(activity, player);
            if (playerFragment == null) {
                x += position.incX;
                playerFragment = PlayerFragment.newInstance(null, player, getPlayerCardsAdapterTag(player), LAYOUT_TYPE_STAGGERED_HORIZONTAL, x, position.y);
            }
            if (!playerFragment.isInLayout() && !playerFragment.isAdded()) {
                fm.beginTransaction()
                        .add(containerResId, playerFragment, getPlayerFragmentTag(player))
                        .commitNow();
                fm.executePendingTransactions();
            }
        }
    }

    private static String getPlayerFragmentTag(final User player) {
        String playerFragmentTag = player.getDisplayName() + "_" + player.getUserId();
        Log.i(TAG, "Player fragment tag is: " + playerFragmentTag);
        return playerFragmentTag;
    }

    private static String getPlayerCardsAdapterTag(final User player) {
        return PLAYER_TAG + "_" + player.getDisplayName();
    }

    public static Fragment getPlayerFragment(FragmentActivity activity, User player) {
        return activity.getSupportFragmentManager().findFragmentByTag(getPlayerFragmentTag(player));
    }

    private static Position getPosition(final FragmentActivity activity, final int currentNoOfPlayers) {
        View decorView = activity.getWindow().getDecorView();
        int width = decorView.getWidth();
        int height = decorView.getHeight();
        Log.d(TAG, "getPosition: width: from decorView: " + width);
        Log.d(TAG, "getPosition: height: from decorView: " + height);

        if (width == 0 && height == 0) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;
            Log.d(TAG, "getPosition: width: from Default Display: " + width);
            Log.d(TAG, "getPosition: height: from Default Display: " + height);
        }

        int maxPlayers = Math.max(currentNoOfPlayers, 5);
        double startX = width * .04;
        double endX = width * .7;
        double y = height * .08;
        double rangeX = endX - startX;
        double incX = (rangeX * currentNoOfPlayers) / maxPlayers;

        return new Position((int) startX, (int) y, (int) incX);
    }

    private static class Position {
        int x;
        int y;
        int incX;
        Position(int x, int y, int incX) {
            this.x = x;
            this.y = y;
            this.incX = incX;
        }
    }
}