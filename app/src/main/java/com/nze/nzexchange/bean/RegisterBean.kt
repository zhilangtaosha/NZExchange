package com.nze.nzexchange.bean

import com.nze.nzexchange.http.CRetrofit
import io.reactivex.Flowable
import retrofit2.http.Field

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/11
 */
data class RegisterBean(
        val meAccmoneyEntity: Any,
        val membAg: String,
        val membCreateTime: Long,
        val membCreateTimeStr: String,
        val membCreateUser: String,
        val membEmail: String,
        val membId: String,
        val membIdentitycard: String,
        val membMemo: String,
        val membName: String,
        val membOrd: Int,
        val membPhone: String,
        val membSex: String,
        val membSn: String,
        val membSource: String,
        val membStatus: Int,
        val membStatusStr: String,
        val membUpdateTime: Long,
        val membUpdateTimeStr: String,
        val membUpdateUser: String,
        val mereallyStatus: String,
        val mereallyStatusStr: String,
        val treeauthCode: String,
        val treeauthId: String,
        val treeauthName: String,
        val userEmail: String,
        val userId: String,
        val userImage: String,
        val userName: String,
        val userNick: String,
        val userNo: String,
        val userPhone: String
) {
    companion object {
        fun registerNet(userPhone: String?,//手机注册
                        userPhonePrecode: String?,//国家电话区号
                        userEmail: String?,//邮箱注册
                        userPassworUcode: String,//密码
                        checkcodeId: String,//验证码id
                        checkcodeVal: String,//验证码
                        membCountry: String?//国家码
        ): Flowable<Result<Any>> {
            return Flowable.defer {
                CRetrofit.instance
                        .userService()
                        .register(userPhone, userPhonePrecode, userEmail, userPassworUcode, checkcodeId, checkcodeVal, membCountry)
            }


        }
    }
}