package com.nze.nzexchange.bean

import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable
import retrofit2.http.Query

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/1/13
 */
data class TickRecordBean(
        val blockNo: String,
        val datetime: Long,
        val fee: Int,
        val from: String,
        val number: String,
        val state: String,
        val to: String,
        val txid: String
) {
    companion object {
        fun tickRecord(
                userId: String,
                currency: String,
                pageNumber: Int,
                pageSize: Int
        ): Flowable<Result<MutableList<TickRecordBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .tickRecord(userId, currency, pageNumber, pageSize)
            }
        }

    }
}