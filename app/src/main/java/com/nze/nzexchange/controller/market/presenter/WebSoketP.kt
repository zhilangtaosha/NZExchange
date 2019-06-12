package com.nze.nzexchange.controller.market.presenter

import com.google.gson.Gson
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.ui.BaseActivityP
import com.nze.nzexchange.bean.*
import com.nze.nzexchange.config.KLineParam
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.http.NWebSocket
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * 订阅今日行情
 * {"method": "today.update", "params": ["BTCUSDT", {"open": "6100.45", "last": "2420", "high": "6100.45", "low": "2420", "volume": "600.140625", "deal": "2795807.675"}], "id": null}
 * 订阅最近成交列表
 * {"method": "deals.update", "params": ["BTCUSDT", [{"id": 91, "time": 1560339406.2907579, "price": "2420", "amount": "0.2", "type": "sell"}, {"id": 90, "time": 1560339406.2903261, "price": "2680", "amount": "4.8", "type": "sell"}]], "id": null}
 * 订阅深度
 * {"method": "depth.update", "params": [true, {"asks": [], "bids": [["2420", "3.38"], ["2320", "2.8"]]}, "BTCUSDT"], "id": null}
 *订阅1分钟 K线
 * {"method": "kline.update", "params": [[1560214860, "8006.01234567", "8006.01234567", "8006.01234567", "8006.01234567", "0", "0", "BTCUSDT"]], "id": null}
 * @创建时间：2019/6/11
 */
class WebSoketP(act: NBaseActivity) : BaseActivityP(act) {

    var nWebSocket: NWebSocket? = null
    var socket: WebSocket? = null
    val gson: Gson by lazy { Gson() }
    lateinit var mPair: String

    fun initSocket(pair: String) {
        this.mPair = pair
        nWebSocket = NWebSocket.newInstance(KLineParam.MARKET_MYSELF, wsListener)
        socket = nWebSocket?.open()
    }

    fun queryPrice() {//查询当前价格
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_QUERY_PRICE)
        requestBean.params.add(mPair)
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("queryPrice>>$param")
        socket?.send(param)
    }

    fun subscribePrice() {//订阅当前价格
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_SUBSCRIBE_PRICE)
        requestBean.params.add(mPair)
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("subscribePrice>>$param")
        socket?.send(param)
    }

    fun subscribeState() {//订阅市场行情
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_SUBSCRIBE_STATE)
        requestBean.params.add(mPair)
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("subscribeState>>$param")
        socket?.send(param)
    }

    fun subscribeToday() {//订阅今日行情
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_SUBSCRIBE_TODAY)
        requestBean.params.add(mPair)
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("subscribeToday>>$param")
        socket?.send(param)
    }

    fun subscribeDeals() {//订阅最近成交列表
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_SUBSCRIBE_DEALS)
        requestBean.params.add(mPair)
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("subscribeDeals>>$param")
        socket?.send(param)
    }

    fun subscribeDepth(amount: Int, depth: String) {//订阅深度
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_SUBSCRIBE_DEPTH)
        requestBean.params.add(mPair)
        requestBean.params.add(amount)
        requestBean.params.add(depth)
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("subscribeDepth>>$param")
        socket?.send(param)
    }


    fun queryKline(amount: Int, type: Int) {//查询K线
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_QUERY_KLINE, KLineParam.ID_KLINE)
        requestBean.params.add(mPair)
        requestBean.params.add(amount)
        requestBean.params.add(type)
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("queryKline>>$param")
        socket?.send(param)
    }

    fun subscribeKline(type: Int) {//查询K线
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_SUBSCRIBE_KLINE)
        requestBean.params.add(mPair)
        requestBean.params.add(type)
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("subscribeKline>>$param")
        socket?.send(param)
    }

    private val wsListener = object : WebSocketListener() {
        val gson: Gson = Gson()
        override fun onOpen(webSocket: WebSocket, response: Response) {
            NLog.i("nonOpen")
            subscribeToday()
            subscribeDeals()
            subscribeDepth(10, KLineParam.DEPTH_8)
            queryKline(100, 60)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            NLog.i("nonFailure")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            NLog.i("nonClosing")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            NLog.i("ntext>>>$text")
            Observable.create<Int> {
                try {
                    if (text.contains("error")) {//查询或者订阅成功返回
                        val queryBean: SoketQueryBean = gson.fromJson(text, SoketQueryBean::class.java)
                        if (queryBean.id == KLineParam.ID_KLINE) {//查询根据id确认
                            val l = gson.fromJson<Array<Array<String>>>(queryBean.result.toString(), Array<Array<String>>::class.java)
                            NLog.i("request>>>${queryBean.result}")
                        }
                    } else {//订阅
                        val subscribeBean: SoketSubscribeBean = gson.fromJson(text, SoketSubscribeBean::class.java)
                        when (subscribeBean.method) {
                            KLineParam.SUBSCRIBE_KLINE -> {//k线
                                it.onNext(KLineParam.DATA_KLINE_SUBSCRIBE)
                            }
                            KLineParam.SUBSCRIBE_TODAY -> {//今日行情
                                val todayBean: SoketTodayBean = gson.fromJson<SoketTodayBean>(subscribeBean.params[1].toString(), SoketTodayBean::class.java)
                                it.onNext(KLineParam.DATA_TODAY_SUBSCRIBE)
                            }
                            KLineParam.SUBSCRIBE_DEPTH -> {//深度
                                val depthBean: SoketDepthBean = gson.fromJson<SoketDepthBean>(subscribeBean.params[1].toString(), SoketDepthBean::class.java)
                                it.onNext(KLineParam.DATA_DEPTH_SUBSCRIBE)
                            }
                            KLineParam.SUBSCRIBE_DEALS -> {//最近成交列表
                                it.onNext(KLineParam.DATA_DEALS_SUBSCRIBE)
                            }
                        }

                    }
                } catch (e: Exception) {
                    it.onError(e)
                }
            }.subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({

                    }, {
                        NLog.i("出错了 。。。")
                    })


            super.onMessage(webSocket, text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            NLog.i("nbytes>>>$bytes")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            NLog.i("nonClosed")
        }
    }

}
