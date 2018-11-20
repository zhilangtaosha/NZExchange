package com.nze.nzexchange.controller.base

import android.content.Intent
import com.kaopiz.kprogresshud.KProgressHUD
import com.nze.nzeframework.ui.BaseActivity
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

    fun <T> netTfWithDialog(): ObservableTransformer<T, T> = ObservableTransformer { observable ->
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    mProgressDialog.show()
                }
                .doFinally {
                    mProgressDialog.dismiss()
                }
    }

    fun <T> netTf(): ObservableTransformer<T, T> = ObservableTransformer { observable ->
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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