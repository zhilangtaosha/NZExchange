package com.nze.nzexchange.controller.common

import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.config.HttpConfig
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:循环操作某个方法
 * @创建时间：2019/5/29
 */
class NLoopAction(val act: NBaseActivity) {
    var disposable: Disposable? = null

    companion object {
        fun getInstance(act: NBaseActivity): NLoopAction {
            return NLoopAction(act)
        }
    }

    fun loop(action: () -> Unit) {
        if (disposable != null && !disposable?.isDisposed!!)
            disposable!!.dispose()
        var b = 1
        if (HttpConfig.isLoop) {
            b = HttpConfig.LOOP_NUM
        }
        disposable = Flowable.interval(0, HttpConfig.LOOP_INTERVAL_TIME, TimeUnit.SECONDS)
                .filter { b > 0 }
                .compose(act.netTf())
                .subscribe {
                    action.invoke()
                    b--
                }
    }

    fun close() {
        if (disposable != null && !disposable?.isDisposed!!) {
            disposable!!.dispose()
        }
    }
}