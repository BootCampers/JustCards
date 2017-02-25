package org.justcards.android.utils;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewAnimationUtils;

import org.justcards.android.R;

/**
 * Author: agoenka
 * Created At: 12/10/2016
 * Version: ${VERSION}
 */
public class AnimationUtils {

    private static final long FAB_ANIMATION_TIME = 300;
    private static final int CAMERA_DISTANCE = 8000;

    private AnimationUtils() {
        //no instance
    }

    public interface FlipLoaderListener {
        void onFlip();
    }

    public static void animateCircularReveal(final View view) {
        animateCircularReveal(view, FAB_ANIMATION_TIME);
    }

    public static void animateCircularReveal(final View view, final long animationTime) {
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
        }, animationTime);
    }

    private static void changeCameraDistance(final Context context, final View view) {
        float scale = context.getResources().getDisplayMetrics().density * CAMERA_DISTANCE;
        view.setCameraDistance(scale);
    }

    public static void animateFlip(final Context context, final View target, final FlipLoaderListener listener) {
        changeCameraDistance(context, target);

        AnimatorSet outAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.flip_out_animation);
        AnimatorSet inAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.flip_in_animation);
        outAnimator.setTarget(target);
        inAnimator.setTarget(target);

        inAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (listener != null) {
                    listener.onFlip();
                }
            }
        });

        AnimatorSet combinedAnimator = new AnimatorSet();
        combinedAnimator.playSequentially(outAnimator, inAnimator);
        combinedAnimator.start();
    }

    public static void enterVineTransition(final Activity context) {
        context.overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
    }

    public static void exitVineTransition(final Activity context) {
        context.overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    public static void enterZoomTransition(final Activity context) {
        context.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    public static void exitZoomTransition(final Activity context) {
        context.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

}