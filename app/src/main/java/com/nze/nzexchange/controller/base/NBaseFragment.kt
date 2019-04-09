package com.nze.nzexchange.controller.base

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kaopiz.kprogresshud.KProgressHUD
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.ui.BaseFragment
import com.nze.nzexchange.config.RrefreshType
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

abstract class NBaseFragment : BaseFragment() {
    protected var isViewCreated: Boolean = false
    protected var isFirstVisible: Boolean = true
    protected var isFragmentVisible: Boolean = false

    private var listener: OnFragmentInteractionListener? = null
    val mProgressDialog: KProgressHUD by lazy {
        KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
    }
    val PAGE_SIZE = 10
    var page = 1
    var refreshType: RrefreshType = RrefreshType.INIT
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    fun skipActivity(cls: Class<*>) {
        startActivity(Intent(activity, cls))
    }

    fun <T> netTfWithDialog(): FlowableTransformer<T, T> = FlowableTransformer { observable ->
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY))
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
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY))
    }


    val onError: (e: Throwable) -> Unit = { e: Throwable ->
        NLog.i("onError...")
    }

    fun showToast(content: String) {
        Toast.makeText(activity, content, Toast.LENGTH_SHORT).show()
    }

    open fun getDataFromNet() {

    }

    //--------------懒加载---------------------
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        isFragmentVisible = isVisibleToUser

        //当 View 创建完成切 用户可见的时候请求 且仅当是第一次对用户可见的时候请求自动数据
        if (isVisibleToUser && isViewCreated && isFirstVisible) {
            onFirstRequest()
            isFirstVisible = false
        }
        // 由于每次可见都需要刷新所以我们只需要判断  Fragment 展示在用户面面前了，view 初始化完成了 然后即可以请求数据了
        if (isVisibleToUser && isViewCreated) {
            onVisibleRequest()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isViewCreated = true

        if (isFragmentVisible && isFirstVisible) {
            //Adapter 默认展示的那个 Fragment ，或者隔 tab 选中的时候  requestData 推迟到 onCreateView 后
            onFirstRequest()
            isFirstVisible = false
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    // 只有在 Fragment 第一次对用户可见的时候才去请求
    open fun onFirstRequest() {

    }

    //每次 Fragment 对用户可见都会去请求
    open fun onVisibleRequest() {

    }


}