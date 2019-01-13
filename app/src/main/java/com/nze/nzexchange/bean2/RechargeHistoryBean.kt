package com.nze.nzexchange.bean2

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/25
 */
@Parcelize
data class RechargeHistoryBean(
        var title: String = "",
        var month: String = "",
        var time: String = "",
        var currency: String = "USDT",
        var rechargeAmount: String = "",
        var totalAmount: String = "余额 12345.23USDT",
        var address: String = "",
        var datetime: Long=0,
        var isTitle: Boolean = false
) : Parcelable {

    companion object {

        fun getList() = mutableListOf<RechargeHistoryBean>().apply {
            add(RechargeHistoryBean("本月", isTitle = true))
            add(RechargeHistoryBean(month = "12-21"))
            add(RechargeHistoryBean(month = "12-18"))
            add(RechargeHistoryBean(month = "12-02"))

            add(RechargeHistoryBean("2018-11", isTitle = true))
            add(RechargeHistoryBean(month = "11-27"))
            add(RechargeHistoryBean(month = "11-13"))
            add(RechargeHistoryBean(month = "11-06"))
        }
    }
}