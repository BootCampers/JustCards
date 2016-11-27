package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.Constants;
import org.bootcamp.fiftytwo.utils.NetworkUtils;
import org.bootcamp.fiftytwo.utils.PlayerUtils;

import static org.bootcamp.fiftytwo.utils.Constants.DISPLAY_NAME;
import static org.bootcamp.fiftytwo.utils.Constants.USER_AVATAR_URI;
import static org.bootcamp.fiftytwo.utils.Constants.USER_ID;
import static org.bootcamp.fiftytwo.utils.Constants.USER_PREFS;

public class RegisterActivity extends AppCompatActivity {

    String userAvatarURI = "";
    EditText usernameTextBox;
    ImageView avatarImageView;
    FloatingActionButton browseButton;
    Button registerButton;
    ScrollView scrollView;
    RelativeLayout networkFailureBanner;
    User user = null;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (ParseUser.getCurrentUser() != null) {
            startWithCurrentUser();
        } else {
            loginToParse();
        }

        instantiateWidgets();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String userName = sharedPreferences.getString(DISPLAY_NAME, "");

        if (NetworkUtils.isNetworkAvailable(RegisterActivity.this)) {
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
            Glide.with(this)
                    .load(userAvatarURI)
                    .asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(avatarImageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            avatarImageView.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }
    }

    private void instantiateWidgets() {
        usernameTextBox = (EditText) findViewById(R.id.userName);
        avatarImageView = (ImageView) findViewById(R.id.ivAvatar);
        browseButton = (FloatingActionButton) findViewById(R.id.edit_fab);
        registerButton = (Button) findViewById(R.id.registerBttn);
        scrollView = (ScrollView) findViewById(R.id.register_form);
        networkFailureBanner = (RelativeLayout) findViewById(R.id.networkFailureBanner);

        userAvatarURI = PlayerUtils.getDefaultAvatar();

        Glide.with(this)
                .load(userAvatarURI)
                .asBitmap()
                .centerCrop()
                .into(new BitmapImageViewTarget(avatarImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        avatarImageView.setImageDrawable(circularBitmapDrawable);
                    }
                });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameTextBox.getText().toString();
                String usernameSansWhiteSpace = username.replaceAll("\\s+", "");
                if (usernameSansWhiteSpace.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Username must have a value!", Toast.LENGTH_SHORT).show();
                    usernameTextBox.requestFocus();
                    scrollView.scrollTo(usernameTextBox.getScrollX(), usernameTextBox.getScrollY());
                    return;
                }

                user = new User(userAvatarURI, username);

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
        });

        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, AvatarSelectionActivity.class);
                startActivityForResult(intent, Constants.PICK_IMAGE_REQUEST, null);
            }
        });
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