package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.adapters.AvatarArrayAdapter;
import org.bootcamp.fiftytwo.utils.Constants;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AvatarSelectionActivity extends AppCompatActivity
    implements AvatarArrayAdapter.OnAvatarSelectedListener{

    @BindView(R.id.rvAvatars)
    RecyclerView rvAvatars;

    HashMap<String, Boolean> avatars = new HashMap<>();
    private RecyclerView.LayoutManager mStaggeredLayoutManager;
    private AvatarArrayAdapter avatarArrayAdapter;
    private String selectedAvatarUrl = "http://i.imgur.com/Fankh2h.jpg"; //defualt //TODO change

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_selection);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        for (String avatarURI : Constants.getDefaultAvatarList()) {
            avatars.put(avatarURI, false);
        }

//        avatars.put("http://i.imgur.com/GkyKh.jpg", false);
//        avatars.put("http://i.imgur.com/4M8vzoD.png", false);
//        avatars.put("http://i.imgur.com/Fankh2h.jpg", false);
//        avatars.put("http://i.imgur.com/i7zmanJ.jpg", false);
//        avatars.put("http://i.imgur.com/jrmh8XL.jpg", false);
//        avatars.put("http://i.imgur.com/VCY27Er.jpg", false);
//        avatars.put("http://i.imgur.com/UMUY9Yn.jpg", false);
//        avatars.put("http://i.imgur.com/RZ0jFNp.gif", false);
//        avatars.put("http://i.imgur.com/9OHzici.jpg?1", false);
//        avatars.put("http://i.imgur.com/ITwmNm3.jpg", false);

        avatarArrayAdapter = new AvatarArrayAdapter(this, avatars, this);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvAvatars.setLayoutManager(mStaggeredLayoutManager);
        rvAvatars.setAdapter(avatarArrayAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Great Choice!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent=new Intent(AvatarSelectionActivity.this,
                        RegisterActivity.class);
                intent.putExtra(Constants.SELECTED_AVATAR, selectedAvatarUrl);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onSelectedAvatar(String avatarUrl) {
        selectedAvatarUrl = avatarUrl;
    }
}
