package org.bootcamp.fiftytwo.views;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

import org.bootcamp.fiftytwo.fragments.PlayerFragment;
import org.bootcamp.fiftytwo.models.User;

import java.util.ArrayList;
import java.util.List;

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

    public static void addPlayer(@NonNull final Fragment fragment, final int containerResId, final User player) {
        List<User> players = new ArrayList<>();
        players.add(player);
        addPlayers(fragment, containerResId, players);
    }

    public static void addPlayers(@NonNull final Fragment fragment, final int containerResId, final List<User> players) {
        View decorView = fragment.getActivity().getWindow().getDecorView();
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
            addPlayerFragment(fragment, containerResId, player, (int) x, (int) y);
        }
    }

    private static void addPlayerFragment(final Fragment fragment, final int containerResId, final User player, int x, int y) {
        Fragment playerCardsFragment = PlayerFragment.newInstance(null, player, PLAYER_TAG + player.getDisplayName(), x, y);

        fragment.getChildFragmentManager()
                .beginTransaction()
                .add(containerResId, playerCardsFragment, getPlayerFragmentTag(player))
                .commitNow();
        fragment.getChildFragmentManager().executePendingTransactions();
    }

    public static String getPlayerFragmentTag(User player) {
        return player.getDisplayName() + "_" + player.getUserId();
    }
}