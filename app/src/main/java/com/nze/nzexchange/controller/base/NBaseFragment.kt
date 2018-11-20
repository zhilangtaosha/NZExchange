package com.nze.nzexchange.controller.base

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.kaopiz.kprogresshud.KProgressHUD
import com.nze.nzeframework.ui.BaseFragment
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

abstract class NBaseFragment : BaseFragment() {
    private var listener: OnFragmentInteractionListener? = null
    val mProgressDialog: KProgressHUD by lazy {
        KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
    }

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
                }
                .doFinally {
                    mProgressDialog.dismiss()
                }
    }

    fun <T> netTf(): FlowableTransformer<T, T> = FlowableTransformer { observable ->
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY))
                .doOnSubscribe {
                    mProgressDialog.show()
                }
                .doFinally {
                    mProgressDialog.dismiss()
                }
    }


    val onError: (e: Throwable) -> Unit = { e: Throwable ->

    }
}