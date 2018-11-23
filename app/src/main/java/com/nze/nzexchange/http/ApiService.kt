package com.nze.nzexchange.http

import com.nze.nzexchange.bean.*
import io.reactivex.Flowable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {


    @FormUrlEncoded
    @POST("otc/buy/getAssets")
    fun getAssets(@Field("userId") userId: String): Flowable<Result<MutableList<AssetBean>>>

    //商家出售，用户购买
    @FormUrlEncoded
    @POST("otc/buy/pendingOrder")
    fun pendingOrder(@Field("userId") userId: String,
                     @Field("tokenId") tokenId: String,
                     @Field("poolAllCount") poolAllCount: String,
                     @Field("poolPrice") poolPrice: String,
                     @Field("remark") remark: String
    ): Flowable<Result<Boolean>>

    //OTC购买列表
    @FormUrlEncoded
    @POST("otc/buy/findOrderPool")
    fun findOrderPool(@Field("tokenId") tokenId: String): Flowable<Result<MutableList<OrderPoolBean>>>

    //OTC买入
    @FormUrlEncoded
    @POST("otc/buy/placeAnOrder")
    fun placeAnOrder(@Field("poolId") poolId: String,
                     @Field("userIdSell") userIdSell: String,
                     @Field("userIdBu") userIdBu: String,
                     @Field("suborderNum") suborderNum: String,
                     @Field("tokenId") tokenId: String
    ): Flowable<Result<SubOrderInfoBean>>

    //获取商家广告列表
    @FormUrlEncoded
    @POST("otc/buy/findSellList")
    fun findSellList(@Field("userId") userId: String): Flowable<Result<MutableList<FindSellBean>>>

    //.商家取消订单
    @FormUrlEncoded
    @POST("otc/buy/cancelOrder")
    fun cancelOrder(@Field("poolId") poolId: String,
                    @Field("userId") userId: String
    ): Flowable<Result<Boolean>>

    //获取用户子订单列表
    @FormUrlEncoded
    @POST("otc/buy/findSubOrderPool")
    fun findSubOrderPool(
            @Field("userId") userId: String,
            @Field("pageNumber") pageNumber: Int,
            @Field("pageSize") pageSize: Int,
            @Field("suborderStatus") suborderStatus: Int
    ): Flowable<Result<MutableList<SubOrderInfoBean>>>
}