package com.nze.nzexchange.controller.my.safecenter

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.widget.CommonButton
import kotlinx.android.synthetic.main.activity_bind_google.*

/**
 * https://blog.csdn.net/jiaoyaning1210/article/details/78427818
 *
 */
class BindGoogleActivity : NBaseActivity() {
    val scrollView: ScrollView by lazy { sv_abg }
    val qcodeIv: ImageView by lazy { iv_qcode_abg }
    val addressTv: TextView by lazy { tv_address_abg }
    val verifyEt1: EditText by lazy { et_verify1_abg }
    val verifyEt2: EditText by lazy { et_verify2_abg }
    val verifyEt3: EditText by lazy { et_verify3_abg }
    val verifyEt4: EditText by lazy { et_verify4_abg }
    val verifyEt5: EditText by lazy { et_verify5_abg }
    val verifyEt6: EditText by lazy { et_verify6_abg }
    val etInputList by lazy { listOf<EditText>(verifyEt6, verifyEt5, verifyEt4, verifyEt3, verifyEt2, verifyEt1) }
    val etDelList by lazy { listOf<EditText>(verifyEt1, verifyEt2, verifyEt3, verifyEt4, verifyEt5, verifyEt6) }
    val confirmBtn: CommonButton by lazy { btn_confirm_abg }
    val guideTv: TextView by lazy { tv_guide_abg }
    val downloadTv: TextView by lazy { tv_download_abg }
    val TYPE_INPUT = 0
    val TYPE_DEL = 1
    var type: Int = TYPE_INPUT
    var isBottom = false

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState)
    }

    override fun getRootView(): Int = R.layout.activity_bind_google

    override fun initView() {

        val childView = scrollView.getChildAt(0)
        scrollView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    isBottom = childView != null && childView.measuredHeight <= (scrollView.scrollY + scrollView.height)
                }
            }
            false
        }
        etInputList.forEach {
            RxTextView.textChanges(it)
                    .subscribe {
                        if (it.isNotEmpty()) {
                            type = TYPE_INPUT
                            refreshFocus()
                        }

                    }
            it.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    type = TYPE_INPUT
                    if (!isBottom) {
                        scrollView.post {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                            refreshFocus()
                        }
                    } else {
                        refreshFocus()
                    }
                }
            }

            it.setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action != KeyEvent.ACTION_UP) {
                    NLog.i("删除。。。")
                    type = TYPE_DEL
                    refreshFocus()
                }
                false
            }
        }

    }

    fun refreshFocus() {
        var i = 0
        if (type == TYPE_INPUT) {
            etDelList.forEachIndexed { index, editText ->
                if (editText.getContent().isNullOrEmpty()) {
                    editText.isCursorVisible = true
                    editText.requestFocus()
                    return
                } else {
                    if (index == etDelList.size - 1) {
                        editText.isCursorVisible = true
                        editText.requestFocus()
                    } else {
                        editText.isCursorVisible = false
                    }

                }

            }
        } else {
            etInputList.forEachIndexed { index, editText ->
                if (editText.getContent().isNotEmpty()) {
//                    type = TYPE_INPUT
                    editText.setText("")
                    editText.isCursorVisible = true
                    editText.requestFocus()
                    return
                } else {
                    if (index === 0) {
                        editText.isCursorVisible = true
                        editText.requestFocus()
                    } else {
                        editText.isCursorVisible = false
                    }
                }
            }
        }

    }

//    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
//        if (event?.keyCode == KeyEvent.KEYCODE_DEL) {
//            NLog.i("删除。。。")
//            type = TYPE_DEL
//            refreshFocus()
//        }
//        return super.dispatchKeyEvent(event)
//    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOverridePendingTransitionMode(): BaseActivity.TransitionMode = TransitionMode.DEFAULT

    override fun isBindEventBusHere(): Boolean = false

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkDisConnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContainerTargetView(): View? = null

}
