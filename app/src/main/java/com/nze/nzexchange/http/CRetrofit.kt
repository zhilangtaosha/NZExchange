package com.nze.nzexchange.http

import com.nze.nzexchange.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class CRetrofit private constructor() {
    //http://zhongyingying.qicp.io:18080
    val LIU_URL = "http://192.168.1.143:8082/"
    val SERVER_URL = "http://zhongyingying.qicp.io:18080/zyy-otc/"

    var url: String = LIU_URL
    var retrofit: Retrofit

    init {
        val builder = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(RetryIntercepter(3))
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


    companion object {
        val instance: CRetrofit by lazy { CRetrofit() }
    }
}