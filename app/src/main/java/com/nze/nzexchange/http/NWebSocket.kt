package com.nze.nzexchange.http

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/1/17
 */
class NWebSocket(var url: String, var listener: WebSocketListener) {

    val request = Request.Builder()
            .url(url)
            .build()
    val client = OkHttpClient.Builder().run {
        connectTimeout(20, TimeUnit.SECONDS)
        writeTimeout(20, TimeUnit.SECONDS)
        readTimeout(20, TimeUnit.SECONDS)
        build()
    }

    fun open(): WebSocket {
        return client.newWebSocket(request, listener)
    }

    fun close() {
        client.dispatcher().executorService().shutdown()
    }


    companion object {

        const val K_URL = "ws://192.168.1.101:800"
        fun newInstance(url: String, listener: WebSocketListener): NWebSocket {
            return NWebSocket(url, listener)
        }
    }
}

