package org.justcards.android.application;

import android.app.Application;
import android.util.Log;

import com.bumptech.glide.request.target.ViewTarget;

import org.justcards.android.R;
import org.justcards.android.interfaces.Observable;
import org.justcards.android.interfaces.Observer;
import org.justcards.android.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baphna on 11/18/2016.
 */
public class JustCardsAndroidApplication extends Application implements Observable {

    private List<Observer> mObservers = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        ViewTarget.setTagId(R.id.glide_tag);
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

    public synchronized void removeAllObservers() {
        mObservers.clear();
    }
}