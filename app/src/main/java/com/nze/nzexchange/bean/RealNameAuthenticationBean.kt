package com.nze.nzexchange.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import com.nze.nzexchange.http.CRetrofit
import io.reactivex.Flowable
import kotlinx.android.parcel.Parcelize
import retrofit2.http.Field

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:实名认证信息
 * 用于判断是否实名认证，以及实名认证到第几步
 * @创建时间：2019/4/2
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class RealNameAuthenticationBean(
        var membCountry: String?,
        val membId: String?,
        //身份证
        var membIdentitycard: String?,
        var membName: String?,
        val membSn: String?,
        val membTruename: String?,
        val mereallyCreateTime: Long?,
        val mereallyCreateTimeStr: String?,
        val mereallyCreateUser: String?,
        val mereallyId: String?,
        /**
         * 100  初始化  会员_上传身份证
        101  审核中   运维_审核身份证
        990  实名通过
        1011 审核不通过  会员_重新上传身份证[审核失败]
         */
        var mereallyStatus: Int?,
        val mereallyStatusStr: String?,
        val mereallyStep1Fileurl: String?,
        val mereallyStep1Name: String?,
        val mereallyStep2Fileurl: String?,
        val mereallyStep2Name: String?,
        val mereallyStep2Pars: String?,
        val mereallyUpdateTime: Long?,
        val mereallyUpdateTimeStr: String?,
        val mereallyUpdateUser: String?,
        val treeauthCode: String?,
        val treeauthName: String?,
        val userId: String?
) : Parcelable {
    companion object {
        fun getReanNameAuthentication(
                tokenUserId: String,
                tokenUserKey: String?,
                tokenSystreeId: String?
        ): Flowable<Result<RealNameAuthenticationBean>> {
            return Flowable.defer {
                CRetrofit.instance
                        .userService()
                        .getReanNameAuthentication(tokenUserId, tokenUserKey, tokenSystreeId)
            }
        }
    }
}