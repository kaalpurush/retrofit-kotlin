package com.codelixir.retrofit.ui

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class DividerItemDecoration : ItemDecoration {
    private var mDivider: Drawable?
    private var mShowFirstDivider = false
    private var mShowLastDivider = false
    private var mOrientation = -1

    /**
     * Instantiates a new Divider item decoration.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    constructor(context: Context, attrs: AttributeSet?) {
        val a = context
            .obtainStyledAttributes(attrs, intArrayOf(R.attr.listDivider))
        mDivider = a.getDrawable(0)
        a.recycle()
    }

    /**
     * Instantiates a new Divider item decoration.
     *
     * @param context          the context
     * @param attrs            the attrs
     * @param showFirstDivider the show first divider
     * @param showLastDivider  the show last divider
     */
    constructor(
        context: Context, attrs: AttributeSet?, showFirstDivider: Boolean,
        showLastDivider: Boolean
    ) : this(context, attrs) {
        mShowFirstDivider = showFirstDivider
        mShowLastDivider = showLastDivider
    }

    /**
     * Instantiates a new Divider item decoration.
     *
     * @param context the context
     * @param resId   the res id
     */
    constructor(context: Context?, resId: Int) {
        mDivider = ContextCompat.getDrawable(context!!, resId)
    }

    /**
     * Instantiates a new Divider item decoration.
     *
     * @param context          the context
     * @param resId            the res id
     * @param showFirstDivider the show first divider
     * @param showLastDivider  the show last divider
     */
    constructor(
        context: Context?, resId: Int, showFirstDivider: Boolean,
        showLastDivider: Boolean
    ) : this(context, resId) {
        mShowFirstDivider = showFirstDivider
        mShowLastDivider = showLastDivider
    }

    /**
     * Instantiates a new Divider item decoration.
     *
     * @param divider the divider
     */
    constructor(divider: Drawable?) {
        mDivider = divider
    }

    /**
     * Instantiates a new Divider item decoration.
     *
     * @param divider          the divider
     * @param showFirstDivider the show first divider
     * @param showLastDivider  the show last divider
     */
    constructor(
        divider: Drawable?, showFirstDivider: Boolean,
        showLastDivider: Boolean
    ) : this(divider) {
        mShowFirstDivider = showFirstDivider
        mShowLastDivider = showLastDivider
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (mDivider == null) {
            return
        }
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION || position == 0 && !mShowFirstDivider) {
            return
        }
        if (mOrientation == -1) getOrientation(parent)
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            outRect.top = mDivider!!.intrinsicHeight
            if (mShowLastDivider && position == state.itemCount - 1) {
                outRect.bottom = outRect.top
            }
        } else {
            outRect.left = mDivider!!.intrinsicWidth
            if (mShowLastDivider && position == state.itemCount - 1) {
                outRect.right = outRect.left
            }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (mDivider == null) {
            super.onDrawOver(c, parent, state)
            return
        }

        // Initialization needed to avoid compiler warning
        var left = 0
        var right = 0
        var top = 0
        var bottom = 0
        val size: Int
        val orientation = if (mOrientation != -1) mOrientation else getOrientation(parent)
        val childCount = parent.childCount
        if (orientation == LinearLayoutManager.VERTICAL) {
            size = mDivider!!.intrinsicHeight
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
        } else { //horizontal
            size = mDivider!!.intrinsicWidth
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
        }
        for (i in (if (mShowFirstDivider) 0 else 1) until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            if (orientation == LinearLayoutManager.VERTICAL) {
                top = child.top - params.topMargin - size
                bottom = top + size
            } else { //horizontal
                left = child.left - params.leftMargin
                right = left + size
            }
            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(c)
        }

        // show last divider
        if (mShowLastDivider && childCount > 0) {
            val child = parent.getChildAt(childCount - 1)
            if (parent.getChildAdapterPosition(child) == state.itemCount - 1) {
                val params = child.layoutParams as RecyclerView.LayoutParams
                if (orientation == LinearLayoutManager.VERTICAL) {
                    top = child.bottom + params.bottomMargin
                    bottom = top + size
                } else { // horizontal
                    left = child.right + params.rightMargin
                    right = left + size
                }
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(c)
            }
        }
    }

    /**
     * Gets orientation.
     *
     * @param parent the parent
     * @return the orientation
     */
    private fun getOrientation(parent: RecyclerView): Int {
        if (mOrientation == -1) {
            mOrientation = if (parent.layoutManager is LinearLayoutManager) {
                val layoutManager = parent.layoutManager as LinearLayoutManager?
                layoutManager!!.orientation
            } else {
                throw IllegalStateException(
                    "DividerItemDecoration can only be used with a LinearLayoutManager."
                )
            }
        }
        return mOrientation
    }
}