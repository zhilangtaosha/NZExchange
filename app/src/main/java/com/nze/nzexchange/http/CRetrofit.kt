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
    val SERVER_URL = "http://zhongyingying.qicp.io:8082/"


    var url: String = if (BuildConfig.DEBUG) {
        LIU_URL
    } else {
        SERVER_URL
    }
    var retrofit: Retrofit
    val builder by lazy {
        OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(RetryIntercepter(3))
                .addInterceptor(EncodeIntercepter())
                .apply {
                    val interceptor = HttpLoggingInterceptor()
                    interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                    addInterceptor(interceptor)
                }
    }

    val upBuilder by lazy {
        OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(RetryIntercepter(3))
                .apply {
                    val interceptor = HttpLoggingInterceptor()
                    interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                    addInterceptor(interceptor)
                }
    }

    init {

        retrofit = Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url)
                .build()

    }


    fun <T> create(service: Class<T>): T = retrofit.create(service)

    fun userService(): UserService = create(UserService::class.java)

    fun uploadService(): UserService {
        return Retrofit.Builder()
                .client(upBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(SERVER_FILE)
                .build()
                .create(UserService::class.java)
    }

    companion object {
        val instance: CRetrofit by lazy { CRetrofit() }
        //文件服务器地址
        val SERVER_FILE = "http://zhongyingying.qicp.io:8083"

        fun imageUrlJoin(url: String): String {
            return "$SERVER_FILE$url"
        }
    }

}