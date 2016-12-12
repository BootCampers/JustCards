package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseFacebookUtils;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.AnimationUtils;
import org.bootcamp.fiftytwo.utils.Constants;
import org.bootcamp.fiftytwo.utils.PlayerUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.bootcamp.fiftytwo.utils.AppUtils.loadRoundedImage;
import static org.bootcamp.fiftytwo.utils.AppUtils.showSnackBar;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_USER;
import static org.bootcamp.fiftytwo.utils.Constants.REQ_CODE_PICK_IMAGE;
import static org.bootcamp.fiftytwo.utils.Constants.SELECTED_AVATAR;
import static org.bootcamp.fiftytwo.utils.NetworkUtils.isNetworkAvailable;

public class RegisterActivity extends AppCompatActivity {

    private String userAvatarURI = "";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.etUserName) EditText etUserName;
    @BindView(R.id.ivAvatar) ImageView ivAvatar;
    @BindView(R.id.fabBrowseAvatar) FloatingActionButton fabBrowseAvatar;
    @BindView(R.id.btnRegister) Button btnRegister;
    @BindView(R.id.scrollViewRegister) ScrollView scrollViewRegister;
    @BindView(R.id.networkFailureBanner) RelativeLayout networkFailureBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (User.getCurrentUser() != null) {
            startWithCurrentUser();
        } else {
            loginToParse();
        }
    }

    private void loginToParse() {
        ParseAnonymousUtils.logIn((user, e) -> {
            if (e != null) {
                Log.e("DEBUG", "Anonymous loginToParse failed: ", e);
            } else {
                startWithCurrentUser();
            }
        });
    }

    private void startWithCurrentUser() {
        User user = User.get(this);
        if (null != user) {
            startSelectGame(user);
            overridePendingTransition(0, 0);
        } else {
            setContentView(R.layout.activity_register);
            ButterKnife.bind(this);
            setSupportActionBar(toolbar);

            if (isNetworkAvailable(this)) {
                notifyNetworkFailure(false);
                userAvatarURI = PlayerUtils.getDefaultAvatar();
                loadRoundedImage(this, ivAvatar, userAvatarURI);
            } else {
                notifyNetworkFailure(true);
            }
        }
    }

    @OnClick(R.id.btnFbLogin)
    public void fbLogin(View view) {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("email");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions,
                (user, err) -> {
                    if (err != null) {
                        Log.d(Constants.TAG, "Uh oh. Error occurred" + err.toString());
                    } else if (user == null) {
                        Log.d(Constants.TAG, "Uh oh. The user cancelled the Facebook login.");
                    } else if (user.isNew()) {
                        Log.d(Constants.TAG, "User signed up and logged in through Facebook!");
                    } else {
                        Toast.makeText(RegisterActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                        Log.d(Constants.TAG, "User logged in through Facebook!");
                    }
                });
    }


    @OnClick(R.id.fabBrowseAvatar)
    public void browse() {
        Intent intent = new Intent(RegisterActivity.this, AvatarSelectionActivity.class);
        startActivityForResult(intent, REQ_CODE_PICK_IMAGE, null);
        AnimationUtils.enterVineTransition(this);
    }

    private void getUserDetailsFromFB() {
        // Suggested by https://disqus.com/by/dominiquecanlas/
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name,picture.type(large)");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                response -> {
                    /* handle the result */
                    try {
                        String profileName = response.getJSONObject().getString("name");
                        Log.d(Constants.TAG, "Facebook profileName is:" + profileName);

                        JSONObject picture = response.getJSONObject().getJSONObject("picture");
                        JSONObject data = picture.getJSONObject("data");
                        // Returns a large profile picture
                        String pictureUrl = data.getString("url");
                        Log.d(Constants.TAG, "Facebook image is: " + pictureUrl);

                        User user = new User(pictureUrl, profileName, User.getCurrentUser().getObjectId());
                        user.save(this);
                        startSelectGame(user);
                        AnimationUtils.enterVineTransition(this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        ).executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQ_CODE_PICK_IMAGE) {
                userAvatarURI = data.getStringExtra(SELECTED_AVATAR);
                Log.d(Constants.TAG, userAvatarURI);
                loadRoundedImage(this, ivAvatar, userAvatarURI);
            } else {
                ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
                getUserDetailsFromFB();
            }
        }
    }

    @OnClick(R.id.btnRegister)
    public void register(final View view) {
        String username = etUserName.getText().toString().replaceAll("\\s+", "");
        if (username.isEmpty()) {
            showSnackBar(getApplicationContext(), view, "Username must have a value!");
            etUserName.requestFocus();
            scrollViewRegister.scrollTo(etUserName.getScrollX(), etUserName.getScrollY());
            return;
        }

        User user = new User(userAvatarURI, username, User.getCurrentUser().getObjectId());
        user.save(this);

        startSelectGame(user);
        AnimationUtils.enterVineTransition(this);
    }

    private void startSelectGame(final User user) {
        Intent selectGameIntent = new Intent(this, SelectGameActivity.class);
        selectGameIntent.putExtra(PARAM_USER, Parcels.wrap(user));
        finish();
        startActivity(selectGameIntent);
    }

    private void notifyNetworkFailure(final boolean networkFailure) {
        networkFailureBanner.setVisibility(networkFailure ? View.VISIBLE : View.GONE);
        fabBrowseAvatar.setEnabled(!networkFailure);
        btnRegister.setEnabled(!networkFailure);
    }
}