package org.justcards.android.application;

import android.app.Application;
import android.util.Log;

import com.bumptech.glide.request.target.ViewTarget;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;

import org.justcards.android.R;
import org.justcards.android.interfaces.Observable;
import org.justcards.android.interfaces.Observer;
import org.justcards.android.models.Game;
import org.justcards.android.models.GameTable;
import org.justcards.android.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baphna on 11/18/2016.
 */
public class JustCardsAndroidApplication extends Application implements Observable {

    public static final String APPLICATION_ID = "codepath-android";
    public static final String APPLICATION_SERVER = "https://codepath-maps-push-lab.herokuapp.com/parse/";
    @SuppressWarnings("unused") public static final String CLIENT_KEY = "8bXPznF5eSLWq0sY9gTUrEF5BJlia7ltmLQFRh";

    private List<Observer> mObservers = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        ViewTarget.setTagId(R.id.glide_tag);

        ParseObject.registerSubclass(Game.class);
        ParseObject.registerSubclass(GameTable.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APPLICATION_ID)
                .clientKey("")
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server(APPLICATION_SERVER)
                .build());

        // ParseFacebookUtils should initialize the Facebook SDK for you
        ParseFacebookUtils.initialize(this);
    }

    @Override
    public synchronized void addObserver(Observer obs) {
        Log.i(Constants.TAG, "addObserver(" + obs + ")");
        if (mObservers.indexOf(obs) < 0) {
            mObservers.add(obs);
        }
    }

    @Override
    public synchronized void deleteObserver(Observer obs) {
        Log.i(Constants.TAG, "deleteObserver(" + obs + ")");
        mObservers.remove(obs);
    }

    public void notifyObservers(String identifier, Object arg) {
        for (Observer obs : mObservers) {
            obs.onUpdate(this, identifier, arg);
        }
    }

    public synchronized void removeAllObservers(){
        mObservers.clear();
    }
}