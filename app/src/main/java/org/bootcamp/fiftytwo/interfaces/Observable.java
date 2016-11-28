package org.bootcamp.fiftytwo.interfaces;

public interface Observable {
    void addObserver(Observer obs);
    void deleteObserver(Observer obs);
}