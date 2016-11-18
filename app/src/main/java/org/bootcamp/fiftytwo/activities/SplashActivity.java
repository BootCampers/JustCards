package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.bootcamp.fiftytwo.models.User;

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

        if(userName.isEmpty()) {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }
        else{
            String userAvatarURI = sharedPreferences.getString(USER_AVATAR_URI, "");
            User user = new User(userAvatarURI, userName);
            Intent createGameIntent = new Intent(SplashActivity.this, CreateJoinGameActivity.class);
            createGameIntent.putExtra(USER_TAG, user.getObjectId());
            startActivity(createGameIntent);
        }
    }
}
