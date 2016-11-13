package org.bootcamp.fiftytwo.models;

/**
 * Created by baphna on 11/13/2016.
 */

public class ChatLog {

    private String from;
    private String details;

    public ChatLog() {
    }

    public ChatLog(String from, String details) {
        this.from = from;
        this.details = details;
    }

    public String getFrom() {
        return from;
    }

    public String getDetails() {
        return details;
    }
}
