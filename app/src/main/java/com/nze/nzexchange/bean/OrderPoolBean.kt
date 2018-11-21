package com.nze.nzexchange.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import com.nze.nzexchange.http.NRetrofit
import com.nze.nzexchange.http.Result
import io.reactivex.Flowable
import kotlinx.android.parcel.Parcelize

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/11/21
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class OrderPoolBean(
        val accmoney: Accmoney,
        val nick: String,
        val poolAllCount: Double,
        val poolId: String,
        val poolMaxamount: Double,
        val poolMinamount: Double,
        val poolNo: String,
        val poolPrice: Double,
        val remark: String,
        val tokenId: String,
        val totalOrder: Int,
        val transactionType: String,
        val userId: String
): Parcelable {
    companion object {
        fun getFromNet(tokenId: String): Flowable<Result<MutableList<OrderPoolBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .createService()
                        .findOrderPool(tokenId)
            }
        }
    }
}
@SuppressLint("ParcelCreator")
@Parcelize
data class Accmoney(
        val accmoneyBank: String,
        val accmoneyBankcard: String,
        val accmoneyBanktype: String,
        val accmoneyCreateTime: String,
        val accmoneyId: String,
        val accmoneyStatus: String,
        val accmoneyUpdateTime: String,
        val accmoneyWeixinacc: String,
        val accmoneyWeixinurl: String,
        val accmoneyZfbacc: String,
        val accmoneyZfburl: String,
        val membId: String,
        val userId: String
):Parcelable