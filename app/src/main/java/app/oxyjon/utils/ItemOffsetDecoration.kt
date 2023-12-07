package app.oxyjon.utils

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView


class ItemOffsetDecoration constructor(private val mItemOffset: Int) :
    RecyclerView.ItemDecoration() {
    constructor(context: Context, @DimenRes itemOffsetId: Int) : this(context.resources
        .getDimensionPixelSize(itemOffsetId)) {
    }

    public override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(mItemOffset, 0, mItemOffset, 0)
    }
}