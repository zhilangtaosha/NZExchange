package com.nze.nzeframework.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.Window
import com.nze.nzeframework.R
import com.nze.nzeframework.loadcontrol.VaryViewHelperController
import com.nze.nzeframework.netstatus.NetChangeObserver
import com.nze.nzeframework.netstatus.NetStateReceiver
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


abstract class BaseActivity : RxAppCompatActivity() {
    protected var mNetChangeObserver: NetChangeObserver? = null
    private var mVaryViewHelperController: VaryViewHelperController? = null
    private var loadingViewContainer: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        when (getOverridePendingTransitionMode()) {
            BaseActivity.TransitionMode.RIGHT -> overridePendingTransition(R.anim.left_in, R.anim.left_out)
            BaseActivity.TransitionMode.LEFT -> overridePendingTransition(R.anim.right_in, R.anim.right_out)
            BaseActivity.TransitionMode.TOP -> overridePendingTransition(R.anim.top_in, R.anim.top_out)
            BaseActivity.TransitionMode.BOTTOM -> overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out)
            BaseActivity.TransitionMode.SCALE -> overridePendingTransition(R.anim.scale_in, R.anim.scale_out)
            BaseActivity.TransitionMode.FADE -> overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            else -> {
            }
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (isBindEventBusHere()) {
            EventBus.getDefault().register(this)
        }

        mNetChangeObserver = object : NetChangeObserver() {
            override fun onNetConnected(type: NetUtils.NetType) {
                super.onNetConnected(type)
                onNetworkConnected(type)
            }

            override fun onNetDisConnect() {
                super.onNetDisConnect()
                onNetworkDisConnected()
            }
        }
        if (isBindNetworkListener()) {
            NetStateReceiver.registerObserver(mNetChangeObserver)
            NetStateReceiver.registerNetworkStateReceiver(this)
        }
        super.onCreate(savedInstanceState)
        setContentView(getRootView())
        initView()
    }

    abstract fun getRootView(): Int

    abstract fun initView()

    @Subscribe
    fun <T> onEventMainThread(eventCenter: EventCenter<T>?) {
        if (null != eventCenter) {
            onEventComming(eventCenter)
        }
    }

    protected abstract fun <T> onEventComming(eventCenter: EventCenter<T>)

    abstract fun getOverridePendingTransitionMode(): TransitionMode

    abstract fun isBindEventBusHere(): Boolean
    abstract fun isBindNetworkListener(): Boolean


    protected abstract fun onNetworkConnected(type: NetUtils.NetType)

    protected abstract fun onNetworkDisConnected()

    enum class TransitionMode {
        DEFAULT, LEFT, RIGHT, TOP, BOTTOM, SCALE, FADE
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBindNetworkListener())
            NetStateReceiver.removeRegisterObserver(mNetChangeObserver)
        if (isBindEventBusHere()) {
            EventBus.getDefault().unregister(this)
        }
    }

    override fun finish() {
        super.finish()
        when (getOverridePendingTransitionMode()) {
            BaseActivity.TransitionMode.RIGHT -> overridePendingTransition(R.anim.right_in, R.anim.right_out)
            BaseActivity.TransitionMode.LEFT -> overridePendingTransition(R.anim.left_in, R.anim.left_out)
            BaseActivity.TransitionMode.TOP -> overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out)
            BaseActivity.TransitionMode.BOTTOM -> overridePendingTransition(R.anim.top_in, R.anim.top_out)
            BaseActivity.TransitionMode.SCALE -> overridePendingTransition(R.anim.scale_in_disappear, R.anim.scale_out_disappear)
            BaseActivity.TransitionMode.FADE -> overridePendingTransition(R.anim.fade_in_disappear, R.anim.fade_out_disappear)
            else -> {
            }
        }
    }

    abstract fun getContainerTargetView(): View?


    fun showLoadingView() {
        toggleShowLoading(true, null)
    }

    fun showErrorView() {
        toggleShowError(true, "系统出错，请检查网络设置后重新进入该页面", null)
    }

    fun showNONetView(onClickListener: View.OnClickListener) {
        toggleNetworkError(true, onClickListener)
    }

    fun showNODataView(str: String) {
        toggleShowEmpty(true, str, R.mipmap.ic_no_data)
    }

    fun stopAllView() {
        toggleShowLoading(false, null)
    }

    protected fun getVaryViewHelperController(): VaryViewHelperController? {
        if (getContainerTargetView() != null && mVaryViewHelperController == null) {
            mVaryViewHelperController = VaryViewHelperController(getContainerTargetView())
        }
        return mVaryViewHelperController
    }

    /**
     * toggle show common_over_loading
     *
     * @param toggle
     */
    protected fun toggleShowLoading(toggle: Boolean, msg: String?) {
        if (null == getVaryViewHelperController()) {
            throw IllegalArgumentException("You must return a right target view for common_over_loading")
        }
        if (toggle) {
            mVaryViewHelperController?.showLoading(msg)
        } else {
            mVaryViewHelperController?.restore()
        }
    }

    /**
     * toggle show empty
     *
     * @param toggle
     */
    protected fun toggleShowEmpty(toggle: Boolean, msg: String, imageId: Int) {
        if (null == getVaryViewHelperController()) {
            throw IllegalArgumentException("You must return a right target view for common_over_loading")
        }

        if (toggle) {
            mVaryViewHelperController?.showEmpty(msg, imageId)
        } else {
            mVaryViewHelperController?.restore()
        }
    }

    /**
     * toggle show empty
     *
     * @param toggle
     */
    protected fun toggleShowEmpty(toggle: Boolean, msg: String, onClickListener: View.OnClickListener, imageId: Int, isNeedMargin: Boolean) {
        if (null == getVaryViewHelperController()) {
            throw IllegalArgumentException("You must return a right target view for common_over_loading")
        }

        if (toggle) {
            mVaryViewHelperController?.showEmpty(msg, onClickListener, imageId, isNeedMargin)
        } else {
            mVaryViewHelperController?.restore()
        }
    }

    /**
     * toggle show error
     *
     * @param toggle
     */
    protected fun toggleShowError(toggle: Boolean, msg: String, onClickListener: View.OnClickListener?) {
        if (null == getVaryViewHelperController()) {
            throw IllegalArgumentException("You must return a right target view for common_over_loading")
        }

        if (toggle) {
            mVaryViewHelperController?.showError(msg, onClickListener)
        } else {
            mVaryViewHelperController?.restore()
        }
    }

    /**
     * toggle show network error
     *
     * @param toggle
     */
    protected fun toggleNetworkError(toggle: Boolean, onClickListener: View.OnClickListener) {
        if (null == getVaryViewHelperController()) {
            throw IllegalArgumentException("You must return a right target view for common_over_loading")
        }

        if (toggle) {
            mVaryViewHelperController?.showNetworkError(onClickListener)
        } else {
            mVaryViewHelperController?.restore()
        }
    }


}