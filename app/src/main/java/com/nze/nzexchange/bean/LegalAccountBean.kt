package com.nze.nzexchange.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:法币账户资产
 * @创建时间：2019/5/21
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class LegalAccountBean(
        val accAbleAmount: Double,
        val accAllAmount: Double,
        val accBeantag: String?,
        val accCreateTime: Long,
        val accCreateTimeStr: String?,
        val accCreateUser: String?,
        val accFronzenAmount: Double,
        val accId: String?,
        val accMemo: String?,
        val accName: String?,
        val accOrd: Int,
        val accStatus: Int,
        val accStatusStr: String?,
        val accType: String?,
        val accUpdateTime: Long,
        val accUpdateTimeStr: String?,
        val accUpdateUser: String?,
        val membId: String?,
        val membIdentitycard: String?,
        val membName: String?,
        val membTruename: String?,
        val systreeId: String?,
        val treeauthCode: String?,
        val treeauthId: String?,
        val treeauthName: String?,
        val userEmail: String?,
        val userId: String,
        val userName: String?,
        val userNick: String?,
        val userNo: String,
        val userPhone: String?,
        val userPhonePrecode: String?
) : Parcelable