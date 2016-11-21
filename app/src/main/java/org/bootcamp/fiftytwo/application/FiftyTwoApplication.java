package org.bootcamp.fiftytwo.application;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.interceptors.ParseLogInterceptor;

import org.bootcamp.fiftytwo.interfaces.Observable;
import org.bootcamp.fiftytwo.interfaces.Observer;
import org.bootcamp.fiftytwo.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baphna on 11/18/2016.
 */
public class FiftyTwoApplication extends Application
        implements Observable {

    public static final String APPLICATION_ID = "codepath-android";
    public static final String APPLICATION_SERVER = "https://codepath-maps-push-lab.herokuapp.com/parse/";
    public static final String CLIENT_KEY = "8bXPznF5eSLWq0sY9gTUrEF5BJlia7ltmLQFRh";
    private List<Observer> mObservers = new ArrayList<Observer>();

    @Override
    public void onCreate() {
        super.onCreate();

//        ParseObject.registerSubclass(User.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APPLICATION_ID)
                .clientKey("")
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server(APPLICATION_SERVER)
                .build());
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

    public void notifyObservers(Object arg) {
        for (Observer obs : mObservers) {
            obs.onUpdate(this, arg);
        }
    }
}