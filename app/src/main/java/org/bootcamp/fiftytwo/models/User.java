package org.bootcamp.fiftytwo.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by baphna on 11/13/2016.
 */

public class User implements Parcelable {

    private boolean isDealer;
    private String avatarUri;
    private String name;
    private int score;

    public User(boolean isDealer, String avatarUri, String name, int score) {
        this.isDealer = isDealer;
        this.avatarUri = avatarUri;
        this.name = name;
        this.score = score;
    }

    public boolean isDealer() {
        return isDealer;
    }

    public void setDealer(boolean dealer) {
        isDealer = dealer;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    //Parceler auto generated begins
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isDealer ? (byte) 1 : (byte) 0);
        dest.writeString(this.avatarUri);
        dest.writeString(this.name);
        dest.writeInt(this.score);
    }

    protected User(Parcel in) {
        this.isDealer = in.readByte() != 0;
        this.avatarUri = in.readString();
        this.name = in.readString();
        this.score = in.readInt();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    //Parceler auto generated ends
}
