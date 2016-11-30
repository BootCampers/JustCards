package org.bootcamp.fiftytwo.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.fragments.TutorialFragment;

public class TutorialActivity extends AppCompatActivity
    implements TutorialFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
