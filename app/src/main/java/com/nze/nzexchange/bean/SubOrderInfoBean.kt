package com.nze.nzexchange.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import com.nze.nzexchange.http.NRetrofit
import com.nze.nzexchange.http.Result
import io.reactivex.Flowable
import kotlinx.android.parcel.Parcelize
import retrofit2.http.Field

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明: OTC 吃单
 * @创建时间：2018/11/21
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class SubOrderInfoBean(
        val accmoney: Accmoney,
        val poolId: String,
        val suborderAmount: Double,
        val suborderCreateTime: Long,
        val suborderId: String,
        val suborderNum: Int,
        val suborderPayTime: Long,
        val suborderOverTime: Long,
        val suborderPrice: Double,
        val suborderReleaseTime: Long,
        val suborderStatus: Int,
        val suborderUpdateTime: Long,
        val tokenId: String,
        val transactionType: Int,
        val userIdBu: String,
        val userIdSell: String
) : Parcelable {
    companion object {
        //交易类型
        const val TRANSACTIONTYPE_BUY = 1//用户购买
        const val TRANSACTIONTYPE_SALE = 0//用户出售

        //订单状态
        const val SUBORDERSTATUS_WAIT_PAY = 1001//待付款
        const val SUBORDERSTATUS_WAIT_RELEASE = 1002//待放币
        const val SUBORDERSTATUS_COMPLETED = 1003//已完成
        const val SUBORDERSTATUS_ACTIVE_CACEL = 1004//主动取消
        const val SUBORDERSTATUS_OVERTIME_CACEL = 1005//主动取消


        //请求子订单列表分类
        //1002未完成 1003已完成  1004取消
        const val REQUEST_NO_COMPLETE = 1002
        const val REQUEST_COMPLETED = 1003
        const val REQUEST_CANCEL = 1004

        //买单
        fun submitNet(poolId: String, userIdSell: String,
                      userIdBu: String,
                      suborderAmount: String,
                      tokenId: String): Flowable<Result<SubOrderInfoBean>> {
            return Flowable.defer {
                NRetrofit.instance
                        .createService()
                        .placeAnOrder(poolId, userIdSell, userIdBu, suborderAmount, tokenId)
            }
        }

        //获取用户子订单列表
        fun findSubOrderPoolNet(userId: String,
                                pageNumber: Int,
                                pageSize: Int,
                                suborderStatus: Int): Flowable<Result<MutableList<SubOrderInfoBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .createService()
                        .findSubOrderPool(userId, pageNumber, pageSize, suborderStatus)
            }
        }

    }
}
