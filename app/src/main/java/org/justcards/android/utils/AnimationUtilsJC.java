package org.justcards.android.utils;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import org.justcards.android.R;

/**
 * Author: agoenka
 * Created At: 12/10/2016
 * Version: ${VERSION}
 */
public class AnimationUtilsJC {

    private static final long FAB_ANIMATION_TIME = 300;
    private static final long FAB_ANIMATION_END_TIME = 100;
    private static final long ANIMATION_ENTER_DURATION = 500;
    private static final long ANIMATION_EXIT_DURATION = 400;
    private static final int CAMERA_DISTANCE = 8000;

    private AnimationUtilsJC() {
        //no instance
    }

    public interface FlipLoaderListener {
        void onFlip();
    }

    public static void bounceAnimation(Context context, final View view) {
        final Animation bounceAnimation = AnimationUtils.loadAnimation(context, R.anim.bounce);
        BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
        bounceAnimation.setInterpolator(interpolator);
        view.startAnimation(bounceAnimation);
    }

    public static void animateCircularReveal(final View view) {
        animateCircularReveal(view, FAB_ANIMATION_TIME);
    }

    public static void animateCircularReveal(final View view, final long animationTime) {
        view.postDelayed(() -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                // getInstance the center for the clipping circle
                int cx = view.getMeasuredWidth() / 2;
                int cy = view.getMeasuredHeight() / 2;
                // getInstance the final radius for the clipping circle
                int finalRadius = Math.max(view.getWidth(), view.getHeight()) / 2;
                // create the animator for this view (the start radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
                // make the view visible and start the animation
                view.setVisibility(View.VISIBLE);
                anim.start();
            }
        }, animationTime);
    }

    public static void animateCornerReveal(final View view) {
        animateCornerReveal(view, null);
    }

    private static void animateCornerReveal(final View view, final Animator.AnimatorListener listener) {
        animateCornerReveal(view, listener, ANIMATION_ENTER_DURATION);
    }

    private static void animateCornerReveal(final View view, final Animator.AnimatorListener listener, final long duration) {
        view.postDelayed(() -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                // getInstance the starting point for the clipping circle
                int cx = view.getWidth();
                int cy = 0;
                // getInstance the final radius for the clipping circle
                float finalRadius = (float) Math.hypot(view.getWidth(), view.getHeight());
                // create the animator for this view (the start radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius);
                anim.setDuration(duration);
                if (listener != null) {
                    anim.addListener(listener);
                }
                // make the view visible and start the animation
                view.setVisibility(View.VISIBLE);
                anim.start();
            }
        }, FAB_ANIMATION_TIME);
    }

    public static void animateCornerUnReveal(final View view, final Animator.AnimatorListener listener) {
        animateCornerUnReveal(view, listener, ANIMATION_EXIT_DURATION);
    }

    private static void animateCornerUnReveal(final View view, final Animator.AnimatorListener listener, final long duration) {
        view.postDelayed(() -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                // getInstance the starting point for the clipping circle
                int cx = view.getWidth();
                int cy = 0;
                // getInstance the initial radius for the clipping circle
                float initialRadius = (float) Math.hypot(view.getWidth(), view.getHeight());
                // create the animator for this view
                Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0f);
                anim.setDuration(duration);
                if (listener != null) {
                    anim.addListener(listener);
                }
                // start the animation
                anim.start();
            }
        }, FAB_ANIMATION_END_TIME);
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