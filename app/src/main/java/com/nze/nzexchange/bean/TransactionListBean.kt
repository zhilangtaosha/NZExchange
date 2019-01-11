package com.nze.nzexchange.bean

import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable
import retrofit2.http.Query

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:充值记录
 * @创建时间：2019/1/11
 */
data class TransactionListBean(
        val address: String,
        val blockNo: Int,
        val datetime: Long,
        val number: String,
        val txid: String
) {
    companion object {
        fun getTransactionList(
                userId: String,
                currency: String,
                pageNumber: Int,
                pageSize: Int
        ): Flowable<Result<MutableList<TransactionListBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .getTransactionList(userId, currency, pageNumber, pageSize)
            }
        }
    }
}