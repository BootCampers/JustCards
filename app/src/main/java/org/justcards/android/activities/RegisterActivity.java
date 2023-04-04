package org.justcards.android.activities;

import static org.justcards.android.utils.AppUtils.loadRoundedImage;
import static org.justcards.android.utils.AppUtils.showSnackBar;
import static org.justcards.android.utils.Constants.PARAM_USER;
import static org.justcards.android.utils.Constants.PLAY_SERVICES_RESOLUTION_REQUEST;
import static org.justcards.android.utils.Constants.REQ_CODE_PICK_IMAGE;
import static org.justcards.android.utils.Constants.SELECTED_AVATAR;
import static org.justcards.android.utils.NetworkUtils.isNetworkAvailable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.justcards.android.R;
import org.justcards.android.messaging.services.RegistrationService;
import org.justcards.android.models.User;
import org.justcards.android.utils.AnimationUtilsJC;
import org.justcards.android.utils.Constants;
import org.justcards.android.utils.PlayerUtils;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = RegisterActivity.class.getSimpleName();
    private String mUserAvatarUri = "";
    private String mUsername = "";

    // Firebase instances for Authentication
    private FirebaseAuth mFirebaseAuth;

    // Listener for Firebase Authentication which gets triggered on Authentication state changes
    private FirebaseAuth.AuthStateListener mAuthListener;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.etUserName)
    EditText etUserName;
    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    @BindView(R.id.fabBrowseAvatar)
    FloatingActionButton fabBrowseAvatar;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.scrollViewRegister)
    ScrollView scrollViewRegister;
    @BindView(R.id.networkFailureBanner)
    RelativeLayout networkFailureBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.FIRST_USE_PREFERENCE,
                Context.MODE_PRIVATE);

        boolean firstUse = sharedPreferences.getBoolean(Constants.FIRST_USE, true);

        if (firstUse == true) {
            startActivity(new Intent(this, TutorialActivity.class));
            finish();
        }

        // Check whether Google Play Services are enabled or not.
        // If enabled, start the Google Instance ID Registration Service
        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationService.class);
            startService(intent);
        }

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        initAuthListener();

        startWithCurrentUser();
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
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device does not have play services enabled.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void initAuthListener() {
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "User has signed in to firebase: " + user.getUid());
            } else {
                Log.d(TAG, "User has signed out of firebase.");
            }
        };
    }

    private void notifyNetworkFailure(final boolean networkFailure) {
        networkFailureBanner.setVisibility(networkFailure ? View.VISIBLE : View.GONE);
        fabBrowseAvatar.setEnabled(!networkFailure);
        btnRegister.setEnabled(!networkFailure);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void startWithCurrentUser() {
        User user = User.get(this);
        if (null != user) {
            Log.d(TAG, "startWithCurrentUser: Logged In User: " + user);
            startSelectGame(user);
            overridePendingTransition(0, 0);
        } else {
            setContentView(R.layout.activity_register);
            ButterKnife.bind(this);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.title_activity_sign_up);
            }

            if (isNetworkAvailable(this)) {
                notifyNetworkFailure(false);
                mUserAvatarUri = PlayerUtils.getDefaultAvatar();
                loadRoundedImage(this, ivAvatar, mUserAvatarUri);
            } else {
                notifyNetworkFailure(true);
            }

        }
    }

    private void startWithFirebaseUser() {
        FirebaseUser firebaseUser = getCurrentUser();
        if (firebaseUser != null) {
            String avatarUri;
            if (firebaseUser.getPhotoUrl() != null) {
                avatarUri = firebaseUser.getPhotoUrl().toString();
            } else {
                avatarUri = PlayerUtils.getDefaultAvatar();
            }
            User user = new User(avatarUri, firebaseUser.getDisplayName(), firebaseUser.getUid());
            user.save(this);
            startSelectGame(user);
        } else {
            Log.e(TAG, "startWithFirebaseUser: User information received from Firebase Authentication is null");
        }
    }

    private void startSelectGame(final User user) {
        Intent selectGameIntent = new Intent(this, SelectGameActivity.class);
        selectGameIntent.putExtra(PARAM_USER, Parcels.wrap(user));
        finish();
        startActivity(selectGameIntent);
        AnimationUtilsJC.enterVineTransition(this);
    }

    private GoogleApiClient getGoogleApiClient() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        return new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @OnClick(R.id.fabBrowseAvatar)
    public void browseAvatar() {
        Intent intent = new Intent(RegisterActivity.this, AvatarSelectionActivity.class);
        startActivityForResult(intent, REQ_CODE_PICK_IMAGE, null);
        AnimationUtilsJC.enterVineTransition(this);
    }

    @OnClick(R.id.btnRegister)
    public void register(final View view) {
        mUsername = etUserName.getText().toString().replaceAll("\\s+", "");
        if (mUsername.isEmpty()) {
            showSnackBar(this, view, "Username must have a value!");
            etUserName.requestFocus();
            scrollViewRegister.scrollTo(etUserName.getScrollX(), etUserName.getScrollY());
            return;
        }
        loginAnonymouslyToFirebase();
    }

    private void loginAnonymouslyToFirebase() {
        mFirebaseAuth.signInAnonymously().addOnCompleteListener(this, task -> {
            Log.d(TAG, "loginAnonymouslyToFirebase: signInAnonymously: complete: " + task.isSuccessful());
            // If sign in fails, display a message to the user.
            // If sign in succeeds the auth state listener will be notified.
            // Logic to handle the signed in user can be handled in the listener.
            if (!task.isSuccessful()) {
                Log.w(TAG, "loginAnonymouslyToFirebase: signInAnonymously: ", task.getException());
                Toast.makeText(RegisterActivity.this, "Anonymous Sign-In to Firebase failed!", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "loginAnonymouslyToFirebase: signInAnonymously: succeeded.");
                User user = new User(mUserAvatarUri, mUsername, getCurrentUser().getUid());
                user.save(this);
                startWithCurrentUser();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_PICK_IMAGE:
                if (data != null) {
                    mUserAvatarUri = data.getStringExtra(SELECTED_AVATAR);
                    Log.d(TAG, mUserAvatarUri);
                    loadRoundedImage(this, ivAvatar, mUserAvatarUri);
                }
                break;
            default:
                break;
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth(credential, "Google");
    }

    private void firebaseAuth(final AuthCredential credential, String authType) {
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "signInWithCredential: onComplete: " + authType + ": " + task.isSuccessful());
                    // If sign in fails, display a message to the user.
                    // If sign in succeeds the auth state listener will be notified
                    // and logic to handle the signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithCredential: " + authType + ": ", task.getException());
                        Toast.makeText(RegisterActivity.this, "Authentication with " + authType + " failed.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Login success
                        startWithFirebaseUser();
                    }
                });
    }

    private FirebaseUser getCurrentUser() {
        return mFirebaseAuth.getCurrentUser();
    }

}