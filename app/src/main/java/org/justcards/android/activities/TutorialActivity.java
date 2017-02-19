package org.justcards.android.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.justcards.android.R;
import org.justcards.android.fragments.TutorialFragment;

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
