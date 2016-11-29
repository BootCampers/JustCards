package org.bootcamp.fiftytwo.views;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;

import org.bootcamp.fiftytwo.fragments.PlayerFragment;
import org.bootcamp.fiftytwo.models.User;

import java.util.List;

import static org.bootcamp.fiftytwo.utils.Constants.LAYOUT_TYPE_STAGGERED_HORIZONTAL;
import static org.bootcamp.fiftytwo.utils.Constants.PLAYER_TAG;

/**
 * Author: agoenka
 * Created At: 11/18/2016
 * Version: ${VERSION}
 */
public class PlayerViewHelper {

    private PlayerViewHelper() {
        //no instance
    }

    public static void addPlayers(@NonNull final FragmentActivity activity, final int containerResId, final List<User> players) {
        View decorView = activity.getWindow().getDecorView();
        int screenWidth = decorView.getWidth();
        int screenHeight = decorView.getHeight();

        double startX = screenWidth * .04;
        double endX = screenWidth * .8;
        double y = screenHeight * .15;
        double x = startX;
        double rangeX = endX - startX;
        double incX = rangeX / (players.size() + 1);

        for (User player : players) {
            x += incX;
            addPlayerFragment(activity.getSupportFragmentManager(), containerResId, player, (int) x, (int) y);
        }
    }

    private static void addPlayerFragment(@NonNull final FragmentManager fm, final int containerResId, final User player, int x, int y) {
        Fragment playerCardsFragment = PlayerFragment.newInstance(null, player, PLAYER_TAG + player.getDisplayName(), LAYOUT_TYPE_STAGGERED_HORIZONTAL, x, y);

        fm.beginTransaction()
                .add(containerResId, playerCardsFragment, getPlayerFragmentTag(player))
                .commitNow();

        fm.executePendingTransactions();
    }

    private static String getPlayerFragmentTag(User player) {
        return player.getDisplayName() + "_" + player.getUserId();
    }

    public static Fragment getPlayerFragment(FragmentActivity activity, User player) {
        return activity.getSupportFragmentManager().findFragmentByTag(getPlayerFragmentTag(player));
    }
}