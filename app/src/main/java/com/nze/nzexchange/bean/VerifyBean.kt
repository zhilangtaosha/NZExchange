package com.nze.nzexchange.bean

import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.config.VerifyType
import com.nze.nzexchange.http.CRetrofit
import com.nze.nzexchange.tools.RegularTool
import io.reactivex.Flowable
import retrofit2.http.Field

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/17
 */
data class VerifyBean(val checkcodeId: String) {

    companion object {
        const val TYPE_FIND_PASSWORD = 0x001
        const val TYPE_REGISTER = 0x002
        const val TYPE_COMMON = 0x003
        fun getVerifyCodeNet(account: String,
                             type: Int): Flowable<Result<VerifyBean>> {
            var messageBustag = when (type) {
                TYPE_FIND_PASSWORD -> {
                    if (RegularTool.isEmail(account)) {
                        VerifyType.TYPE_FIND_PWD_BY_EMAIL
                    } else {
                        VerifyType.TYPE_FIND_PWD_BY_PHONE
                    }
                }
                TYPE_REGISTER -> {
                    if (RegularTool.isEmail(account)) {
                        VerifyType.TYPE_RESIGER_BY_EMAIL
                    } else {
                        VerifyType.TYPE_RESIGER_BY_PHONE
                    }
                }
                TYPE_COMMON -> {
                    if (RegularTool.isEmail(account)) {
                        VerifyType.TYPE_COMMON_BY_EMAIL
                    } else {
                        VerifyType.TYPE_COMMON_BY_PHONE
                    }
                }
                else -> ""
            }
            return Flowable.defer {
                CRetrofit.instance
                        .userService()
                        .getVerifyCode(messageBustag, account)
            }
        }

    }
}