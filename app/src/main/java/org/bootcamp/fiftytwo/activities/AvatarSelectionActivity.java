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
import org.bootcamp.fiftytwo.utils.AppUtils;
import org.bootcamp.fiftytwo.utils.Constants;
import org.bootcamp.fiftytwo.utils.PlayerUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.bootcamp.fiftytwo.utils.PlayerUtils.getDefaultAvatar;

public class AvatarSelectionActivity extends AppCompatActivity implements AvatarArrayAdapter.OnAvatarSelectedListener {

    @BindView(R.id.rvAvatars) RecyclerView rvAvatars;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    HashMap<String, Boolean> avatars = new HashMap<>();
    private String selectedAvatarUrl = getDefaultAvatar(); //default // TODO change

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_selection);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        for (String avatarURI : PlayerUtils.playerAvatars) {
            avatars.put(avatarURI, false);
        }

        AvatarArrayAdapter avatarArrayAdapter = new AvatarArrayAdapter(this, avatars, this);
        RecyclerView.LayoutManager mStaggeredLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvAvatars.setLayoutManager(mStaggeredLayoutManager);
        rvAvatars.setAdapter(avatarArrayAdapter);
    }

    @OnClick(R.id.fab)
    public void onFab(final View view) {
        Snackbar.make(view, "Great Choice!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        Intent intent = new Intent(AvatarSelectionActivity.this, RegisterActivity.class);
        intent.putExtra(Constants.SELECTED_AVATAR, selectedAvatarUrl);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.animateCircularReveal(fab);
    }

    @Override
    public void onSelectedAvatar(String avatarUrl) {
        selectedAvatarUrl = avatarUrl;
    }
}