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

    private final int x;
    private final int y;

    public OverlapDecoration(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == 0)
            return;
        outRect.set(x, y, 0, 0);
    }

}