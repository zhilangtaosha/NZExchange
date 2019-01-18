package com.nze.nzexchange.http

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

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
    val client = OkHttpClient()

    fun open(): WebSocket {
        return client.newWebSocket(request, listener)
    }

    fun close() {
        client.dispatcher().executorService().shutdown()
    }


    companion object {
        fun newInstance(url: String, listener: WebSocketListener): NWebSocket {
            return NWebSocket(url, listener)
        }
    }
}