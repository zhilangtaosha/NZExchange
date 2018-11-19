package com.nze.nzeframework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nze.nzeframework.R
import com.nze.nzeframework.loadcontrol.VaryViewHelperController
import com.nze.nzeframework.netstatus.NetChangeObserver
import com.nze.nzeframework.netstatus.NetStateReceiver
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.trello.rxlifecycle2.components.support.RxFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

abstract class BaseFragment:RxFragment() {

    protected var mNetChangeObserver: NetChangeObserver? = null
    private var mVaryViewHelperController: VaryViewHelperController? = null
    private var loadingViewContainer: View? = null
    lateinit var  mBaseActivity: BaseActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBaseActivity = activity as BaseActivity
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
            NetStateReceiver.registerNetworkStateReceiver(mBaseActivity)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(getRootView(),container,false)
        initView(rootView)
        return rootView
    }

    abstract fun getRootView(): Int
    abstract fun initView(rootView:View)


    @Subscribe
    fun <T> onEventMainThread(eventCenter: EventCenter<T>?) {
        if (null != eventCenter) {
            onEventComming(eventCenter)
        }
    }

    protected abstract fun <T> onEventComming(eventCenter: EventCenter<T>)


    abstract fun isBindEventBusHere(): Boolean
    abstract fun isBindNetworkListener(): Boolean


    protected abstract fun onNetworkConnected(type: NetUtils.NetType)

    protected abstract fun onNetworkDisConnected()



    override fun onDestroy() {
        super.onDestroy()
        if (isBindNetworkListener())
            NetStateReceiver.removeRegisterObserver(mNetChangeObserver)
        if (isBindEventBusHere()) {
            EventBus.getDefault().unregister(this)
        }
    }

    abstract fun getContainerTargetView(): View?


    protected fun showLoadingView() {
        toggleShowLoading(true, null)
    }

    protected fun showErrorView() {
        toggleShowError(true, "系统出错，请检查网络设置后重新进入该页面", null)
    }

    protected fun showNONetView(onClickListener: View.OnClickListener) {
        toggleNetworkError(true, onClickListener)
    }

    protected fun showNODataView(str: String) {
        toggleShowEmpty(true, str, R.mipmap.ic_no_data)
    }

    protected fun stopAllView() {
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