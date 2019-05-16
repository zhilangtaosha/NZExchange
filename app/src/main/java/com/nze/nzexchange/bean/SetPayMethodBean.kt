package com.nze.nzexchange.bean

import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.http.CRetrofit
import io.reactivex.Flowable
import retrofit2.http.Field

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/16
 */
data class SetPayMethodBean(
        var accmoneyBank: String?,
        var accmoneyBankcard: String?,
        var accmoneyBanktype: String?,
        val accmoneyCreateTime: Long,
        val accmoneyCreateTimeStr: String?,
        val accmoneyCreateUser: String?,
        val accmoneyId: String?,
        val accmoneyStatus: Int,
        val accmoneyStatusStr: String?,
        val accmoneyUpdateTime: Long,
        val accmoneyUpdateTimeStr: String?,
        val accmoneyUpdateUser: String?,
        var accmoneyWeixinacc: String?,
        var accmoneyWeixinurl: String?,
        var accmoneyZfbacc: String?,
        var accmoneyZfburl: String?,
        val membId: String?,
        val membIdentitycard: String?,
        val membName: String?,
        val membSn: String?,
        val treeauthCode: String?,
        val treeauthName: String?,
        val userId: String?,
        var accmoneyBpaySn: String?,
        var accmoneyBpayAccount: String?,
        var accmoneyFrBank: String?,
        var accmoneyFrBankcard: String?,
        var accmoneyFrAccount: String?

) {
    companion object {
        fun setPayMethodNet(
                tokenUserId: String,
                tokenUserKey: String,
                accmoneyBank: String?,
                accmoneyBankcard: String?,
                accmoneyBanktype: String?,
                accmoneyWeixinurl: String?,
                accmoneyWeixinacc: String?,
                accmoneyZfburl: String?,
                accmoneyZfbacc: String?,
                curBuspwUcode: String?,
                accmoneyBpaySn: String?,
                accmoneyBpayAccount: String?,
                accmoneyFrBank: String?,
                accmoneyFrBankcard: String?,
                accmoneyFrAccount: String?
        ): Flowable<Result<SetPayMethodBean>> {
            return Flowable.defer {
                CRetrofit.instance
                        .userService()
                        .setPayMethod(tokenUserId,
                                tokenUserKey,
                                accmoneyBank,
                                accmoneyBankcard,
                                accmoneyBanktype,
                                accmoneyWeixinurl,
                                accmoneyWeixinacc,
                                accmoneyZfburl,
                                accmoneyZfbacc,
                                curBuspwUcode,
                                accmoneyBpaySn,
                                accmoneyBpayAccount,
                                accmoneyFrBank,
                                accmoneyFrBankcard,
                                accmoneyFrAccount)
            }
        }

        fun getPayMethodNet(
                tokenUserId: String,
                tokenUserKey: String,
                tokenSystreeId: String
        ): Flowable<Result<SetPayMethodBean>> {
            return Flowable.defer {
                CRetrofit.instance
                        .userService()
                        .getPayMethod(tokenUserId, tokenUserKey, tokenSystreeId)
            }
        }
    }
}