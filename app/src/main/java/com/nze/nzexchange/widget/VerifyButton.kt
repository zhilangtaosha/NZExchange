package com.nze.nzexchange.widget

import android.content.Context
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

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    private fun initView(context: Context) {
        setTextColor(ContextCompat.getColor(getContext(), R.color.color_FFE05760))
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)

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
                    text = "获取验证码"
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