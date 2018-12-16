package com.nze.nzexchange.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable
import kotlinx.android.parcel.Parcelize

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:OTC 购买 出售列表实体
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
        val userId: String,
        val poolLeftamount: Double
) : Parcelable {
    companion object {
        //购买列表
        fun getFromNet(tokenId: String,
                       pageNumber: Int,
                       pageSize: Int): Flowable<Result<MutableList<OrderPoolBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .buyService()
                        .findOrderPool(tokenId, pageNumber, pageSize)
            }
        }

        //出售列列表
        fun getSellNet(tokenId: String,
                       pageNumber: Int,
                       pageSize: Int): Flowable<Result<MutableList<OrderPoolBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .sellService()
                        .findOrderPool(tokenId, pageNumber, pageSize)
            }
        }
    }
}
