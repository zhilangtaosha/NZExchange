package com.nze.nzexchange.widget.recyclerview

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.nze.nzexchange.tools.dp2px

class Divider:RecyclerView.ItemDecoration{
    private lateinit var mDivider: Drawable

    private var leftMargin: Int = 0
    private var rightMargin:Int = 0
    private var topMargin:Int = 0
    private var bottomMargin:Int = 0

    private var width: Int = 0
    private var height:Int = 0

    private var mOrientation: Int = 0

    init {
    }

    constructor(orientation: Int=LinearLayoutManager.VERTICAL, divider: Drawable=ColorDrawable(0x10000)){
        setDivider(divider)
        setOrientation(orientation)
    }

    private fun setDivider(divider: Drawable) {
        this.mDivider = divider
        width = mDivider.intrinsicWidth
        height = mDivider.intrinsicHeight
    }

    private fun setOrientation(orientation: Int) {
        if (orientation != LinearLayoutManager.HORIZONTAL && orientation != LinearLayoutManager.VERTICAL) {
            throw IllegalArgumentException("invalid orientation")
        }
        mOrientation = orientation
    }

    fun setMargin(left: Int, top: Int, right: Int, bottom: Int) {
        this.leftMargin = left
        this.topMargin = top
        this.rightMargin = right
        this.bottomMargin = bottom
    }

    fun setHeight(height: Int) {
        this.height = height
    }

    fun setWidth(width: Int) {
        this.width = dp2px(width.toFloat())
    }

    fun getHeight(): Int {
        return height
    }

    fun getWidth(): Int {
        return width
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            drawHorizontal(c, parent)
        } else {
            drawVertical(c, parent)
        }
    }

    fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop + topMargin
        val bottom = parent.height - parent.paddingBottom - bottomMargin

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child
                    .layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin + leftMargin
            val right = left + width
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    fun drawVertical(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft + leftMargin
        val right = parent.width - parent.paddingRight - rightMargin

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            if (i == childCount - 1) break
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin + topMargin
            val bottom = top + height
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            outRect.set(0, 0, leftMargin + width + rightMargin, 0)
        } else {
            outRect.set(0, 0, 0, topMargin + height + bottomMargin)
        }
    }
}