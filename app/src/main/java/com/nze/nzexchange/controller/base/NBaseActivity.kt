package com.nze.nzexchange.controller.base

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.kaopiz.kprogresshud.KProgressHUD
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzexchange.config.RrefreshType
import com.trello.rxlifecycle2.android.ActivityEvent
import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


abstract class NBaseActivity : BaseActivity() {
    val PAGE_SIZE = 20
    var page = 1
    var refreshType: RrefreshType = RrefreshType.INIT
    var onHideKeyboardListener: OnHideKeyboardListener? = null

    val mProgressDialog: KProgressHUD by lazy {
        KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
    }

    fun showLoad() {
        mProgressDialog.show()
    }

    fun dismissLoad() {
        if (mProgressDialog.isShowing) {
            mProgressDialog.dismiss()
        }
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
        NLog.i("error.....${e.localizedMessage}")
        showToast(e.localizedMessage)
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

    fun setWindowStatusBarColor(colorResId: Int) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                val window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(this, colorResId)

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}