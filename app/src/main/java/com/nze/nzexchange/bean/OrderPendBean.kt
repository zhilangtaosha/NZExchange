package com.nze.nzexchange.bean

import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable
import retrofit2.http.Field
import retrofit2.http.Query

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/1/7
 */
data class OrderPendBean(
        val amount: String,
        val ctime: Double,
        val deal_fee: String,
        val deal_money: String,
        val deal_stock: String,
        val id: String,
        val left: String,
        val maker_fee: String,
        val market: String,
        val mtime: Double,
        val price: String,
        val side: Int,
        val source: String,
        val taker_fee: String,
        val type: Int,
        val userId: String,
        val ftime: Double,
        val status: Int
) {
    companion object {
        fun orderPending(
                currencyId: String,
                userId: String?
        ): Flowable<Result<MutableList<OrderPendBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .orderPending(currencyId, userId)
            }
        }


        fun cancelOrder(
                orderId: String,
                userId: String,
                currencyId: String
        ): Flowable<Result<OrderPendBean>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .cancelOrder(orderId, userId, currencyId)
            }
        }

        fun orderTracking(
                currency: String?,
                mainCurrency: String?,
                userId: String?,
                status: Int?,
                transactionType: Int?
        ): Flowable<Result<MutableList<OrderPendBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .orderTracking(currency, mainCurrency, userId, status, transactionType)
            }
        }
    }
}