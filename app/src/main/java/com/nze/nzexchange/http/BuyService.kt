package com.nze.nzexchange.http

import com.nze.nzexchange.bean.*
import com.nze.nzexchange.bean.Result
import io.reactivex.Flowable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface BuyService {


    @FormUrlEncoded
    @POST("otc/buy/getAssets")
    fun getAssets(@Field("userId") userId: String): Flowable<Result<MutableList<OtcAssetBean>>>

    //挂单，商家出售，用户购买
    @FormUrlEncoded
    @POST("otc/buy/pendingOrder")
    fun pendingOrder(@Field("userId") userId: String,
                     @Field("tokenId") tokenId: String,
                     @Field("poolAllCount") poolAllCount: String,
                     @Field("poolPrice") poolPrice: String,
                     @Field("remark") remark: String,
                     @Field("tokenUserId") tokenUserId: String,
                     @Field("tokenUserKey") tokenUserKey: String,
                     @Field("curBuspwUcode") curBuspwUcode: String
    ): Flowable<Result<Boolean>>

    //OTC购买列表
    @FormUrlEncoded
    @POST("otc/buy/findOrderPool")
    fun findOrderPool(@Field("userId") userId: String?,
                      @Field("tokenId") tokenId: String?,
                      @Field("pageNumber") pageNumber: Int,
                      @Field("pageSize") pageSize: Int
    ): Flowable<Result<MutableList<OrderPoolBean>>>

    //吃单
    @FormUrlEncoded
    @POST("otc/buy/placeAnOrder")
    fun placeAnOrder(@Field("poolId") poolId: String,
                     @Field("userIdSell") userIdSell: String,
                     @Field("userIdBu") userIdBu: String,
                     @Field("suborderNum") suborderNum: String,
                     @Field("tokenId") tokenId: String,
                     @Field("tokenUserId") tokenUserId: String,
                     @Field("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<SubOrderInfoBean>>

    //获取商家广告列表
    @FormUrlEncoded
    @POST("otc/buy/findSellList")
    fun findSellList(@Field("userId") userId: String,
                     @Field("pageNumber") pageNumber: Int,
                     @Field("pageSize") pageSize: Int
    ): Flowable<Result<MutableList<FindSellBean>>>

    //商家出售取消
    @FormUrlEncoded
    @POST("otc/buy/cancelOrder")
    fun cancelSaleOrder(@Field("poolId") poolId: String,
                        @Field("userId") userId: String,
                        @Field("tokenUserId") tokenUserId: String,
                        @Field("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<Boolean>>

    //商家购买取消
    @FormUrlEncoded
    @POST("otc/sell/cancelOrder")
    fun cancelBuyOrder(@Field("poolId") poolId: String,
                       @Field("userId") userId: String,
                       @Field("tokenUserId") tokenUserId: String,
                       @Field("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<Boolean>>

    //获取用户子订单列表
    @FormUrlEncoded
    @POST("otc/buy/findSubOrderPool")
    fun findSubOrderPool(
            @Field("userId") userId: String,
            @Field("pageNumber") pageNumber: Int,
            @Field("pageSize") pageSize: Int,
            @Field("suborderStatus") suborderStatus: Int?,
            @Field("tokenUserId") tokenUserId: String,
            @Field("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<MutableList<SubOrderInfoBean>>>

    //获取子订单详情
    @FormUrlEncoded
    @POST("otc/buy/findSubOrderInfo")
    fun findSubOrderInfo(@Field("userId") userId: String,
                         @Field("subOrderId") subOrderId: String,
                         @Field("tokenUserId") tokenUserId: String,
                         @Field("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<SubOrderInfoBean>>


    //用户确认付款
    @FormUrlEncoded
    @POST("otc/buy/confirmReceipt")
    fun confirmReceipt(@Field("userIdBu") userIdBu: String,
                       @Field("suborderId") suborderId: String,
                       @Field("tokenUserId") tokenUserId: String,
                       @Field("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<Boolean>>

    //商家确认收款
    @FormUrlEncoded
    @POST("otc/buy/confirmPayment")
    fun confirmPayment(@Field("userIdSell") userIdSell: String,
                       @Field("suborderId") suborderId: String,
                       @Field("curBuspwUcode") curBuspwUcode: String,
                       @Field("tokenUserId") tokenUserId: String,
                       @Field("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<Boolean>>

    //用户取消子订单
    @FormUrlEncoded
    @POST("otc/buy/userCancelOrder")
    fun userCancelOrder(@Field("userId") userId: String,
                        @Field("subOrderId") subOrderId: String,
                        @Field("tokenUserId") tokenUserId: String,
                        @Field("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<Boolean>>


    //获取计价币种
    @GET("otc/buy/getCurrency")
    fun getCurrency(): Flowable<Result<MutableList<MainCurrencyBean>>>
}