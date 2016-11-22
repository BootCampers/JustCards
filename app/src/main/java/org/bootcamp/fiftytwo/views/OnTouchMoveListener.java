package org.bootcamp.fiftytwo.views;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Author: agoenka
 * Created At: 11/21/2016
 * Version: ${VERSION}
 */
public class OnTouchMoveListener implements View.OnTouchListener {

    private ViewGroup container;
    private float dX, dY;

    public OnTouchMoveListener(final ViewGroup container) {
        this.container = container;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float newX = event.getRawX() + dX;
                float newY = event.getRawY() + dY;

                if (newX < 0)
                    newX = 0;
                if (newY < 0)
                    newY = 0;

                if (newX + view.getWidth() > container.getWidth())
                    newX = container.getWidth() - view.getWidth();
                if (newY + view.getHeight() > container.getHeight())
                    newY = container.getHeight() - view.getHeight();

                view.animate()
                        .x(newX)
                        .y(newY)
                        .setDuration(0)
                        .start();
                break;
            default:
                return false;
        }
        return true;
    }
}