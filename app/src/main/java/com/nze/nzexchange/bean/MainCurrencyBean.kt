package com.nze.nzexchange.bean

import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/9
 */
data class MainCurrencyBean(
        val tokenId: String,
        val tokenStatus: Int,
        val tokenSymbol: String
) {
    companion object {
        fun getCurrency(): Flowable<Result<MutableList<MainCurrencyBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .buyService()
                        .getCurrency()
            }
        }

    }
}