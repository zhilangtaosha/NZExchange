package com.nze.nzexchange.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明: 提币地址实体
 * @创建时间：2019/5/16
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class CurrenyWithdrawAddressBean(
        val addTime: Long?,
        val address: String,
        val id: Int?,
        val market: String?,
        val status: Int?,
        val tokenName: String?,
        val userId: String?,
        val title: String,
        var isSelect: Boolean = false
): Parcelable