package org.bootcamp.fiftytwo.views;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by baphna on 11/20/2016.
 */
public class OnGestureListener implements View.OnTouchListener {

    private GestureDetector gestureDetector;

    protected OnGestureListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            OnGestureListener.this.onDoubleTap(e);
            return super.onDoubleTap(e);
        }
    }

    public void onDoubleTap(MotionEvent event) {
        // To be overridden when implementing listener
    }
}