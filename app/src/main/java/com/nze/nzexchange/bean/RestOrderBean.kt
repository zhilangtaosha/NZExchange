package com.nze.nzexchange.bean

import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明: 挂单信息bean
 * @创建时间：2019/1/2
 */
data class RestOrderBean(
        val mainCurrency: MainCurrency?,
        val rate: Double
) {
    companion object {
        fun getPendingOrderInfo(currencyId: Int, userId: String?): Flowable<Result<RestOrderBean>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .getPendingOrderInfo(currencyId, userId)
            }
        }
    }
}

data class MainCurrency(
        val available: Double,
        val currency: String,
        val freeze: Double,
        val remark: String
)