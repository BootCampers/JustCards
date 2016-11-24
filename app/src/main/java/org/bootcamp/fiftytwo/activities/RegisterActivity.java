package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.Constants;

import static org.bootcamp.fiftytwo.utils.Constants.DISPLAY_NAME;
import static org.bootcamp.fiftytwo.utils.Constants.USER_AVATAR_URI;
import static org.bootcamp.fiftytwo.utils.Constants.USER_ID;
import static org.bootcamp.fiftytwo.utils.Constants.USER_PREFS;
import static org.bootcamp.fiftytwo.utils.Constants.USER_TAG;

public class RegisterActivity extends AppCompatActivity {
    String userAvatarURI = "";
    EditText usernameTxtbox;
    ImageView avatarImageView;
    Button browseButton;
    Button registerButton;
    User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        instantiateWidgets();
    }

    private void instantiateWidgets(){
        usernameTxtbox = (EditText) findViewById(R.id.userName);
        avatarImageView = (ImageView) findViewById(R.id.ivAvatar);
        browseButton = (Button) findViewById(R.id.browseAvatarBttn);
        registerButton = (Button) findViewById(R.id.registerBttn);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameTxtbox.getText().toString();
                user = new User(userAvatarURI, username);
                //user.saveInBackground();

                SharedPreferences userPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = userPrefs.edit();
                editor.putString(DISPLAY_NAME, username);
                //TODO: use actual avatar
                editor.putString(USER_AVATAR_URI, "http://i.imgur.com/GkyKh.jpg");
                editor.putString(USER_ID, ParseUser.getCurrentUser().getObjectId());
                editor.commit();

                Intent createGameIntent = new Intent(RegisterActivity.this, CreateJoinGameActivity.class);
                createGameIntent.putExtra(USER_TAG, user.getAvatarUri());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {

            String selectedAvatarUrl = data.getStringExtra(Constants.SELECTED_AVATAR);
            Log.d(Constants.TAG, selectedAvatarUrl);
            Glide.with(this)
                    .load(selectedAvatarUrl)
                    .centerCrop()
                    .into(avatarImageView);
        }
    }
}
