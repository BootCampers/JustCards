package org.justcards.android.activities;

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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseFacebookUtils;

import org.json.JSONException;
import org.justcards.android.R;
import org.justcards.android.models.User;
import org.justcards.android.services.RegistrationService;
import org.justcards.android.utils.AnimationUtils;
import org.justcards.android.utils.Constants;
import org.justcards.android.utils.PlayerUtils;
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.justcards.android.utils.AppUtils.loadRoundedImage;
import static org.justcards.android.utils.AppUtils.showSnackBar;
import static org.justcards.android.utils.Constants.PARAM_USER;
import static org.justcards.android.utils.Constants.PLAY_SERVICES_RESOLUTION_REQUEST;
import static org.justcards.android.utils.Constants.REQ_CODE_PICK_IMAGE;
import static org.justcards.android.utils.Constants.SELECTED_AVATAR;
import static org.justcards.android.utils.Constants.TAG;
import static org.justcards.android.utils.NetworkUtils.isNetworkAvailable;

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

        if(checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationService.class);
            startService(intent);
        }

        if (User.getCurrentUser() != null) {
            startWithCurrentUser();
        } else {
            loginToParse();
        }
    }

    /**
     * Check device to ensure it has Google Play Services APK.
     * If it doesn't, display a dialog that allows users to download the APK from the Google Play Store
     * Or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if(apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device does not have play services enabled.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void loginToParse() {
        ParseAnonymousUtils.logIn((user, e) -> {
            if (e != null) {
                Log.e("DEBUG", "Anonymous loginToParse failed: ", e);
            } else {
                Log.d("DEBUG", "Anonymous loginToParse succeeded");
                startWithCurrentUser();
            }
        });
    }

    private void startWithCurrentUser() {
        Log.d(TAG, "startWithCurrentUser: Parse User ID: " + User.getCurrentUser().getObjectId());
        User user = User.get(this);
        if (null != user) {
            Log.d(TAG, "startWithCurrentUser: Saved User: " + user);
            user.setUserId(User.getCurrentUser().getObjectId());
            user.save(this);
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
        ParseFacebookUtils.linkWithReadPermissionsInBackground(User.getCurrentUser(), this, permissions,
                (err) -> {
                    if (err != null) {
                        Log.d(Constants.TAG, "Uh oh. Error occurred: " + err.getMessage());
                        // TODO: Fix Issue of 'this auth is already used'
                    } else {
                        Toast.makeText(RegisterActivity.this, "Logged in with facebook account!", Toast.LENGTH_SHORT).show();
                        Log.d(Constants.TAG, "Linked user's facebook login with parse!");
                    }
                });
    }


    @OnClick(R.id.fabBrowseAvatar)
    public void browse() {
        Intent intent = new Intent(RegisterActivity.this, AvatarSelectionActivity.class);
        startActivityForResult(intent, REQ_CODE_PICK_IMAGE, null);
        AnimationUtils.enterVineTransition(this);
    }

    // Suggested by https://disqus.com/by/dominiquecanlas/
    private void getUserDetailsFromFB() {
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
                        Log.d(TAG, "getUserDetailsFromFB: response from facebook: " + response);

                        String profileName = response.getJSONObject().getString("name");
                        String profilePictureUrl = response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url");

                        Log.d(Constants.TAG, "Facebook profileName is:" + profileName);
                        Log.d(Constants.TAG, "Facebook image is: " + profilePictureUrl);
                        Log.d(TAG, "getUserDetailsFromFB: is facebook profile linked: " + ParseFacebookUtils.isLinked(User.getCurrentUser()));

                        User user = new User(profilePictureUrl, profileName, User.getCurrentUser().getObjectId());
                        Log.d(TAG, "getUserDetailsFromFB: Retrieved User: " + user);

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