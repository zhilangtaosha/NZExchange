package com.nze.nzexchange.http

import com.nze.nzexchange.bean.*
import io.reactivex.Flowable
import okhttp3.MultipartBody
import retrofit2.http.*

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
                 @Field("checkcodeId") checkcodeId: String?,
                 @Field("checkcodeVal") checkcodeVal: String?
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

    //获取验证码
    @FormUrlEncoded
    @POST("/msgRunBus/msgMessagetreeSend/zyy/messagetree_sendMessage_checkCode.json")
    fun getVerifyCode(
            @Field("messageBustag") messageBustag: String,
            @Field("messageTo") messageTo: String
    ): Flowable<Result<VerifyBean>>


    //重置密码
    @FormUrlEncoded
    @POST("/manaTokenAllBus/zyy/manaTokenUpdatePwByUserTag.json")
    fun findPassword(
            @Field("checkcodeId") checkcodeId: String,
            @Field("checkcodeVal") checkcodeVal: String,
            @Field("userPhone") userPhone: String,
            @Field("userEmail") userEmail: String,
            @Field("userPassworUcode") userPassworUcode: String
    ): Flowable<Result<FindPasswordBean>>


    //修改密码
    @FormUrlEncoded
    @POST("/manaTokenAllBus/zyy/manaTokenUpdatePw.json")
    fun modifyPassword(
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String,
            @Field("tokenSystreeId") tokenSystreeId: String,
            @Field("curUserpasswordUcode") curUserpasswordUcode: String,
            @Field("newUserPassworUcode") newUserPassworUcode: String
    ): Flowable<Result<HashMap<String, String>>>

    //上传文件
    @Multipart
    @POST("/fileWebAllBus/zyy/uploadAttachement.json")
    fun uploadFile(
            @Part("tokenUserId") tokenUserId: String,
            @Part("tokenUserKey") tokenUserKey: String,
            @Part("tokenSystreeId") tokenSystreeId: String,
            @Part("path") path: String,
            @Part("busId") busId: String,
            @Part("coverOldBusFile") coverOldBusFile: Boolean,
            @Part file: MultipartBody.Part
    ): Flowable<Result<HashMap<String, String>>>
}