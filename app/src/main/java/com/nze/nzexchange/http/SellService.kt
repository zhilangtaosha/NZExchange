package com.nze.nzexchange.http

import com.nze.nzexchange.bean.OrderPoolBean
import com.nze.nzexchange.bean.SubOrderInfoBean
import com.nze.nzexchange.bean.Result
import com.nze.nzexchange.bean.TokenInfoBean
import io.reactivex.Flowable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/11/24
 */
interface SellService {

    //挂单，商家出售，用户购买
    @FormUrlEncoded
    @POST("otc/sell/pendingOrder")
    fun pendingOrder(@Field("userId") userId: String,
                     @Field("tokenId") tokenId: String,
                     @Field("poolAllCount") poolAllCount: String,
                     @Field("poolPrice") poolPrice: String,
                     @Field("remark") remark: String,
                     @Field("tokenUserId") tokenUserId: String,
                     @Field("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<Boolean>>


    //吃单
    @FormUrlEncoded
    @POST("otc/sell/placeAnOrder")
    fun placeAnOrder(@Field("poolId") poolId: String,
                     @Field("userIdSell") userIdSell: String,
                     @Field("userIdBu") userIdBu: String,
                     @Field("suborderNum") suborderNum: String,
                     @Field("tokenId") tokenId: String,
                     @Field("tokenUserId") tokenUserId: String,
                     @Field("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<SubOrderInfoBean>>

    //出售列表
    @FormUrlEncoded
    @POST("otc/sell/findOrderPool")
    fun findOrderPool(
            @Field("userId") userId: String?,
            @Field("tokenId") tokenId: String?,
            @Field("pageNumber") pageNumber: Int,
            @Field("pageSize") pageSize: Int
    ): Flowable<Result<MutableList<OrderPoolBean>>>


    //商家确认付款
    @FormUrlEncoded
    @POST("otc/sell/confirmReceipt")
    fun confirmReceipt(@Field("userIdSell") userIdSell: String,
                       @Field("suborderId") suborderId: String,
                       @Field("tokenUserId") tokenUserId: String,
                       @Field("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<Boolean>>


    //用户确认收款
    @FormUrlEncoded
    @POST("otc/sell/confirmPayment")
    fun confirmPayment(@Field("userIdBu") userIdSell: String,
                       @Field("suborderId") suborderId: String,
                       @Field("tokenUserId") tokenUserId: String,
                       @Field("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<Boolean>>


    //商家取消子订单
    @FormUrlEncoded
    @POST("otc/sell/cancelOrder")
    fun userCancelOrder(@Field("userId") userId: String,
                        @Field("subOrderId") subOrderId: String,
                        @Field("tokenUserId") tokenUserId: String,
                        @Field("tokenUserKey") tokenUserKey: String
    ): Flowable<Result<Boolean>>

    //k线页面数字货币信息
    @FormUrlEncoded
    @POST("otc/token/getTokenInfo")
    fun getTokenInfo(
            @Field("tokenName") tokenName: String
    ): Flowable<Result<TokenInfoBean>>

}