package com.nze.nzexchange.http

import com.nze.nzexchange.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NRetrofit private constructor() {
    //http://zhongyingying.qicp.io:18080
    val LIU_URL = "http://192.168.1.101:18080/"
    //    val LIU_URL = "http://192.168.0.113:18080/"
    val SERVER_URL = "http://zhongyingying.qicp.io:18080/"


    var url: String = if (BuildConfig.DEBUG) {
        LIU_URL
    } else {
        SERVER_URL
    }
    var retrofit: Retrofit

    init {
        val builder = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
        //.addInterceptor(RetryIntercepter(3))
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        builder.addInterceptor(interceptor)
        retrofit = Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url)
                .build()

    }


    fun <T> create(service: Class<T>): T = retrofit.create(service)

    fun buyService(): BuyService = create(BuyService::class.java)

    fun sellService(): SellService = create(SellService::class.java)

    fun bibiService(): BibiService = create(BibiService::class.java)

    fun assetService(): AssetService = create(AssetService::class.java)

    companion object {
        val instance: NRetrofit = NRetrofit()
    }
}