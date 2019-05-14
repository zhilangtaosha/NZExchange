package com.nze.nzexchange.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import com.nze.nzexchange.http.NRetrofit
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
        val accmoney: Accmoney?,
        val poolId: String,
        val suborderAmount: Double,
        val suborderCreateTime: Long,
        val suborderId: String,
        val suborderNum: Double,
        val suborderPayTime: Long,
        var suborderOverTime: Long,
        val suborderPrice: Double,
        val suborderReleaseTime: Long,
        //1001:待付款 / 1002待放币 / 1003已完成 / 1004主动取消 / 1005超时取消
        val suborderStatus: Int,
        val suborderUpdateTime: Long,
        val tokenId: String,
        val transactionType: Int,
        val userIdBu: String,
        val userIdSell: String,
        val remark: String
) : Parcelable {
    var phoneTime: Long = System.currentTimeMillis()

    companion object {
        //交易类型
        const val TRANSACTIONTYPE_BUY = 1//用户购买
        const val TRANSACTIONTYPE_SALE = 0//用户出售

        //订单状态
        const val SUBORDERSTATUS_WAIT_PAY = 1001//待付款
        const val SUBORDERSTATUS_WAIT_RELEASE = 1002//待放币
        const val SUBORDERSTATUS_COMPLETED = 1003//已完成
        const val SUBORDERSTATUS_ACTIVE_CACEL = 1004//主动取消
        const val SUBORDERSTATUS_OVERTIME_CACEL = 1005//z超时取消


        //请求子订单列表分类
        //1002未完成 1003已完成  1004取消
        const val REQUEST_NO_COMPLETE = 1001
        const val REQUEST_COMPLETED = 1003
        const val REQUEST_CANCEL = 1004

        fun getStatus(code: Int): String {
            return when (code) {
                SUBORDERSTATUS_WAIT_PAY -> "待付款"
                SUBORDERSTATUS_WAIT_RELEASE -> "待放币"
                SUBORDERSTATUS_COMPLETED -> "已完成"
                SUBORDERSTATUS_ACTIVE_CACEL -> "买家取消"
                SUBORDERSTATUS_OVERTIME_CACEL -> "订单取消"
                else -> ""
            }
        }

        //购买吃单
        fun submitNet(poolId: String, userIdSell: String,
                      userIdBu: String,
                      suborderAmount: String,
                      tokenId: String,
                      tokenUserId: String,
                      tokenUserKey: String): Flowable<Result<SubOrderInfoBean>> {
            return Flowable.defer {
                NRetrofit.instance
                        .buyService()
                        .placeAnOrder(poolId, userIdSell, userIdBu, suborderAmount, tokenId, tokenUserId, tokenUserKey)
            }
        }

        //获取用户子订单列表
        fun findSubOrderPoolNet(userId: String,
                                pageNumber: Int,
                                pageSize: Int,
                                suborderStatus: Int?,
                                tokenUserId: String,
                                tokenUserKey: String): Flowable<Result<MutableList<SubOrderInfoBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .buyService()
                        .findSubOrderPool(userId, pageNumber, pageSize, suborderStatus, tokenUserId, tokenUserKey)
            }
        }

        //获取子订单详情
        fun findSubOrderInfoNet(
                userId: String,
                subOrderId: String,
                tokenUserId: String,
                tokenUserKey: String
        ): Flowable<Result<SubOrderInfoBean>> {
            return Flowable.defer {
                NRetrofit.instance
                        .buyService()
                        .findSubOrderInfo(userId, subOrderId, tokenUserId, tokenUserKey)
            }
        }

        //出售出单
        fun sellNet(poolId: String, userIdSell: String,
                    userIdBu: String,
                    suborderAmount: String,
                    tokenId: String,
                    tokenUserId: String,
                    tokenUserKey: String
        ): Flowable<Result<SubOrderInfoBean>> {
            return Flowable.defer {
                NRetrofit.instance
                        .sellService()
                        .placeAnOrder(poolId, userIdSell, userIdBu, suborderAmount, tokenId, tokenUserId, tokenUserKey)
            }
        }

    }


}

