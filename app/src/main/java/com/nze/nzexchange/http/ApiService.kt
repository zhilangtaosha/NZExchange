package com.nze.nzexchange.http

import com.nze.nzexchange.bean.AssetBean
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Observer
import org.intellij.lang.annotations.Flow
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("otc/buy/pendingOrder")
    fun pendingOrder(@Field("tokenId") tokenId:String): Observable<Result<String>>

    @FormUrlEncoded
    @POST("otc/buy/getAssets")
    fun getAssets(@Field("userId") userId:String): Flowable<Result<MutableList<AssetBean>>>
}