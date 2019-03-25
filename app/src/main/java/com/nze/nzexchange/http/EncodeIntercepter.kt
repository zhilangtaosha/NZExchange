package com.nze.nzexchange.http

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.http.POST
import java.io.IOException
import java.net.URLDecoder


/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/3/21
 */
class EncodeIntercepter: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        //El problema es que codifica los parametros del body y no los queremos codificados
        val postBody = bodyToString(original.body())
        val newPostBody = URLDecoder.decode(postBody,"utf-8")
        val body = original.body()
        var requestBody: RequestBody? = null

        if (body != null) {
            requestBody = RequestBody.create(original.body()!!.contentType(), newPostBody)
        }

        // Aqui el problema es que se tiene que modificar el body en los put y los post para decodificar,
        // pero los get y delete no tienen body
        val request: Request
        if (original.method().equals("POST")) {
            request = original.newBuilder()
                    .method(original.method(), original.body())
                    .post(requestBody)
                    .build()
        } else if (original.method().equals("PUT")) {
            request = original.newBuilder()
                    .method(original.method(), original.body())
                    .put(requestBody)
                    .build()
        } else {
            request = original.newBuilder()
                    .method(original.method(), original.body())
                    .build()
        }


        return chain.proceed(request)
    }

    fun bodyToString(request: RequestBody?): String {
        try {
            val buffer = Buffer()
            if (request != null)
                request.writeTo(buffer)
            else
                return ""
            return buffer.readUtf8()
        } catch (e: IOException) {
            return "did not work"
        }

    }
}