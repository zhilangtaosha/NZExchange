package com.nze.nzexchange.bean

import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:提币信息，提供手续费等信息
 * @创建时间：2019/5/18
 */
data class CurrencyWithdrawInfoBean(
        val feeAmt: String,
        val feeCode: String,
        val feeGetbiText: String,
        val feeId: String,
        val feeLimitlowFill: Double,
        val feeLimitlowGet: Double,
        val feeName: String,
        val feeRate: Double,//提币手续费
        val feeStatus: Int,
        val feeType1: String,
        val feeUpdateTime: Long,
        val frrFillbiText: String
) {
    companion object {
        fun getCurrencyWithdrawInfo(tokenName: String): Flowable<Result<CurrencyWithdrawInfoBean>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .getCurrencyWithdrawInfo(tokenName)
            }
        }
    }
}