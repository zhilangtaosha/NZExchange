package com.nze.nzexchange.tools

import io.reactivex.ObservableTransformer
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.xml.transform.Transformer
import kotlin.properties.Delegates.observable

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/11/20
 */
class RxTransformer {

    companion object {
        fun <T> applyScheduler(): ObservableTransformer<T, T> = ObservableTransformer { observable ->
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }


}