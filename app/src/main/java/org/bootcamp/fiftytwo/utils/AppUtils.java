package org.bootcamp.fiftytwo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.parceler.Parcels;

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

}