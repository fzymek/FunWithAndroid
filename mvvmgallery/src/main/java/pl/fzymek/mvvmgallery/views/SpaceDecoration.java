package pl.fzymek.mvvmgallery.views;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by filip on 30.09.2016.
 */
public class SpaceDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int spacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.0f, view.getResources().getDisplayMetrics());
        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = spacing;
        }
        outRect.left = spacing;
        outRect.right = spacing;
    }
}
