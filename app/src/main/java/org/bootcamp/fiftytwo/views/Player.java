package org.bootcamp.fiftytwo.views;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.fragments.PlayerFragment;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.CardUtil;

import java.util.ArrayList;
import java.util.List;

import static org.bootcamp.fiftytwo.utils.Constants.PLAYER_TAG;

/**
 * Author: agoenka
 * Created At: 11/18/2016
 * Version: ${VERSION}
 */
public class Player {

    private Player() {
        //no instance
    }

    public static void addPlayer(@NonNull final Fragment fragment, final User player) {
        List<User> players = new ArrayList<>();
        players.add(player);
        addPlayers(fragment, players);
    }

    public static void addPlayers(@NonNull final Fragment fragment, final List<User> players) {
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
            addPlayerFragment(fragment, player, (int) x, (int) y);
        }
    }

    private static void addPlayerFragment(Fragment fragment, User player, int x, int y) {
        List<Card> cards = CardUtil.generateDeck(1, false).subList(0, 2);
        Fragment playerCardsFragment = PlayerFragment.newInstance(cards, player, PLAYER_TAG + player.getDisplayName(), x, y);

        fragment.getChildFragmentManager()
                .beginTransaction()
                .add(R.id.flDealerViewContainer, playerCardsFragment, PLAYER_TAG + player.getDisplayName())
                .commitNow();
        fragment.getChildFragmentManager().executePendingTransactions();
    }
}