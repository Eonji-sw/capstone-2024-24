package com.kotlin.kiumee.presentation.menu.chat

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.kiumee.core.util.context.pxToDp

class ChatItemDecorator(val context: Context) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)

        if (position == 0) {
            outRect.top = context.pxToDp(16)
        } else {
            outRect.bottom = context.pxToDp(2)
        }
        outRect.bottom = context.pxToDp(16)
    }
}
