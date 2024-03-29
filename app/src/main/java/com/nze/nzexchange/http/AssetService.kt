package com.nze.nzexchange.http

import com.nze.nzexchange.bean.BibiAssetBean
import com.nze.nzexchange.bean.Result
import com.nze.nzexchange.bean.TransferRecordBean
import io.reactivex.Flowable
import retrofit2.http.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.POST


/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明: 资产类接口
 * @创建时间：2018/12/20
 */
interface AssetService {
    //资产查询
    @GET("zyy-otc/assets/getUserAssets")
    fun getUserAssets(@Query("userId") userId: String): Flowable<Result<MutableList<BibiAssetBean>>>

    //提币请求
    @FormUrlEncoded
    @POST("zyy-otc/assets/sendTransaction")
    fun sendTransaction(
            @Field("userId") userId: String,
            @Field("currency") currency: String,
            @Field("to") to: String,
            @Field("number") number: String,
            @Field("password") password: String,
            @Field("remark") remark: String
    ): Flowable<Result<String>>

    //    @Headers("Content-Type: application/json;Accept: application/json")
    @POST("/")
    fun zhangNet(@Body info: RequestBody): Flowable<Any>

    //资产划转
    @FormUrlEncoded
    @POST("transferAssets")
    fun transferAssets(
            @Field("userId") userId: String,
            @Field("token") token: String,
            @Field("from") from: String,
            @Field("to") to: String,
            @Field("amount") amount: Double,
            @Field("remark") remark: String?,
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<Any>>

    //资产划转记录筛选
    @GET("screeningTransferRecord")
    fun transferRecord(
            @Query("userId") userId: String,
            @Query("from") from: String?,
            @Query("to") to: String?,
            @Query("token") token: String?,
            @Query("pageNumber") pageNumber: Int,
            @Query("pageSize") pageSize: Int
    ): Flowable<Result<MutableList<TransferRecordBean>>>
}