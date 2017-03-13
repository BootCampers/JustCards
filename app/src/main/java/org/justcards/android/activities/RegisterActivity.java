package org.justcards.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseFacebookUtils;

import org.json.JSONException;
import org.justcards.android.R;
import org.justcards.android.models.User;
import org.justcards.android.services.messaging.RegistrationService;
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

public class RegisterActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    private static final int RC_SIGN_IN = 9001;
    private String userAvatarURI = "";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.etUserName) EditText etUserName;
    @BindView(R.id.ivAvatar) ImageView ivAvatar;
    @BindView(R.id.fabBrowseAvatar) FloatingActionButton fabBrowseAvatar;
    @BindView(R.id.btnRegister) Button btnRegister;
    @BindView(R.id.btnGoogleSignIn) SignInButton btnGoogleSignIn;
    @BindView(R.id.scrollViewRegister) ScrollView scrollViewRegister;
    @BindView(R.id.networkFailureBanner) RelativeLayout networkFailureBanner;

    private GoogleApiClient mGoogleApiClient;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

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

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
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
        if (requestCode == REQ_CODE_PICK_IMAGE) {
            userAvatarURI = data.getStringExtra(SELECTED_AVATAR);
            Log.d(Constants.TAG, userAvatarURI);
            loadRoundedImage(this, ivAvatar, userAvatarURI);
        } else if (requestCode == RC_SIGN_IN /*Google sign in*/) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign-In failed
                Toast.makeText(getApplicationContext(), "Google Sign-In failed.", Toast.LENGTH_LONG).show();
            }
        } else {
            ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
            getUserDetailsFromFB();
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
    }

    private void startSelectGame(final User user) {
        Intent selectGameIntent = new Intent(this, SelectGameActivity.class);
        selectGameIntent.putExtra(PARAM_USER, Parcels.wrap(user));
        finish();
        startActivity(selectGameIntent);
        AnimationUtils.enterVineTransition(this);
    }

    private void notifyNetworkFailure(final boolean networkFailure) {
        networkFailureBanner.setVisibility(networkFailure ? View.VISIBLE : View.GONE);
        fabBrowseAvatar.setEnabled(!networkFailure);
        btnRegister.setEnabled(!networkFailure);
    }

    @OnClick(R.id.btnGoogleSignIn)
    public void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithCredential", task.getException());
                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        //Login success
                        mFirebaseUser = mFirebaseAuth.getCurrentUser();
                        if (mFirebaseUser.getPhotoUrl() != null) {
                            User user = new User(mFirebaseUser.getPhotoUrl().toString(),
                                    mFirebaseUser.getDisplayName(),
                                    mFirebaseUser.getUid());
                            user.save(getApplicationContext());
                            startSelectGame(user);
                        } else {
                            //TODO: Give a random image to the user and start game
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}