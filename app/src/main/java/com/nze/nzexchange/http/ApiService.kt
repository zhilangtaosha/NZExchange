package com.nze.nzexchange.http

import com.nze.nzexchange.bean.Accmoney
import com.nze.nzexchange.bean.AssetBean
import com.nze.nzexchange.bean.OrderPoolBean
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Observer
import org.intellij.lang.annotations.Flow
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
    //poolId=Order1542784401045&userIdSell=007&userIdBu=008&suborderAmount=1&suborderPrice=2.6
    @FormUrlEncoded
    @POST("otc/buy/placeAnOrder")
    fun placeAnOrder(@Field("poolId") poolId: String,
                     @Field("userIdSell") userIdSell: String,
                     @Field("userIdBu") userIdBu: String,
                     @Field("suborderAmount") suborderAmount: String,
                     @Field("suborderPrice") suborderPrice: String
    ): Flowable<Result<Accmoney>>
}