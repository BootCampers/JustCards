package org.justcards.android.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.justcards.android.R;

import static android.content.Context.MODE_PRIVATE;
import static org.justcards.android.utils.Constants.RULE_PREFS;
import static org.justcards.android.utils.Constants.RULE_VIEW_TABLE_CARD;

/**
 * Author: agoenka
 * Created At: 12/7/2016
 * Version: ${VERSION}
 */
public class GameRules {

    private boolean isViewTableCardAllowed;

    private static GameRules build() {
        return new GameRules();
    }

    public boolean isViewTableCardAllowed() {
        return isViewTableCardAllowed;
    }

    public GameRules setViewTableCardAllowed(boolean viewTableCardAllowed) {
        isViewTableCardAllowed = viewTableCardAllowed;
        return this;
    }

    public static GameRules get(final Context context) {
        SharedPreferences rulePrefs = context.getSharedPreferences(RULE_PREFS, MODE_PRIVATE);
        if (rulePrefs != null) {
            boolean isViewTableCardAllowed = rulePrefs.getBoolean(RULE_VIEW_TABLE_CARD, true);
            return GameRules.build().setViewTableCardAllowed(isViewTableCardAllowed);
        } else {
            return GameRules.build();
        }
    }

    public void save(final Context context) {
        context.getSharedPreferences(RULE_PREFS, MODE_PRIVATE)
                .edit()
                .putBoolean(RULE_VIEW_TABLE_CARD, isViewTableCardAllowed)
                .apply();
    }

    public static String getRuleDescription(final String code, final Context context) {
        if (!TextUtils.isEmpty(code)) {
            if (RULE_VIEW_TABLE_CARD.equalsIgnoreCase(code)) {
                return context.getResources().getString(R.string.rule_view_table_card);
            }
        }
        return "";
    }

    @Override
    public String toString() {
        return "GameRules {isViewTableCardAllowed: " + isViewTableCardAllowed + "}";
    }

}