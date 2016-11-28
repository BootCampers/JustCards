package org.bootcamp.fiftytwo.models;

/**
 * Created by baphna on 11/13/2016.
 */

public class ChatLog {

    private String from;
    private String details;
    private String fromAvatar;

    public ChatLog() {
    }

    public ChatLog(String from, String fromAvatar, String details) {
        this.from = from;
        this.fromAvatar = fromAvatar;
        this.details = details;
    }

    public String getFrom() {
        return from;
    }

    public String getDetails() {
        return details;
    }

    public String getFromAvatar() {
        return fromAvatar;
    }
}
