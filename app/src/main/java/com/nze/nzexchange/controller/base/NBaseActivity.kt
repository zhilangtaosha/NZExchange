package com.nze.nzexchange.controller.base

import android.content.Intent
import android.widget.Toast
import com.kaopiz.kprogresshud.KProgressHUD
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzexchange.NzeApp
import com.trello.rxlifecycle2.android.ActivityEvent
import io.reactivex.FlowableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


abstract class NBaseActivity : BaseActivity() {

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
                }
                .doOnComplete {
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
}