package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.Constants;
import org.bootcamp.fiftytwo.utils.PlayerUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.bootcamp.fiftytwo.utils.AppUtils.loadRoundedImage;
import static org.bootcamp.fiftytwo.utils.Constants.DISPLAY_NAME;
import static org.bootcamp.fiftytwo.utils.Constants.USER_AVATAR_URI;
import static org.bootcamp.fiftytwo.utils.Constants.USER_ID;
import static org.bootcamp.fiftytwo.utils.Constants.USER_PREFS;
import static org.bootcamp.fiftytwo.utils.NetworkUtils.isNetworkAvailable;

public class RegisterActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String userAvatarURI = "";

    @BindView(R.id.userName) EditText usernameTextBox;
    @BindView(R.id.ivAvatar) ImageView avatarImageView;
    @BindView(R.id.edit_fab) FloatingActionButton browseButton;
    @BindView(R.id.registerBttn) Button registerButton;
    @BindView(R.id.register_form) ScrollView scrollView;
    @BindView(R.id.networkFailureBanner) RelativeLayout networkFailureBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        if (ParseUser.getCurrentUser() != null) {
            startWithCurrentUser();
        } else {
            loginToParse();
        }

        sharedPreferences = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        userAvatarURI = PlayerUtils.getDefaultAvatar();
        loadRoundedImage(this, avatarImageView, userAvatarURI);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String userName = sharedPreferences.getString(DISPLAY_NAME, "");

        if (isNetworkAvailable(RegisterActivity.this)) {
            notifyNetworkFailure(false);
            if (!userName.isEmpty()) {
                String userAvatarURI = sharedPreferences.getString(USER_AVATAR_URI, "");
                //TODO: get from Parse server??
                User user = new User(userAvatarURI, userName);
                Intent createGameIntent = new Intent(RegisterActivity.this, SelectGameActivity.class);
                createGameIntent.putExtra(USER_AVATAR_URI, user.getAvatarUri());
                createGameIntent.putExtra(DISPLAY_NAME, user.getDisplayName());
                createGameIntent.putExtra(USER_ID, ParseUser.getCurrentUser().getObjectId());
                startActivity(createGameIntent);
            }
        } else {
            notifyNetworkFailure(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            userAvatarURI = data.getStringExtra(Constants.SELECTED_AVATAR);
            Log.d(Constants.TAG, userAvatarURI);
            loadRoundedImage(this, avatarImageView, userAvatarURI);
        }
    }

    @OnClick(R.id.edit_fab)
    public void browse() {
        Intent intent = new Intent(RegisterActivity.this, AvatarSelectionActivity.class);
        startActivityForResult(intent, Constants.PICK_IMAGE_REQUEST, null);
    }

    @OnClick(R.id.registerBttn)
    public void register() {
        String username = usernameTextBox.getText().toString();
        String usernameSansWhiteSpace = username.replaceAll("\\s+", "");
        if (usernameSansWhiteSpace.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Username must have a value!", Toast.LENGTH_SHORT).show();
            usernameTextBox.requestFocus();
            scrollView.scrollTo(usernameTextBox.getScrollX(), usernameTextBox.getScrollY());
            return;
        }

        User user = new User(userAvatarURI, username);

        SharedPreferences userPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putString(DISPLAY_NAME, username);
        editor.putString(USER_AVATAR_URI, user.getAvatarUri());
        editor.putString(USER_ID, ParseUser.getCurrentUser().getObjectId());
        editor.apply();

        Intent createGameIntent = new Intent(RegisterActivity.this, SelectGameActivity.class);
        createGameIntent.putExtra(USER_AVATAR_URI, user.getAvatarUri());
        createGameIntent.putExtra(DISPLAY_NAME, user.getDisplayName());
        createGameIntent.putExtra(USER_ID, ParseUser.getCurrentUser().getObjectId());
        finish();
        startActivity(createGameIntent);
    }

    private void loginToParse() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e("DEBUG", "Anonymous loginToParse failed: ", e);
                } else {
                    startWithCurrentUser();
                }
            }
        });
    }

    private void startWithCurrentUser() {
        // Do Nothing
    }

    private void notifyNetworkFailure(boolean networkFailure) {
        if (networkFailure)
            networkFailureBanner.setVisibility(View.VISIBLE);
        else
            networkFailureBanner.setVisibility(View.GONE);

        browseButton.setEnabled(!networkFailure);
        registerButton.setEnabled(!networkFailure);
    }
}