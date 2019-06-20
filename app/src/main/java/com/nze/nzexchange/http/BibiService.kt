package com.nze.nzexchange.http

import com.nze.nzexchange.bean.*
import io.reactivex.Flowable
import retrofit2.http.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/7
 */
interface BibiService {

    //获取计价货币列表
    @GET("market/getAllTransactionPairs")
    fun getAllTransactionPairs(): Flowable<Result<MutableList<TransactionPairsBean>>>

    //通过计价货币获取交易对列表
    @GET("market/getTransactionPairs")
    fun getTransactionPairs(
            @Query("mainCurrency") mainCurrency: String,
            @Query("userId") userId: String?
    ): Flowable<Result<MutableList<TransactionPairsBean>>>


    @GET("market/findTransactionPairs")
    fun findTransactionPairs(@Query("currency") currency: String): Flowable<Result<MutableList<TransactionPairsBean>>>

    //获取自选交易对
    @GET("market/getOptionalTransactionPair")
    fun getOptionalTransactionPair(@Query("userId") userId: String): Flowable<Result<MutableList<TransactionPairsBean>>>

    //添加用户自选交易对
    @FormUrlEncoded
    @POST("market/addOptional")
    fun addOptional(@Field("currencyId") currencyId: String,
                    @Field("userId") userId: String
    ): Flowable<Result<String>>

    //删除用户自选交易对
    @FormUrlEncoded
    @POST("market/deleteOptional")
    fun deleteOptional(@Field("currencyId") currencyId: String,
                       @Field("userId") userId: String
    ): Flowable<Result<String>>

    //获取挂单信息
    @GET("cc/getPendingOrderInfo")
    fun getPendingOrderInfo(
            @Query("currencyId") currencyId: String,
            @Query("userId") userId: String?
    ): Flowable<Result<RestOrderBean>>

    //提交限价交易
    @FormUrlEncoded
    @POST("cc/limitTransaction")
    fun limitTransaction(
            @Field("transactionType") transactionType: Int,
            @Field("userId") userId: String,
            @Field("currencyId") currencyId: String,
            @Field("number") number: Double,
            @Field("price") price: Double,
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String,
            @Field("curBuspwUcode") curBuspwUcode: String
    ): Flowable<Result<LimitTransactionBean>>


    //提交市价交易
    @FormUrlEncoded
    @POST("cc/marketTransaction")
    fun marketTransaction(
            @Field("transactionType") transactionType: Int,
            @Field("userId") userId: String,
            @Field("currencyId") currencyId: String,
            @Field("number") number: Double,
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String,
            @Field("curBuspwUcode") curBuspwUcode: String
    ): Flowable<Result<LimitTransactionBean>>

    //查询未执行订单列表
    @GET("cc/orderPending")
    fun orderPending(
            @Query("currencyId") currencyId: String,
            @Query("userId") userId: String?,
            @Query("pageNumber") pageNumber: Int?,
            @Query("pageSize") pageSize: Int?
    ): Flowable<Result<MutableList<OrderPendBean>>>

    //取消订单
    @FormUrlEncoded
    @POST("cc/cancelOrder")
    fun cancelOrder(
            @Field("orderId") orderId: String,
            @Field("userId") userId: String,
            @Field("currencyId") currencyId: String?,
            @Field("currencyName") currencyName: String?,
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<OrderPendBean>>


    //查询OTC账户资产
    @GET("otc/buy/getAssets")
    fun getUserAssets(
            @Query("userId") userId: String,
            @Query("tokenUserId") tokenUserId: String,
            @Query("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<MutableList<UserAssetBean>>>

    //查询币币账户资产
    @GET("balance/assetInquiry")
    fun assetInquiry(
            @Query("userId") userId: String,
            @Query("tokenId") tokenId: String?,
            @Query("tokenName") tokenName: String?,
            @Query("tokenUserId") tokenUserId: String,
            @Query("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<MutableList<UserAssetBean>>>

    //提币
    @FormUrlEncoded
    @POST("assets/sendTransaction")
    fun sendTransaction(
            @Field("userId") userId: String,
            @Field("token") currency: String,
            @Field("to") to: String,
            @Field("amount") number: Double,
            @Field("password") password: String,
            @Field("remark") remark: String?,
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String,
            @Field("curBuspwUcode") curBuspwUcode: String,
            @Field("checkcodeId") checkcodeId: String,
            @Field("checkcodeVal") checkcodeVal: String
    ): Flowable<Result<Any>>


    //充值记录
    @GET("assets/getTransactionList")
    fun getTransactionList(
            @Query("userId") userId: String,
            @Query("token") token: String,
            @Query("pageNumber") pageNumber: Int,
            @Query("pageSize") pageSize: Int
    ): Flowable<Result<MutableList<TransactionListBean>>>


    @GET("assets/tickRecord")
    fun tickRecord(
            @Query("userId") userId: String,
            @Query("currency") currency: String,
            @Query("pageNumber") pageNumber: Int,
            @Query("pageSize") pageSize: Int
    ): Flowable<Result<MutableList<TransactionListBean>>>


    //订单筛选
    @GET("cc/orderTracking")
    fun orderTracking(
            @Query("currency") currency: String?,
            @Query("mainCurrency") mainCurrency: String?,
            @Query("userId") userId: String?,
            @Query("status") status: Int?,
            @Query("transactionType") transactionType: Int?
    ): Flowable<Result<MutableList<OrderPendBean>>>

    //首页热门交易对
    @GET("market/marketPopular")
    fun marketPopular(
            @Query("userId") userId: String?
    ): Flowable<Result<MutableList<TransactionPairsBean>>>

    //添加用户提币地址
    @FormUrlEncoded
    @POST("addCoinAddress")
    fun addCurrencyWithdrawAddress(
            @Field("userId") userId: String,
            @Field("address") address: String,
            @Field("tokenName") tokenName: String,
            @Field("title") title: String,
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<Any>>

    //删除用户提币地址
    @FormUrlEncoded
    @POST("deleteCoinAddress")
    fun deleteCurrencyWithdrawAddress(
            @Field("userId") userId: String,
            @Field("address") address: String,
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<Any>>


    //提币地址列表
    @FormUrlEncoded
    @POST("getCoinAddress")
    fun getCurrencyWithdrawAddress(
            @Field("userId") userId: String,
            @Field("token") token: String
    ): Flowable<Result<MutableList<CurrenyWithdrawAddressBean>>>

    //财务记录
    @GET("assets/getFinancialRecord")
    fun getFinancialRecord(
            @Query("userId") userId: String,
            @Query("token") token: String,
            @Query("from") from: String,
            @Query("pageNumber") pageNumber: Int?,
            @Query("pageSize") pageSize: Int?
    ): Flowable<Result<MutableList<FinancialRecordBean>>>


    //首页涨幅榜
    @GET("market/risingList")
    fun getRisingList(@Query("userId") userId: String?): Flowable<Result<MutableList<TransactionPairsBean>>>

    //获取充提币信息
    @FormUrlEncoded
    @POST("otc/token/getTokenDetails")
    fun getCurrencyWithdrawInfo(
            @Field("tokenName") tokenName: String
    ): Flowable<Result<CurrencyWithdrawInfoBean>>
}