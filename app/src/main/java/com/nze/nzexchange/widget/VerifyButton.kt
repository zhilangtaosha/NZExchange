package com.nze.nzexchange.widget

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/11
 */
class VerifyButton : AppCompatTextView {
    private var mDisposable: Disposable? = null
    private var mTextColor: Int = -1
    private var mTextSize = 24F

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttrs(context, attrs)
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttrs(context, attrs)
        initView(context)
    }

    fun initAttrs(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.VerifyButton)
        ta?.let {
            mTextColor = ta.getColor(R.styleable.VerifyButton_nz_textcolor, ContextCompat.getColor(context, R.color.color_FFE05760))
            mTextSize = ta.getDimension(R.styleable.VerifyButton_nz_textSize, -1f)
        }
        ta.recycle()
    }

    private fun initView(context: Context) {

        setTextColor(mTextColor)
        if (mTextSize > 0) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize)
        } else {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        }

        gravity = Gravity.CENTER
    }

    fun setVerifyClick(click: View.OnClickListener) {
        setOnClickListener {
            click.onClick(it)
        }
    }

    fun startVerify() {
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take((60 + 1).toLong())
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.computation())
                .map {
                    60 - it
                }
                .doOnSubscribe {
                    isEnabled = false
                    setTextColor(ContextCompat.getColor(context, R.color.color_FFE05760))
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    text = String.format("%d s", it)
                }, {

                }, {
                    isEnabled = true
                    setTextColor(ContextCompat.getColor(context, R.color.color_FFE05760))
                    text = "重新发送"
                    NLog.i("onComplete")
                }, {
                    mDisposable = it
                })
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mDisposable?.dispose()
        NLog.i("onDetachedFromWindow.........")
    }
}