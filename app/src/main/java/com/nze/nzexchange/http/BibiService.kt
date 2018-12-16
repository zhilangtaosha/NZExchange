package com.nze.nzexchange.http

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
}