package org.bootcamp.fiftytwo.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.bootcamp.fiftytwo.R;

public class ShowPopUp extends Activity implements View.OnClickListener {

    Button ok;
    Button cancel;

    boolean click = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Cupon");
        setContentView(R.layout.activity_show_pop_up);
        ok = (Button)findViewById(R.id.popOkB);
        ok.setOnClickListener(this);
        cancel = (Button)findViewById(R.id.popCancelB);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        finish();
    }
}
