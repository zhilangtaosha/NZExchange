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
        val amount: Double,
        val ctime: Double,
        val deal_fee: String,
        val deal_money: String,
        val deal_stock: Double,
        val id: String,
        val left: String,
        val maker_fee: String,
        val market: String,
        val mtime: Double,
        val price: Double,
        val side: Int,
        val source: String,
        val taker_fee: String,
        val type: Int,
        val userId: String,
        val ftime: Double,
        val status: Int,
        val currencyId: String
) {
    companion object {
        fun orderPending(
                currencyId: String,
                userId: String?,
                pageNumber: Int = 1,
                pageSize: Int = 20
        ): Flowable<Result<MutableList<OrderPendBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .orderPending(currencyId, userId, pageNumber, pageSize)
            }
        }


        fun cancelOrder(
                orderId: String,
                userId: String,
                currencyId: String?,
                currencyName: String?,
                tokenUserId: String,
                tokenUserKey: String
        ): Flowable<Result<OrderPendBean>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .cancelOrder(orderId, userId, currencyId, currencyName, tokenUserId, tokenUserKey)
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