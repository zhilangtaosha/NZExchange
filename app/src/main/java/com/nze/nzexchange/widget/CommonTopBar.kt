package com.nze.nzexchange.widget

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.R.id.layout_root_ctb
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.tools.getNColor
import kotlinx.android.synthetic.main.common_top_bar.view.*

class CommonTopBar(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    private lateinit var mLeftTv: TextView
    private lateinit var mTitleTv: TextView
    private lateinit var mRightTv: TextView
    private lateinit var mRootLayout: RelativeLayout
    private var mTitleText: String? = null
    private var mShowLeftIcon: Boolean = false
    private var mLeftText: String? = null
    private var mRightText: String? = null
    private var mLeftIconId: Int = -1
    private var mRightIconId: Int = -1
    private var mBackgroudColor: Int = -1

    init {
        initAttrs(context, attrs)
        init(context)
    }

    fun initAttrs(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CommonTopBar)
        ta?.let {
            mTitleText = ta.getString(R.styleable.CommonTopBar_tb_title_text)
            mShowLeftIcon = ta.getBoolean(R.styleable.CommonTopBar_tb_show_left_icon, true)
            mLeftText = ta.getString(R.styleable.CommonTopBar_tb_left_text)
            mLeftIconId = ta.getResourceId(R.styleable.CommonTopBar_tb_left_icon, R.mipmap.left_arrow)
            mRightIconId = ta.getResourceId(R.styleable.CommonTopBar_tb_right_icon, -1)
            mRightText = ta.getString(R.styleable.CommonTopBar_tb_right_text)
            mBackgroudColor = ta.getColor(R.styleable.CommonTopBar_tb_backgroud, ContextCompat.getColor(context, R.color.color_title_bg))
        }
        ta.recycle()
    }

    fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.common_top_bar, this, true)
        mLeftTv = findViewById<TextView>(R.id.tv_left_tb)
        mRightTv = findViewById<TextView>(R.id.tv_right_tb)
        mTitleTv = findViewById<TextView>(R.id.tv_title_tb)
        mRootLayout = findViewById<RelativeLayout>(R.id.layout_root_ctb)

        mRootLayout.setBackgroundColor(mBackgroudColor)

        mTitleTv.setText(mTitleText)
        val leftIcon = ContextCompat.getDrawable(context, mLeftIconId)

        if (!mShowLeftIcon) {
            mLeftTv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        } else {
            mLeftTv.setCompoundDrawablesWithIntrinsicBounds(leftIcon, null, null, null)
        }

        if (mRightIconId > 0) {
            val rightIcon = ContextCompat.getDrawable(context, mRightIconId)
            mRightTv.setCompoundDrawablesWithIntrinsicBounds(null, null, rightIcon, null)
        }

        if (!TextUtils.isEmpty(mLeftText))
            mLeftTv.setText(mLeftText)

        if (!TextUtils.isEmpty(mRightText))
            mRightTv.setText(mRightText)

        mLeftTv.setOnClickListener {
            (context as Activity).finish()
        }

       if(context is NBaseActivity) {
           context.setWindowStatusBarColor(R.color.color_title_bg)
       }
    }

    fun setTitle(resid: Int): CommonTopBar {
        mTitleTv.setText(resid)
        return this
    }

    fun setTitleRightIcon(iconId: Int) {
        val drawable = ContextCompat.getDrawable(context, iconId)
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        mTitleTv.setCompoundDrawables(null, null, drawable, null)
    }

    fun setTitleClick(click: () -> Unit) {
        mTitleTv.setOnClickListener {
            click.invoke()
        }
    }

    fun setTitle(title: String): CommonTopBar {
        mTitleTv.text = title
        return this
    }

    fun setRightText(str: String): CommonTopBar {
        mRightTv.text = str
        return this
    }

    fun setRightClick(click: () -> Unit): CommonTopBar {
        mRightTv.setOnClickListener {
            click()
        }
        return this
    }


    fun setBackGroud(color: Int): CommonTopBar {
        layout_root_ctb.setBackgroundColor(color)
        return this
    }

    fun hideLeft(): CommonTopBar {
        mLeftTv.visibility = View.INVISIBLE
        return this
    }

    fun hideLeftDrawable() {
        mLeftTv.setCompoundDrawables(null, null, null, null)
    }
}