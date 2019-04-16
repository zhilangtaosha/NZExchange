package com.nze.nzexchange.controller.bibi

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.google.gson.Gson
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.bean.*
import com.nze.nzexchange.bean2.ShenDubean
import com.nze.nzexchange.config.KLineParam
import com.nze.nzexchange.config.KLineRequestBean
import com.nze.nzexchange.http.NWebSocket
import com.nze.nzexchange.tools.TimeTool
import com.nze.nzexchange.widget.chart.DataHelper
import com.nze.nzexchange.widget.chart.KLineEntity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

/**
 * https://blog.csdn.net/sun20209527/article/details/78653286
 * https://www.cnblogs.com/wicrecend/p/5288527.html
 */
class KLineService : Service() {

    val binder: KBinder = KBinder()
    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    class KBinder : Binder() {
        val KLINE_TYPE_NOWLINEK = "nowLineK"
        val KLINE_TYPE_OLDLINEK = "oldLineK"
        val KLINE_TYPE_NEWLINEK = "newLineK"
        private val buyList: MutableList<ShenDubean> by lazy { mutableListOf<ShenDubean>() }
        private val sellList: MutableList<ShenDubean> by lazy { mutableListOf<ShenDubean>() }
        private val newDealList: MutableList<NewDealBean> by lazy { mutableListOf<NewDealBean>() }
        var pattern: String = TimeTool.PATTERN5
        val chartData: MutableList<KLineEntity> by lazy { mutableListOf<KLineEntity>() }
        var nWebSocket: NWebSocket? = null
        var socket: WebSocket? = null
        val gson: Gson by lazy { Gson() }
        var socketRequestId: String = ""
            get() {
                return System.currentTimeMillis().toString()
            }

        var kDataCallBack: ((lineK: LineKBean?, handicap: Handicap?, latestDeal: List<NewDealBean>?, quotes: Array<String>?, depth: Depth?) -> Unit)? = null
        fun initKSocket(market: String, kDataCallBack: ((lineK: LineKBean?, handicap: Handicap?, latestDeal: List<NewDealBean>?, quotes: Array<String>?, depth: Depth?) -> Unit)) {
            this.kDataCallBack = kDataCallBack
            socket?.cancel()
            nWebSocket = NWebSocket.newInstance("${NWebSocket.K_URL}/$market", wsListener)
            socket = nWebSocket?.open()

        }

        /**
         * 获取交易数据
         * 初始参数是1分钟
         */
        fun getKDataRequest(pairsBean: TransactionPairsBean) {
            val requestBean: KLineRequestBean = KLineRequestBean(KLineParam.METHOD_GET_K, mutableListOf<String>())
            requestBean.params.add("${pairsBean?.currency?.toUpperCase()}${pairsBean?.mainCurrency?.toUpperCase()}")
            requestBean.params.add(KLineParam.TIME_ONE_MIN)
            requestBean.params.add(socketRequestId)
            val param: String = gson.toJson(requestBean, KLineRequestBean::class.java)
            NLog.i("socketRequestId>>>$socketRequestId")
            NLog.i("getKDataRequest>>$param")
            socket?.send(param)
        }

        /**
         * 切换交易对
         */
        fun changePair(pairsBean: TransactionPairsBean){
            val requestBean: KLineRequestBean = KLineRequestBean(KLineParam.METHOD_CHANGE_PAIR, mutableListOf<String>())
            requestBean.params.add("${pairsBean?.currency?.toUpperCase()}${pairsBean?.mainCurrency?.toUpperCase()}")
            requestBean.params.add(KLineParam.TIME_ONE_MIN)
            val param: String = gson.toJson(requestBean, KLineRequestBean::class.java)
            NLog.i("changePair>>$param")
            socket?.send(param)
        }

        private val wsListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                NLog.i("onOpen")
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
            var handicap: Handicap? = null
            override fun onMessage(webSocket: WebSocket, text: String) {
                NLog.i("text>>>$text")
                Observable.create<Int> {
                    var soketbean: Soketbean = gson.fromJson(text, Soketbean::class.java)
                    val lineK = soketbean.lineK
                    val handicap = soketbean.handicap
                    val latestDeal = soketbean.latestDeal
                    val quotes = soketbean.quotes
                    val depth = soketbean.depth
                    if (lineK != null) {//K线数据
                        var kList = mutableListOf<KLineEntity>()
                        lineK.result?.let {
                            it.forEachIndexed { index, it ->
                                val bean = KLineEntity()
//                            bean.Date = TimeTool.format(pattern, it[0].toLong() * 1000)
                                bean.Date = it[0]
                                bean.Open = it[1].toFloat()
                                bean.Close = it[2].toFloat()
                                bean.High = it[3].toFloat()
                                bean.Low = it[4].toFloat()
                                bean.Volume = it[5].toFloat()
                                if (bean.High < bean.Open || bean.High < bean.Close || bean.High < bean.Low) {
                                    NLog.i("最高值出错>>>index=$index")
                                }
                                if (bean.Low > bean.Open || bean.Low > bean.Close || bean.Low > bean.High) {
                                    NLog.i("最小值出错>>>index=$index")
                                }
                                kList.add(bean)
                            }

                            kList.forEach {
                                it.Date = TimeTool.format(pattern, it.Date.toLong() * 1000)
                            }
                        }
                        when (lineK.type) {
                            KLINE_TYPE_NOWLINEK -> {
                                chartData.addAll(0, kList)
                                it.onNext(DATA_NOW_LINEK)
                            }
                            KLINE_TYPE_NEWLINEK -> {

                                chartData.addAll(kList)
                                it.onNext(DATA_NEW_LINEK)
                            }
                            KLINE_TYPE_OLDLINEK -> {
                                chartData.addAll(0, kList)
                                it.onNext(DATA_OLD_LINEK)
                            }
                        }
                        DataHelper.calculate(chartData)
                    }

                    if (handicap != null) {//盘口
                        this.handicap = handicap
                        it.onNext(DATA_HANDICAP)
                    }

                    if (latestDeal != null) {//最新成交
                        newDealList.clear()
                        newDealList.addAll(latestDeal)
                        it.onNext(DATE_NEW_DEAL)
                    }
                }.subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            when (it) {
                                DATA_NOW_LINEK -> {//k线数据

                                }
                                DATA_NEW_LINEK -> {

                                }
                                DATA_OLD_LINEK -> {

                                }
                                DATA_HANDICAP -> {//盘口数据
                                    kDataCallBack?.invoke(null, handicap, null, null, null)
                                }
                                DATE_NEW_DEAL -> {//最新成交数据

                                }
                            }
                        }

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

        fun close(){
            nWebSocket?.close()
            socket?.cancel()
        }
    }
}
