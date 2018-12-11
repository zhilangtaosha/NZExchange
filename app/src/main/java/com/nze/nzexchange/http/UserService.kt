package com.nze.nzexchange.http

import com.nze.nzexchange.bean.RegisterBean
import com.nze.nzexchange.bean.UserBean
import io.reactivex.Flowable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/6
 */
interface UserService {
    //注册
    @FormUrlEncoded
    @POST("meRunBus/meMemb/zyy/save_meMemb_regBus.json")
    fun register(@Field("userPhone") userPhone: String?,
                 @Field("userEmail") userEmail: String?,
                 @Field("userName") userName: String?,
                 @Field("userPassworUcode") userPassworUcode: String,
                 @Field("checkcodeId ") checkcodeId: String?,
                 @Field("checkcodeVal ") checkcodeVal: String?
    ): Flowable<Result<RegisterBean>>


    //登录
    @FormUrlEncoded
    @POST("manaTokenRunBus/tokenLogin/zyy/manaTokenLogin.json")
    fun login(@Field("userTag") userTag: String,
              @Field("userPassworUcode") userPassworUcode: String,
              @Field("checkCodeId") checkCodeId: String?,
              @Field("checkCodeVal") checkCodeVal: String?
    ): Flowable<Result<UserBean>>

}