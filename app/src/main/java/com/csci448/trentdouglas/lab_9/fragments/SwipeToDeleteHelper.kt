package com.csci448.trentdouglas.lab_9.fragments

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.TextPaint
import android.util.Log
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.csci448.trentdouglas.lab_9.R

class SwipeToDeleteHelper(private val adapter: MarkerListAdapter) : ItemTouchHelper.Callback() {

    private val LOG_TAG = "swiper"
    interface ItemTouchHelperAdapter {
        fun onItemDismiss(position: Int)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(0, ItemTouchHelper.RIGHT)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        Log.d(LOG_TAG, "adaperPos: ${viewHolder.adapterPosition}")
        adapter.onItemDismiss(viewHolder.adapterPosition)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float,
                             actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val context = recyclerView.context

        // make background red
        val background = ColorDrawable(Color.RED)
        background.setBounds(0, viewHolder.itemView.top, (viewHolder.itemView.left + dX).toInt(), viewHolder.itemView.bottom)
        background.draw(c)

        // add delete icon
        val horizontalMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16.0f, context.resources.displayMetrics).toInt()
        val icon = ContextCompat.getDrawable(context, R.drawable.ic_menu_swipe_delete_character)
        val iconSize = icon?.intrinsicHeight ?: viewHolder.itemView.height / 2
        val left = viewHolder.itemView.left + horizontalMargin
        if( dX > horizontalMargin ) {
            val top = viewHolder.itemView.top + (viewHolder.itemView.bottom - viewHolder.itemView.top) / 2 - (iconSize / 2)
            icon?.setBounds(left, top, left + icon.intrinsicWidth, top + icon.intrinsicHeight)
            icon?.draw(c)
        }

        // add delete text
        if( dX > horizontalMargin + iconSize ) {
            val textPaint = TextPaint().apply {
                isAntiAlias = true
                color = Color.WHITE
                typeface = Typeface.SANS_SERIF
                textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14.0f, context.resources.displayMetrics)
            }
            c.drawText(context.resources.getString(R.string.delete_character_label),
                (left + (icon?.intrinsicWidth ?: horizontalMargin) + horizontalMargin).toFloat(),
                (viewHolder.itemView.top + (viewHolder.itemView.bottom - viewHolder.itemView.top) / 2 + textPaint.textSize / 2),
                textPaint)
        }
    }
}