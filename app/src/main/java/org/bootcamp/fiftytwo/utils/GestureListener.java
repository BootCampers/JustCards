package org.bootcamp.fiftytwo.utils;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by baphna on 11/20/2016.
 */
//TODO: Join this and DragListener to get which view is clicked
public class GestureListener extends
        GestureDetector.SimpleOnGestureListener {

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(Constants.TAG, "Double tap");
        return true;
    }
}
