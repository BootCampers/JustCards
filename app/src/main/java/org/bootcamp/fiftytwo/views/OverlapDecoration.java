package org.bootcamp.fiftytwo.views;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Author: agoenka
 * Created At: 11/19/2016
 * Version: ${VERSION}
 */
public class OverlapDecoration extends RecyclerView.ItemDecoration {

    private final static int verticalOverlap = 0;
    private final static int horizontalOverlap = -5;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(horizontalOverlap, verticalOverlap, 0, 0);
    }
}