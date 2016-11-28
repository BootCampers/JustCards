package org.bootcamp.fiftytwo.interfaces;

public interface Observer {
    void onUpdate(Observable o, Object identifier, Object arg);
}