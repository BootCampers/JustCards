package org.bootcamp.fiftytwo.utils;

import android.os.Parcelable;

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
}