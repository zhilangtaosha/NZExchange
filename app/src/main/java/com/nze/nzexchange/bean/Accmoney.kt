package com.nze.nzexchange.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/11/22
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class Accmoney(
        val accmoneyBank: String?,
        val accmoneyBankcard: String?,
        val accmoneyBanktype: String?,
        val accmoneyCreateTime: String?,
        val accmoneyId: String?,
        val accmoneyStatus: String?,
        val accmoneyUpdateTime: String?,
        val accmoneyUpdateUser: String?,
        val accmoneyWeixinacc: String?,
        val accmoneyWeixinurl: String?,
        val accmoneyZfbacc: String?,
        val accmoneyZfburl: String?,
        val membId: String?,
        val userId: String?,
        val phone: String?,
        val trueName: String?
) : Parcelable