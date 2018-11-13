package com.nze.nzexchange.widget

import android.app.Activity
import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.nze.nzexchange.R

class CommonTopBar(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    private lateinit var mLeftTv: TextView
    private lateinit var mTitleTv: TextView
    private lateinit var mRightTv: TextView
    private var mTitleText: String? = null
    private var mShowLeftIcon: Boolean = false
    private var mLeftText: String? = null
    private var mRightText: String? = null
    private var mLeftIconId: Int = 0

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
            mRightText = ta.getString(R.styleable.CommonTopBar_tb_right_text)
            ta.recycle()
        }

    }

    fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.common_top_bar, this, true)
        mLeftTv = findViewById(R.id.tv_left_tb) as TextView
        mRightTv = findViewById(R.id.tv_right_tb) as TextView
        mTitleTv = findViewById(R.id.tv_title_tb) as TextView

        mTitleTv.setText(mTitleText)
        val leftIcon = ContextCompat.getDrawable(context, mLeftIconId)

        if (!mShowLeftIcon) {
            mLeftTv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        } else {
            mLeftTv.setCompoundDrawablesWithIntrinsicBounds(leftIcon, null, null, null)
        }

        if (!TextUtils.isEmpty(mLeftText))
            mLeftTv.setText(mLeftText)

        if (!TextUtils.isEmpty(mRightText))
            mRightTv.setText(mRightText)

        mLeftTv.setOnClickListener {
            (context as Activity).finish()
        }


    }

    fun setTitle(resid: Int) {
        mTitleTv.setText(resid)
    }

    fun setTitle(title: String) {
        mTitleTv.text = title
    }

    fun setRightClick(click: () -> Unit) {
        mRightTv.setOnClickListener {
            click()
        }
    }

}