package org.bootcamp.fiftytwo.utils;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.bootcamp.fiftytwo.adapters.CardsAdapter;
import org.bootcamp.fiftytwo.models.Card;

import static org.bootcamp.fiftytwo.utils.Constants.DEALER_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.PLAYER_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.TABLE_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.TAG;

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
            if (TABLE_TAG.equalsIgnoreCase(sourceTag) && TABLE_TAG.equalsIgnoreCase(targetTag)) {
                Log.w(TAG, "isCardMoveAllowed: Attempted to move cards within " + sourceTag + " and " + targetTag + " which is not allowed");
                Toast.makeText(source.getContext(), "This move is not allowed", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean isCardSinkDropAllowed(final CardsAdapter source) {
        String sourceTag = source.getTag();
        return !TextUtils.isEmpty(sourceTag) && (TABLE_TAG.equalsIgnoreCase(sourceTag) || PLAYER_TAG.equalsIgnoreCase(sourceTag) || DEALER_TAG.equalsIgnoreCase(sourceTag));
    }

    public static boolean isCardViewable(final Card card, String tag) {
        if (card != null && !TextUtils.isEmpty(tag)) {
            if (DEALER_TAG.equalsIgnoreCase(tag)) {
                return false;
            } else if (PLAYER_TAG.equalsIgnoreCase(tag)) {
                return true;
            } else if (TABLE_TAG.equalsIgnoreCase(tag)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isToggleCardBroadcastRequired(String tag) {
        return !TextUtils.isEmpty(tag) && TABLE_TAG.equalsIgnoreCase(tag);
    }
}