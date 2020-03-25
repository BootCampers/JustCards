package org.justcards.android.views;

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Author: agoenka
 * Created At: 11/19/2016
 * Version: ${VERSION}
 */
public class OverlapDecoration extends RecyclerView.ItemDecoration {

    private final int x;
    private final int y;
    private final float scale;

    public OverlapDecoration(Context context, int x, int y) {
        this.x = x;
        this.y = y;
        scale = context.getResources().getDisplayMetrics().density;
    }

    @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == 0)
            return;
        outRect.set(px(x), px(y), 0, 0);
    }

    private int px(int dp) {
        return (int) (dp * scale * 0.5f);
    }
}