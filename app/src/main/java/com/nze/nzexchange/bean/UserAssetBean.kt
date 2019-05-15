package com.nze.nzexchange.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable
import kotlinx.android.parcel.Parcelize
import retrofit2.http.Field
import retrofit2.http.Query

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
        fun getUserAssets(
                userId: String,
                tokenUserId: String,
                tokenUserKey: String
        ): Flowable<Result<MutableList<UserAssetBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .getUserAssets(userId, tokenUserId, tokenUserKey)
            }
        }

        /**
         * 币币账户
         */
        fun assetInquiry(
                userId: String,
                tokenId: String? = null,
                tokenName: String? = null,
                tokenUserId: String,
                tokenUserKey: String
        ): Flowable<Result<MutableList<UserAssetBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .assetInquiry(userId, tokenId, tokenName, tokenUserId, tokenUserKey)
            }
        }
    }
}