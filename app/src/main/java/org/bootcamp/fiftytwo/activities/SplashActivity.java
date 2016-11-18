package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.Constants;

import static org.bootcamp.fiftytwo.utils.Constants.USERNAME;
import static org.bootcamp.fiftytwo.utils.Constants.USER_AVATAR_URI;
import static org.bootcamp.fiftytwo.utils.Constants.USER_PREFS;
import static org.bootcamp.fiftytwo.utils.Constants.USER_TAG;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String userName = sharedPreferences.getString(USERNAME, "");

        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();
        } else { // If not logged in, login as a new anonymous user
            login();
        }

        if(userName.isEmpty()) {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }
        else{
            String userAvatarURI = sharedPreferences.getString(USER_AVATAR_URI, "");
            //TODO: get from Parese server??
            User user = new User(userAvatarURI, userName);
            Intent createGameIntent = new Intent(SplashActivity.this, CreateJoinGameActivity.class);
            createGameIntent.putExtra(USER_TAG, user.getObjectId());
            startActivity(createGameIntent);
        }
    }

    // Get the userId from the cached currentUser object
    void startWithCurrentUser() {
        // TODO:
    }

    // Create an anonymous user using ParseAnonymousUtils and set sUserId
    void login() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(Constants.TAG, "Anonymous login failed: ", e);
                } else {
                    startWithCurrentUser();
                }
            }
        });
    }
}
