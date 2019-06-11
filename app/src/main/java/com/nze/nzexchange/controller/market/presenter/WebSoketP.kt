package com.nze.nzexchange.controller.market.presenter

import android.view.View
import com.google.gson.Gson
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.ui.BaseActivityP
import com.nze.nzexchange.bean.Soketbean
import com.nze.nzexchange.bean2.ShenDubean
import com.nze.nzexchange.config.KLineParam
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.retainInt
import com.nze.nzexchange.http.NWebSocket
import com.nze.nzexchange.tools.TimeTool
import com.nze.nzexchange.widget.chart.DataHelper
import com.nze.nzexchange.widget.chart.KLineEntity
import com.nze.nzexchange.widget.depth.DepthDataBean
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import kotlin.text.Typography.quote

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/6/11
 */
class WebSoketP(act: NBaseActivity) : BaseActivityP(act) {

    var nWebSocket: NWebSocket? = null
    var socket: WebSocket? = null

    fun initSocket() {
//        socket?.cancel()
//        dataType = DATA_TYPE_INIT
        nWebSocket = NWebSocket.newInstance(KLineParam.MARKET_MYSELF, wsListener)
        socket = nWebSocket?.open()
    }

    

    private val wsListener = object : WebSocketListener() {
        val gson: Gson = Gson()
        override fun onOpen(webSocket: WebSocket, response: Response) {

        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            NLog.i("onFailure")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            NLog.i("onClosing")
        }

        val DATA_K = 0
        val DATA_HANDICAP = 1
        val DATE_NEW_DEAL = 2
        val DATA_NOW_LINEK = 3
        val DATA_OLD_LINEK = 4
        val DATA_NEW_LINEK = 5
        val DATA_QUOTES = 6
        val DATA_DEPTH = 7
        val DATA_DATASOURCE = 8
        override fun onMessage(webSocket: WebSocket, text: String) {
            NLog.i("text>>>$text")


            super.onMessage(webSocket, text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            NLog.i("bytes>>>$bytes")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            NLog.i("onClosed")
        }
    }

}