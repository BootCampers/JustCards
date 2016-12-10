package org.bootcamp.fiftytwo.utils;

import android.animation.Animator;
import android.view.View;
import android.view.ViewAnimationUtils;

/**
 * Author: agoenka
 * Created At: 12/10/2016
 * Version: ${VERSION}
 */
public class AnimationUtils {

    private static final long FAB_ANIMATION_TIME = 300;

    private AnimationUtils() {
        //no instance
    }

    public static void animateCircularReveal(final View view) {
        view.postDelayed(() -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                // get the center for the clipping circle
                int cx = view.getMeasuredWidth() / 2;
                int cy = view.getMeasuredHeight() / 2;
                // get the final radius for the clipping circle
                int finalRadius = Math.max(view.getWidth(), view.getHeight()) / 2;
                // create the animator for this view (the start radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
                // make the view visible and start the animation
                view.setVisibility(View.VISIBLE);
                anim.start();
            }
        }, FAB_ANIMATION_TIME);
    }

}