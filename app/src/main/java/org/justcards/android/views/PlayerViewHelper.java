package org.justcards.android.views;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import org.justcards.android.activities.GameViewManagerActivity;
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

    public static void addPlayers(@NonNull final GameViewManagerActivity activity, final int containerResId, final List<User> players) {
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
        Log.d(TAG, "addPlayerFragment: FragmentManager: " + fm + " : playerFragmentTag: " + getPlayerFragmentTag(player));

        Fragment playerCardsFragment = PlayerFragment.newInstance(null, player, getPlayerCardsAdapterTag(player), LAYOUT_TYPE_STAGGERED_HORIZONTAL, x, y);

        fm.beginTransaction()
                .add(containerResId, playerCardsFragment, getPlayerFragmentTag(player))
                .commitNow();

        fm.executePendingTransactions();
    }

    private static String getPlayerFragmentTag(final User player) {
        return player.getDisplayName() + "_" + player.getUserId();
    }

    private static String getPlayerCardsAdapterTag(final User player) {
        return PLAYER_TAG + "_" + player.getDisplayName();
    }

    public static Fragment getPlayerFragment(FragmentActivity activity, User player) {
        return activity.getSupportFragmentManager().findFragmentByTag(getPlayerFragmentTag(player));
    }
}