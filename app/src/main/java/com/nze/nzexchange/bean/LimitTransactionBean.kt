package com.nze.nzexchange.bean

import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/1/3
 */
data class LimitTransactionBean(
        val amount: String,
        val ctime: Double,
        val deal_fee: String,
        val deal_money: String,
        val deal_stock: String,
        val id: Int,
        val left: String,
        val maker_fee: String,
        val market: String,
        val mtime: Double,
        val price: String,
        val side: Int,
        val source: String,
        val taker_fee: String,
        val type: Int,
        val user: Int
) {
    companion object {

        fun limitTransaction(
                transactionType: Int,
                userId: String,
                currencyId: Int,
                number: Double,
                price: Double
        ): Flowable<Result<LimitTransactionBean>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .limitTransaction(transactionType, userId, currencyId, number, price)
            }
        }
    }
}