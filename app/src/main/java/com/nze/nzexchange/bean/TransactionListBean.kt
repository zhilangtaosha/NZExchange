package com.nze.nzexchange.bean

import android.os.Parcelable
import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable
import kotlinx.android.parcel.Parcelize
import retrofit2.http.Query

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:充值记录
 * @创建时间：2019/1/11
 */
@Parcelize
data class TransactionListBean(
        val amount: Double,
        val blockNo: String?,
        val dateTime: String?,
        val datetime: String?,
        val createTime: Long,
        val fee: Double,
        val from: String?,
        val state: String?,
        val to: String,
        val txid: String?,
        val token: String?,
        val status: Int?,
        val userId: String?
) : Parcelable {
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


        fun tickRecord(
                userId: String,
                currency: String,
                pageNumber: Int,
                pageSize: Int
        ): Flowable<Result<MutableList<TransactionListBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .tickRecord(userId, currency, pageNumber, pageSize)
            }
        }
    }


}