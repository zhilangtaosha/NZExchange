package com.nze.nzexchange.http

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class RetryIntercepter(private val maxRetry: Int) : Interceptor {
    private var retryNum: Int = 0

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)
        while (!response.isSuccessful && retryNum < maxRetry) {
            retryNum++
            response = chain.proceed(request)
        }
        return response
    }
}