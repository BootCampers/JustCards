package org.justcards.android.activities;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import org.justcards.android.R;
import org.justcards.android.adapters.AvatarArrayAdapter;
import org.justcards.android.utils.AnimationUtilsJC;
import org.justcards.android.utils.Constants;
import org.justcards.android.utils.PlayerUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.justcards.android.utils.PlayerUtils.getDefaultAvatar;

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
        AnimationUtilsJC.exitVineTransition(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnimationUtilsJC.animateCircularReveal(fab);
    }

    @Override
    public void onSelectedAvatar(String avatarUrl) {
        selectedAvatarUrl = avatarUrl;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            AnimationUtilsJC.exitVineTransition(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimationUtilsJC.exitVineTransition(this);
    }
}