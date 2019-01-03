package com.nze.nzexchange.http

import com.nze.nzexchange.bean.LimitTransactionBean
import com.nze.nzexchange.bean.RestOrderBean
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.bean.Result
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
    fun getTransactionPairs(@Query("mainCurrency") mainCurrency: String, @Query("userId") userId: String): Flowable<Result<MutableList<TransactionPairsBean>>>


    @GET("market/findTransactionPairs")
    fun findTransactionPairs(@Query("currency") currency: String): Flowable<Result<MutableList<TransactionPairsBean>>>

    //获取自选交易对
    @GET("market/getOptionalTransactionPair")
    fun getOptionalTransactionPair(@Query("userId") userId: String): Flowable<Result<MutableList<TransactionPairsBean>>>

    //添加用户自选交易对
    @FormUrlEncoded
    @POST("market/addOptional")
    fun addOptional(@Field("currencyId") currencyId: Int,
                    @Field("userId") userId: String
    ): Flowable<Result<String>>

    //删除用户自选交易对
    @FormUrlEncoded
    @POST("market/deleteOptional")
    fun deleteOptional(@Field("currencyId") currencyId: Int,
                       @Field("userId") userId: String
    ): Flowable<Result<String>>

    //获取挂单信息
    @GET("cc/getPendingOrderInfo")
    fun getPendingOrderInfo(
            @Query("currencyId") currencyId: Int,
            @Query("userId") userId: String?
    ): Flowable<Result<RestOrderBean>>

    //提交限价交易
    @FormUrlEncoded
    @POST("cc/limitTransaction")
    fun limitTransaction(
            @Field("transactionType") transactionType: Int,
            @Field("userId") userId: String,
            @Field("currencyId") currencyId: Int,
            @Field("number") number: Double,
            @Field("price") price: Double
    ): Flowable<Result<LimitTransactionBean>>


    //提交市价交易
    @FormUrlEncoded
    @POST("cc/marketTransaction")
    fun marketTransaction(
            @Field("transactionType") transactionType: Int,
            @Field("userId") userId: String,
            @Field("currencyId") currencyId: Int,
            @Field("number") number: Double
    ): Flowable<Result<HashMap<String, Any>>>
}