package org.bootcamp.fiftytwo.application;

import android.app.Application;
import android.util.Log;

import com.bumptech.glide.request.target.ViewTarget;
import com.parse.Parse;
import com.parse.interceptors.ParseLogInterceptor;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.interfaces.Observable;
import org.bootcamp.fiftytwo.interfaces.Observer;
import org.bootcamp.fiftytwo.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baphna on 11/18/2016.
 */
public class FiftyTwoApplication extends Application implements Observable {

    public static final String APPLICATION_ID = "fiftyTwoAppId";
    public static final String APPLICATION_SERVER = "http://fiftytwo.herokuapp.com/parse/";
    @SuppressWarnings("unused") public static final String CLIENT_KEY = "AIzaSyC2mQITmBqFHnUC3GmC0DlvFs4GoshJiCI";

    private List<Observer> mObservers = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        ViewTarget.setTagId(R.id.glide_tag);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APPLICATION_ID)
                .clientKey("bootCampers")
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

    public void notifyObservers(String identifier, Object arg) {
        for (Observer obs : mObservers) {
            obs.onUpdate(this, identifier, arg);
        }
    }

    public synchronized void removeAllObservers(){
        mObservers.clear();
    }
}