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
 * @类 说 明: OTC 买入返回实体
 * @创建时间：2018/11/21
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class PlaceAnOrderBean(
        val accmoney: Accmoney,
        val poolId: String,
        val suborderAmount: Double,
        val suborderCreateTime: Long,
        val suborderId: String,
        val suborderNum: Int,
        val suborderOverTime: Long,
        val suborderPrice: Double,
        val suborderStatus: Int,
        val tokenId: String,
        val transactionType: Int,
        val userIdBu: String,
        val userIdSell: String
) : Parcelable {
    companion object {
        fun submitNet(poolId: String, userIdSell: String, userIdBu: String, suborderAmount: String, tokenId: String): Flowable<Result<PlaceAnOrderBean>> {
            return Flowable.defer {
                NRetrofit.instance
                        .createService()
                        .placeAnOrder(poolId, userIdSell, userIdBu, suborderAmount, tokenId)
            }
        }
    }
}

