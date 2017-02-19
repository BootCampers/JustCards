package org.justcards.android.interfaces;

public interface Observable {
    void addObserver(Observer obs);
    void deleteObserver(Observer obs);
}