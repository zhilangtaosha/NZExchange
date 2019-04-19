package com.nze.nzexchange.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable
import kotlinx.android.parcel.Parcelize

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/1/8
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class UserAssetBean(
        val address: String?,
        val available: Double,
        val currency: String,
        val freeze: Double,
        val remark: String?,
        val tokenId: String?,
        val userId: String?,
        val amount: Double?
) : Parcelable {
    companion object {
        /**
         * OTC账户
         */
        fun getUserAssets(userId: String): Flowable<Result<MutableList<UserAssetBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .getUserAssets(userId)
            }
        }

        /**
         * 币币账户
         */
        fun assetInquiry(userId: String): Flowable<Result<MutableList<UserAssetBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .assetInquiry(userId)
            }
        }
    }
}