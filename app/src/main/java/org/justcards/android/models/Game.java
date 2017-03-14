package org.justcards.android.models;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;
import static org.justcards.android.utils.Constants.GAME_NAME;
import static org.justcards.android.utils.Constants.GAME_PREFS;

/**
 * Created by baphna on 11/26/2016.
 */
public class Game {

    private SharedPreferences mPreferences;

    private static Game build() {
        return new Game();
    }

    private Game setPreferences(SharedPreferences preferences) {
        this.mPreferences = preferences;
        return this;
    }

    public static Game getInstance(final Context context) {
        return Game.build().setPreferences(context.getSharedPreferences(GAME_PREFS, MODE_PRIVATE));
    }

    public void setName(final String gameName) {
        mPreferences.edit()
                .putString(GAME_NAME, gameName)
                .apply();
    }

    public String getName() {
        String gameName = mPreferences.getString(GAME_NAME, "");
        return !gameName.isEmpty() ? gameName : null;
    }
}