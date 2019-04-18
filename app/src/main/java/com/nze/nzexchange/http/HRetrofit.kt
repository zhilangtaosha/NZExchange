package com.nze.nzexchange.http

import com.nze.nzexchange.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:800端口的连接
 * @创建时间：2019/4/18
 */
class HRetrofit {
    val HUO_URL = "http://192.168.1.101:800/"
    var url: String = HUO_URL
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

    companion object {
        val instance: HRetrofit = HRetrofit()
    }

    fun huoService(): HuoService = create(HuoService::class.java)
}