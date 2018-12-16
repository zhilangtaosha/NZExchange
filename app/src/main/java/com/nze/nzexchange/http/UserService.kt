package com.nze.nzexchange.http

import com.nze.nzexchange.bean.LoginBean
import com.nze.nzexchange.bean.RegisterBean
import com.nze.nzexchange.bean.Result
import com.nze.nzexchange.bean.SetPayMethodBean
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
    ): Flowable<Result<LoginBean>>

    //退出登录
    @FormUrlEncoded
    @POST("/manaTokenAllBus/zyy/manaTokenDel.json")
    fun logout(@Field("tokenUserId") tokenUserId: String,
               @Field("tokenUserKey") tokenUserKey: String,
               @Field("tokenSystreeId") tokenSystreeId: String
    ): Flowable<Result<String>>


    //修改当前登录人的收款方式
    @FormUrlEncoded
    @POST("/meRunBus/meAccmoney/zyy/saveOneEntity_meAccmoney_curToken_t.json")
    fun setPayMethod(
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String,
            @Field("accmoneyBank") accmoneyBank: String?,
            @Field("accmoneyBankcard") accmoneyBankcard: String?,
            @Field("accmoneyBanktype") accmoneyBanktype: String?,
            @Field("accmoneyWeixinurl") accmoneyWeixinurl: String?,
            @Field("accmoneyWeixinacc") accmoneyWeixinacc: String?,
            @Field("accmoneyZfburl") accmoneyZfburl: String?,
            @Field("accmoneyZfbacc") accmoneyZfbacc: String?
    ): Flowable<Result<SetPayMethodBean>>

    //获取当前用户的收款方式
    @FormUrlEncoded
    @POST("/meRunBus/meAccmoney/zyy/findDataVoByBusKey_meAccmoney_curToken.json")
    fun getPayMethod(
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String,
            @Field("tokenSystreeId") tokenSystreeId: String
    ): Flowable<Result<SetPayMethodBean>>
}