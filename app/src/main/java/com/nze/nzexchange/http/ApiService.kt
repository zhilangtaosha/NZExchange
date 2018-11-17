package com.nze.nzexchange.http

import io.reactivex.Observable
import io.reactivex.Observer
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("otc/buy/pendingOrder")
    fun pendingOrder(@Field("tokenId") tokenId:String): Observable<Result<String>>
}