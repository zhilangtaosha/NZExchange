package com.nze.nzexchange.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable
import kotlinx.android.parcel.Parcelize

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/7
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class TransactionPairsBean(
        val createTime: Long,
        val currency: String,//交易货币
        val exchangeRate: Double,
        val id: String,
        val mainCurrency: String,//计价货币
        val remark: String?,
        val status: Int,
        val transactionPair: String,//交易对
        val gain: Double,
        var optional: Int = 0,
        val volume: Double,
        val popular: Int,//热门交易对状态
        val deal: Double
) : Parcelable {

    constructor() : this(0, "", 0.0, "", "", "", 0, "", 0.0, 0, 0.0, 0, 0.0) {}

    companion object {
        fun getAllTransactionPairs(): Flowable<Result<MutableList<TransactionPairsBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .getAllTransactionPairs()
            }
        }

        fun getTransactionPairs(mainCurrency: String, userId: String?): Flowable<Result<MutableList<TransactionPairsBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .getTransactionPairs(mainCurrency, userId)
            }
        }


        fun getOptionalTransactionPair(userId: String): Flowable<Result<MutableList<TransactionPairsBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .getOptionalTransactionPair(userId)
            }
        }

        fun findTransactionPairs(currency: String): Flowable<Result<MutableList<TransactionPairsBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .findTransactionPairs(currency)
            }
        }

        fun marketPopular( userId: String?=null): Flowable<Result<MutableList<TransactionPairsBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .marketPopular(userId)
            }
        }
    }
}