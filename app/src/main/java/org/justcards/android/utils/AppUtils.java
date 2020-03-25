package org.justcards.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.reflect.TypeToken;

import org.justcards.android.R;
import org.justcards.android.models.User;
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

    public static Type getUsersType() {
        return new TypeToken<List<User>>() {
        }.getType();
    }

    public static void loadRoundedImage(final Context context, final ImageView view, final Object uri) {
        Glide.with(context)
                .asBitmap()
                .load(uri)
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

    public static void showSnackBar(Context context, View view, String msg) {
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        snackbar.show();
    }

    public static VectorDrawableCompat getVectorCompat(final Context context, final int resId) {
        return VectorDrawableCompat.create(context.getResources(), resId, context.getTheme());
    }

    public static ArrayAdapter getSpinnerAdapter(final Context context, final int resource) {
        ArrayAdapter adapter = ArrayAdapter.createFromResource(context, resource, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        return adapter;
    }

}