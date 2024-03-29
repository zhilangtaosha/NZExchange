package com.nze.nzexchange.bean

import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:OTC 广告列表实体
 * @创建时间：2018/11/22
 */
data class FindSellBean(
        val poolAllCount: Double,
        val poolCreateTime: Long,
        val poolId: String,
        val poolLeftamount: Double,
        val poolLockamount: Double,
        val poolMaxamount: Double,
        val poolMinamount: Double,
        val poolNo: String,
        val poolPrice: Double,
        val poolStatus: Int,
        val poolSuborderamount: Double,
        val remark: String,
        val tokenId: String,
        val transactionType: Int,
        val userId: String,
        val poolSuccessamount: Double//已成交数量
) {
    companion object {
        const val TRANSACTIONTYPE_BUY = 0
        const val TRANSACTIONTYPE_SALE = 1
        fun getFromNet(userId: String, page: Int, pageSize: Int): Flowable<Result<MutableList<FindSellBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .buyService()
                        .findSellList(userId,page,pageSize)
            }
        }
    }
}