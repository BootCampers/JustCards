package org.justcards.android.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.justcards.android.adapters.CardsAdapter;
import org.justcards.android.models.Card;
import org.justcards.android.models.GameRules;
import org.justcards.android.models.User;

import static org.justcards.android.network.ParseUtils.isSelf;
import static org.justcards.android.utils.Constants.DEALER_TAG;
import static org.justcards.android.utils.Constants.PLAYER_TAG;
import static org.justcards.android.utils.Constants.SINK_TAG;
import static org.justcards.android.utils.Constants.TABLE_TAG;
import static org.justcards.android.utils.Constants.TAG;

/**
 * Author: agoenka
 * Created At: 12/3/2016
 * Version: ${VERSION}
 */
public class RuleUtils {

    /**
     * Card Exchange Rules:
     * <p>
     * ------          Dealer	Player  Table   Player View
     * Dealer	        X	    Y(D)	Y(DT)	Y(D)
     * Player	        NP	    Y(P)	Y(CT)	NP
     * Table	        NP	    Y(CT)	NA	    NP
     * Player View	    NP	    NP	    NP	    NP
     * <p>
     * Legends:
     * X	No Broadcast
     * Y	Broadcast
     * NA	Move Not Allowed
     * NP	Move Not Possible
     * <p>
     * Values in parentheses describe the corresponding APIs in use with the legends being:
     * D   Deal
     * DT  Deal Table
     * CT  Card Table
     * P   Within Player
     * <p>
     * Tags for different Card Fragments:
     * 1.DEALER_TAG
     * 2.PLAYER_TAG
     * 3.TABLE_TAG
     * 4.Custom Player View Tag (player.getDisplayName() + "_" + player.getUserId())
     */

    private RuleUtils() {
        //no instance
    }

    public static boolean isCardMoveAllowed(final CardsAdapter source, final CardsAdapter target) {
        String sourceTag = source.getTag();
        String targetTag = target.getTag();
        if (!TextUtils.isEmpty(sourceTag) && !TextUtils.isEmpty(targetTag)) {
            if (isPlayerNotEligible(source.getContext(), sourceTag)) {
                Log.w(TAG, "isCardMoveAllowed: A not eligible player attempted cards moves in the current round");
                handleNotAllowed(source.getContext(), "This move is not allowed since you're not playing in this round any more!");
            } else if ((TABLE_TAG.equalsIgnoreCase(sourceTag) && TABLE_TAG.equalsIgnoreCase(targetTag))
                    || isFloatingPlayerTag(sourceTag)
                    || isFloatingPlayerTag(targetTag)) {
                Log.w(TAG, "isCardMoveAllowed: Attempted to move cards within " + sourceTag + " and " + targetTag + " which is not allowed");
                handleNotAllowed(source.getContext(), "This move is not allowed!");
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean isCardSinkDropAllowed(final CardsAdapter source) {
        String sourceTag = source.getTag();
        if (isPlayerNotEligible(source.getContext(), sourceTag)) {
            Log.w(TAG, "isCardMoveAllowed: A not eligible player attempted to drop cards to sink");
            handleNotAllowed(source.getContext(), "This card cannot be dropped to sink since you're not playing in this round any more!!");
            return false;
        } else if (!TextUtils.isEmpty(sourceTag)
                && (TABLE_TAG.equalsIgnoreCase(sourceTag)
                || PLAYER_TAG.equalsIgnoreCase(sourceTag)
                || DEALER_TAG.equalsIgnoreCase(sourceTag))) {
            return true;
        } else {
            Log.w(TAG, "isCardMoveAllowed: Attempted to drop cards from " + sourceTag + " to the sink which is not allowed");
            handleNotAllowed(source.getContext(), "This card cannot be dropped to sink!");
            return false;
        }
    }

    public static boolean isCardViewable(final Context context, final Card card, final String tag) {
        if (card != null && !TextUtils.isEmpty(tag)) {
            if (DEALER_TAG.equalsIgnoreCase(tag)) {
                return false;
            } else if (PLAYER_TAG.equalsIgnoreCase(tag)) {
                return true;
            } else if (TABLE_TAG.equalsIgnoreCase(tag) && !isPlayerNotEligible(context, tag) && GameRules.get(context).isViewTableCardAllowed()) {
                return true;
            } else if (isFloatingPlayerTag(tag)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isToggleCardBroadcastRequired(final String tag) {
        return !TextUtils.isEmpty(tag) && TABLE_TAG.equalsIgnoreCase(tag);
    }

    private static boolean isFloatingPlayerTag(final String tag) {
        return !TextUtils.isEmpty(tag)
                && !DEALER_TAG.equalsIgnoreCase(tag)
                && !TABLE_TAG.equalsIgnoreCase(tag)
                && !PLAYER_TAG.equalsIgnoreCase(tag)
                && !SINK_TAG.equalsIgnoreCase(tag)
                && tag.contains("_");
    }

    public static void handleNotAllowed(final Context context, final String message) {
        new MediaUtils(context).playNotAllowedTone();
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private static boolean isPlayerNotEligible(final Context context, final String tag) {
        User self = User.getCurrentUser(context);
        return (self.isShowingCards() || !self.isActive()) && !DEALER_TAG.equalsIgnoreCase(tag);
    }

    public static boolean isPlayerNotEligibleForDeal(final User player, final boolean doDealSelf) {
        return !player.isActive() || player.isShowingCards() || (!doDealSelf && isSelf(player));
    }

}