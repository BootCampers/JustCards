package org.bootcamp.fiftytwo.utils;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.reflect.TypeToken;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.User;
import org.parceler.Parcels;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Author: agoenka
 * Created At: 11/22/2016
 * Version: ${VERSION}
 */
public class AppUtils {

    private AppUtils() {
        //no instance
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.size() == 0;
    }

    public static <T> Parcelable getParcelable(final List<T> elements) {
        ArrayList<T> list = new ArrayList<>();
        if (!isEmpty(elements)) {
            list.addAll(elements);
        }
        return Parcels.wrap(list);
    }

    public static <T> List<T> getList(T arg) {
        List<T> list = new ArrayList<>();
        list.add(arg);
        return list;
    }

    public static Type getCardsType() {
        return new TypeToken<List<Card>>() {}.getType();
    }

    public static Type getUsersType() {
        return new TypeToken<List<User>>() {}.getType();
    }

    public static void loadRoundedImage(final Context context, final ImageView view, final Object uri) {
        Glide.with(context)
                .load(uri)
                .asBitmap()
                .centerCrop()
                .into(new BitmapImageViewTarget(view) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        view.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    public static void showSnackBar(Context context, View view, String msg){
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        snackbar.show();
    }

    public static VectorDrawableCompat getVectorCompat(final Context context, final int resId) {
        return VectorDrawableCompat.create(context.getResources(), resId, context.getTheme());
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
        }, Constants.FAB_ANIMATION_TIME);
    }

}