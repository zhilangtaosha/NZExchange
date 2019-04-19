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
            @Field("price") price: Double
    ): Flowable<Result<LimitTransactionBean>>


    //提交市价交易
    @FormUrlEncoded
    @POST("cc/marketTransaction")
    fun marketTransaction(
            @Field("transactionType") transactionType: Int,
            @Field("userId") userId: String,
            @Field("currencyId") currencyId: String,
            @Field("number") number: Double
    ): Flowable<Result<LimitTransactionBean>>

    //查询未执行订单列表
    @GET("cc/orderPending")
    fun orderPending(
            @Query("currencyId") currencyId: String,
            @Query("userId") userId: String?
    ): Flowable<Result<MutableList<OrderPendBean>>>

    //取消订单
    @FormUrlEncoded
    @POST("cc/cancelOrder")
    fun cancelOrder(
            @Field("orderId") orderId: String,
            @Field("userId") userId: String,
            @Field("currencyId") currencyId: String
    ): Flowable<Result<OrderPendBean>>


    //查询OTC账户资产
    @GET("otc/buy/getAssets")
    fun getUserAssets(@Query("userId") userId: String): Flowable<Result<MutableList<UserAssetBean>>>

    //查询币币账户资产
    @GET("balance/assetInquiry")
    fun assetInquiry(
            @Query("userId") userId: String
    ): Flowable<Result<MutableList<UserAssetBean>>>

    //提币
    @FormUrlEncoded
    @POST("assets/sendTransaction")
    fun sendTransaction(
            @Field("userId") userId: String,
            @Field("currency") currency: String,
            @Field("to") to: String,
            @Field("number") number: String,
            @Field("password") password: String,
            @Field("remark") remark: String?
    ): Flowable<Result<String>>


    //充值记录
    @GET("assets/getTransactionList")
    fun getTransactionList(
            @Query("userId") userId: String,
            @Query("currency") currency: String,
            @Query("pageNumber") pageNumber: Int,
            @Query("pageSize") pageSize: Int
    ): Flowable<Result<MutableList<TransactionListBean>>>


    @GET("assets/tickRecord")
    fun tickRecord(
            @Query("userId") userId: String,
            @Query("currency") currency: String,
            @Query("pageNumber") pageNumber: Int,
            @Query("pageSize") pageSize: Int
    ): Flowable<Result<MutableList<TickRecordBean>>>


    //订单筛选
    @GET("cc/orderTracking")
    fun orderTracking(
            @Query("currency") currency: String?,
            @Query("mainCurrency") mainCurrency: String?,
            @Query("userId") userId: String?,
            @Query("status") status: Int?,
            @Query("transactionType") transactionType: Int?
    ): Flowable<Result<MutableList<OrderPendBean>>>

    
}