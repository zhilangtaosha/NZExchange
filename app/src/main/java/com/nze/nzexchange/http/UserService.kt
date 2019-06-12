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
            @Field("accmoneyZfbacc") accmoneyZfbacc: String?,
            @Field("curBuspwUcode") curBuspwUcode: String?,
            @Field("accmoneyBpaySn") accmoneyBpaySn: String?,
            @Field("accmoneyBpayAccount") accmoneyBpayAccount: String?,
            @Field("accmoneyFrBank") accmoneyFrBank: String?,
            @Field("accmoneyFrBankcard") accmoneyFrBankcard: String?,
            @Field("accmoneyFrAccount") accmoneyFrAccount: String?
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
    ): Flowable<Result<Any>>


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

    //会员实名_实名认证提交材料
    @FormUrlEncoded
    @POST("/meRunBus/meMereally/saveOneEntity_meMereally_curToken.json")
    fun saveOneEntity(
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String,
            @Field("mereallyStep1Fileurl") mereallyStep1Fileurl: String,
            @Field("mereallyStep2Fileurl") mereallyStep2Fileurl: String?
    ): Flowable<Result<RealAuthenticationBean>>

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
            @Field("tokenUserKey") tokenUserKey: String?,
            @Field("tokenSystreeId") tokenSystreeId: String?
    ): Flowable<Result<RealNameAuthenticationBean>>

    //凭证业务权限_凭证是否能做某事【不包含入参判断】
    @FormUrlEncoded
    @POST("sysAllBus/busflow/token_busCheck.json")
    fun busCheck(
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String,
            @Field("busflowTag") busflowTag: String
    ): Flowable<Result<Any>>

    //会员注册_验证绑定的登录信息是否已经存在
    @FormUrlEncoded
    @POST("manaTokenAllBus/manaTokenUser_isExist.json")
    fun checkAccount(
            @Field("userPhone") userPhone: String?,
            @Field("userEmail") userEmail: String?
    ): Flowable<Result<Any>>

    //获取识别码，充值前调用
    @FormUrlEncoded
    @POST("sysAllBus/sysSeq/getSeq_withSeqTypeEnumCode_sim5.json")
    fun getLegalCode(
            @Field("type") type: String
    ): Flowable<Result<String>>

    //法币充值
    @FormUrlEncoded
    @POST("amtRunBus/amtCheckpay/saveOneDataVo_amtCheckpay_curToken.json")
    fun legalRecharge(
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String,
            @Field("busicheckBusType") busicheckBusType: String,
            @Field("checkpayAmt") checkpayAmt: String,
            @Field("checkpayPlat") checkpayPlat: String?,
            @Field("checkpayDoTime") checkpayDoTime: String?,
            @Field("checkpayAccount") checkpayAccount: String?,
            @Field("checkpayBillno") checkpayBillno: String?,
            @Field("checkpayType") checkpayType: String,
            @Field("checkpayCode") checkpayCode: String
    ): Flowable<Result<LegalRechargeBean>>

    //获取公司收款方式
    @GET("tempRunBus/tempCont/listSimp_cont_byPositionCode_t.json")
    fun getCompanyPaymethod(
            @Query("parentPositionId") parentPositionId: String
    ): Flowable<Result<MutableList<CompanyPaymentBean>>>

    //法币提现
    @FormUrlEncoded
    @POST("amtRunBus/amtPickfund/saveOneDataVo_amtPickfund_curToken.json")
    fun legalWithdraw(
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String,
            @Field("pickfundApplyamt") pickfundApplyamt: String,
            @Field("pickfundToinfo1") pickfundToinfo1: String,
            @Field("pickfundToinfo2") pickfundToinfo2: String,
            @Field("pickfundToinfo3") pickfundToinfo3: String,
            @Field("pickfundMemo") pickfundMemo: String,
            @Field("curBuspwUcode") curBuspwUcode: String,
            @Field("checkcodeId") checkcodeId: String,
            @Field("checkcodeVal") checkcodeVal: String
    ): Flowable<Result<Any>>

    //获取法币提现费率
    @GET("financeRunBus/financeFee/findDataVos_financeFee_parentFeeId_pickFund.json")
    fun getLegalFee(): Flowable<Result<MutableList<LegalFeeBean>>>

    //获取法币充值记录
    @FormUrlEncoded
    @POST("amtRunBus/amtCheckpay/page_amtCheckpay_token_t.json")
    fun getLegalRechargeHistory(
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String,
            @Field("page") page: Int,
            @Field("rows") pageSize: Int
    ): Flowable<Result<MutableList<LegalRechargeHistoryBean>>>


    //获取法币提现记录
    @FormUrlEncoded
    @POST("amtRunBus/amtPickfund/page_amtPickfund_curToken_t.json")
    fun getLegalWithdrawHistory(
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String,
            @Field("page") page: Int,
            @Field("rows") pageSize: Int
    ): Flowable<Result<MutableList<LegalWithdrawHistoryBean>>>


    //资金账户查询_查询账户信息
    @FormUrlEncoded
    @POST("meRunBus/meAcc/findDataVoByBusKey_meAcc_token.json")
    fun getLegalAccountInfo(
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<LegalAccountBean>>


    //数字币转法币价格
    @FormUrlEncoded
    @POST("accOtoBus/otoTransaction/transactionCurreryToRmb.json")
    fun currencyToLegal(
            @Field("currery") currery: String,
            @Field("amt") amt: Double
    ): Flowable<Result<Double>>
}