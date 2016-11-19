package org.bootcamp.fiftytwo.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

@ParseClassName("User")
@Parcel(analyze = User.class)
public class User  extends ParseObject {

    public User() {
        super();
    }

    public User(String avatarUri, String name) {
        super();
        setAvatarUri(avatarUri);
        setName(name);
    }

    public String getAvatarUri() {
        return getString("avatarUri");
    }

    public void setAvatarUri(String avatarUri) {
        put("avatarUri", avatarUri);
    }

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public static User getUser() {
        return new User("", "Ankit");
    }

    public static User getUsers() {
        return new User("", "Ankit");
    }
}