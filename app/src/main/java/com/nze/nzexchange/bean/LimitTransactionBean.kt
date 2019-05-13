package com.nze.nzexchange.bean

import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable
import retrofit2.http.Field

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
                currencyId: String,
                number: Double,
                price: Double,
                tokenUserId: String,
                tokenUserKey: String
        ): Flowable<Result<LimitTransactionBean>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .limitTransaction(transactionType, userId, currencyId, number, price, tokenUserId, tokenUserKey)
            }
        }


        fun marketTransaction(
                transactionType: Int,
                userId: String,
                currencyId: String,
                number: Double,
                tokenUserId: String,
                tokenUserKey: String
        ): Flowable<Result<LimitTransactionBean>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .marketTransaction(transactionType, userId, currencyId, number, tokenUserId, tokenUserKey)
            }
        }
    }
}