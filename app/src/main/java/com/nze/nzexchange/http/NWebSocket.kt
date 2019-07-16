package com.nze.nzexchange.http

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/1/17
 */
class NWebSocket(var url: String, var listener: WebSocketListener) {
    private lateinit var okClient: OkHttpClient

    init {
        val xtm: X509TrustManager = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                val x509Certificates = arrayOf<X509Certificate>()
                return x509Certificates
            }

        }
        var sslContext: SSLContext? = null
        sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, arrayOf<TrustManager>(xtm), SecureRandom())

        val noVerify = object : HostnameVerifier {
            override fun verify(hostname: String?, session: SSLSession?): Boolean {
                return true
            }

        }
        okClient = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .sslSocketFactory(sslContext.socketFactory)
                .hostnameVerifier(noVerify)
                .build()

    }


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
        return okClient.newWebSocket(request, listener)
    }

    fun close() {
        okClient.dispatcher().executorService().shutdown()
    }


    companion object {

        fun newInstance(url: String, listener: WebSocketListener): NWebSocket {
            return NWebSocket(url, listener)
        }
    }
}

