package org.justcards.android.interfaces;

public interface Observer {
    void onUpdate(Observable o, Object identifier, Object arg);
}