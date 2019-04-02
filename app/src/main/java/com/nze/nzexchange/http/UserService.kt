package com.nze.nzexchange.http

import com.nze.nzexchange.bean.*
import io.reactivex.Flowable
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    @POST("meRunBus/meMemb/save_meMemb_regBus.json")
    fun register(@Field("userPhone") userPhone: String?,//手机注册
                 @Field("userPhonePrecode") userPhonePrecode: String?,//国家电话区号
                 @Field("userEmail") userEmail: String?,//邮箱注册
                 @Field("userPassworUcode") userPassworUcode: String,//密码
                 @Field("checkcodeId") checkcodeId: String,//验证码id
                 @Field("checkcodeVal") checkcodeVal: String,//验证码
                 @Field("membCountry") membCountry: String?//国家码
    ): Flowable<Result<Any>>


    //登录
    @FormUrlEncoded
    @POST("manaTokenRunBus/tokenLogin/manaTokenLogin.json")
    fun login(@Field("userTag") userTag: String,
              @Field("userPassworUcode") userPassworUcode: String,
              @Field("checkCodeId") checkCodeId: String?,
              @Field("checkCodeVal") checkCodeVal: String?
    ): Flowable<Result<LoginBean>>

    //退出登录
    @FormUrlEncoded
    @POST("/manaTokenAllBus/manaTokenDel.json")
    fun logout(@Field("tokenUserId") tokenUserId: String,
               @Field("tokenUserKey") tokenUserKey: String,
               @Field("tokenSystreeId") tokenSystreeId: String
    ): Flowable<Result<String>>


    //修改当前登录人的收款方式
    @FormUrlEncoded
    @POST("/meRunBus/meAccmoney/saveOneEntity_meAccmoney_curToken_t.json")
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
    @POST("/meRunBus/meAccmoney/findDataVoByBusKey_meAccmoney_curToken.json")
    fun getPayMethod(
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String,
            @Field("tokenSystreeId") tokenSystreeId: String
    ): Flowable<Result<SetPayMethodBean>>

    //获取验证码
    @FormUrlEncoded
    @POST("/msgRunBus/msgMessagetreeSend/messagetree_sendMessage_checkCode.json")
    fun getVerifyCode(
            @Field("messageBustag") messageBustag: String,
            @Field("messageTo") messageTo: String
    ): Flowable<Result<VerifyBean>>


    //重置密码
    @FormUrlEncoded
    @POST("/manaTokenAllBus/manaTokenUpdatePwByUserTag.json")
    fun findPassword(
            @Field("checkcodeId") checkcodeId: String,
            @Field("checkcodeVal") checkcodeVal: String,
            @Field("userPhone") userPhone: String,
            @Field("userEmail") userEmail: String,
            @Field("userPassworUcode") userPassworUcode: String
    ): Flowable<Result<FindPasswordBean>>


    //修改密码
    @FormUrlEncoded
    @POST("/manaTokenAllBus/manaTokenUpdatePw.json")
    fun modifyPassword(
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String,
            @Field("tokenSystreeId") tokenSystreeId: String,
            @Field("curUserpasswordUcode") curUserpasswordUcode: String,
            @Field("newUserPassworUcode") newUserPassworUcode: String
    ): Flowable<Result<Any>>

    //上传文件
    @Multipart
    @POST("/fileWebAllBus/uploadAttachement.json")
    fun uploadFile(
            @Query("tokenUserId") tokenUserId: String,
            @Query("tokenUserKey") tokenUserKey: String,
            @Query("tokenSystreeId") tokenSystreeId: String,
            @Query("path") path: String,
            @Query("busId") busId: String,
            @Query("coverOldBusFile") coverOldBusFile: Boolean,
            @Part file: MultipartBody.Part
    ): Flowable<Result<UploadBean>>

    //提交实名信息
    @GET("/meRunBus/meMemb/saveOneEntity_meMemb_curToken_t.json")
    fun primaryAuthentication(
            @Query("tokenUserId") tokenUserId: String,
            @Query("tokenUserKey") tokenUserKey: String,
            @Query("tokenSystreeId") tokenSystreeId: String,
            @Query("membName") membName: String,
            @Query("membIdentitycard") membIdentitycard: String,
            @Query("membCountry") membCountry: String
    ): Flowable<Result<String>>

    //首页
    @FormUrlEncoded
    @POST("indexAppRunBus/indexAllData_t.json")
    fun indexAllData(
            @Field("containNotice") containNotice: Boolean,
            @Field("containTempImg") containTempImg: Boolean,
            @Field("containTempCont") containTempCont: Boolean
    ): Flowable<Result<IndexTopBean>>

    //设置支付密码
    @FormUrlEncoded
    @POST("meRunBus/meBuspw/resetBuspwByCheckcode_pay.json")
    fun setBuspw(
            @Field("checkcodeId") checkcodeId: String,
            @Field("checkcodeVal") checkcodeVal: String,
            @Field("userEmail") userEmail: String,
            @Field("newBuspwUcode") newBuspwUcode: String
    ): Flowable<Result<Any>>

    //绑定手机
    @FormUrlEncoded
    @POST("manaTokenAllBus/manaToken_bindUserPhone_curToken.json")
    fun bindPhone(
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String,
            @Field("checkcodeId") checkcodeId: String,
            @Field("checkcodeVal") checkcodeVal: String,
            @Field("userPhone") userPhone: String
    ): Flowable<Result<Any>>

    //绑定邮箱
    @FormUrlEncoded
    @POST("manaTokenAllBus/manaToken_bindUserEmail_curToken.json")
    fun bindEmail(
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String,
            @Field("checkcodeId") checkcodeId: String,
            @Field("checkcodeVal") checkcodeVal: String,
            @Field("userEmail") userEmail: String
    ): Flowable<Result<Any>>

    //实名材料信息
    @FormUrlEncoded
    @POST("meRunBus/meMereally/findDataVoByBusKey_meMereally_curToken.json")
    fun getReanNameAuthentication(
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<RealNameAuthenticationBean>>
}