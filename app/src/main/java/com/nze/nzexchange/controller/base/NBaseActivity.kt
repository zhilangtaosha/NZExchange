package com.nze.nzexchange.controller.base

import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.kaopiz.kprogresshud.KProgressHUD
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzexchange.NzeApp
import com.trello.rxlifecycle2.android.ActivityEvent
import io.reactivex.FlowableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.nze.nzexchange.controller.base.NBaseActivity.OnHideKeyboardListener


abstract class NBaseActivity : BaseActivity() {

    var onHideKeyboardListener: OnHideKeyboardListener? = null

    val mProgressDialog: KProgressHUD by lazy {
        KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
    }


    fun skipActivity(cls: Class<*>) {
        startActivity(Intent(this, cls))
    }

    fun <T> netTfWithDialog(): FlowableTransformer<T, T> = FlowableTransformer { observable ->
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .doOnSubscribe {
                    mProgressDialog.show()
                }.doOnNext {
                    if (mProgressDialog.isShowing)
                        mProgressDialog.dismiss()
                }
                .doOnComplete {
                    if (mProgressDialog.isShowing)
                        mProgressDialog.dismiss()
                }.doFinally {
                    if (mProgressDialog.isShowing)
                        mProgressDialog.dismiss()
                }
    }


    fun <T> netTf(): FlowableTransformer<T, T> = FlowableTransformer { observable ->
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
    }

    val onError: (e: Throwable) -> Unit = { e: Throwable ->

    }

    fun showToast(content: String) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.windowToken)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     */
    private fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.height
            val right = left + v.width
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     */
    private fun hideKeyboard(token: IBinder?) {
        if (token != null) {
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    interface OnHideKeyboardListener {
        fun hideKeyboard(): Boolean
    }
}