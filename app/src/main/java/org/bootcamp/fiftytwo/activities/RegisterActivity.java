package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.User;

import java.io.IOException;

import static org.bootcamp.fiftytwo.utils.Constants.USERNAME;
import static org.bootcamp.fiftytwo.utils.Constants.USER_AVATAR_URI;
import static org.bootcamp.fiftytwo.utils.Constants.USER_PREFS;
import static org.bootcamp.fiftytwo.utils.Constants.USER_TAG;

public class RegisterActivity extends AppCompatActivity {
    String userAvatarURI = "";
    EditText usernameTxtbox;
    ImageView avatarImageView;
    Button browseButton;
    Button registerButton;
    User user = null;
    int PICK_IMAGE_REQUEST = 1;


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
                SharedPreferences userPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = userPrefs.edit();
                editor.putString(USERNAME, username);
                editor.putString(USER_AVATAR_URI, userAvatarURI);
                editor.commit();

                Intent createGameIntent = new Intent(RegisterActivity.this, CreateJoinGameActivity.class);
                createGameIntent.putExtra(USER_TAG, user);
                startActivity(createGameIntent);
            }
        });


        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                userAvatarURI = uri.toString();
                // Log.d(TAG, String.valueOf(bitmap));

                avatarImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
