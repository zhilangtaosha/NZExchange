package com.nze.nzexchange.controller.market.presenter

import android.view.View
import com.google.gson.Gson
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.bean.*
import com.nze.nzexchange.bean2.ShenDubean
import com.nze.nzexchange.config.KLineParam
import com.nze.nzexchange.config.KLineRequestBean
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
 * @类 说 明: 曙光定义的格式
 * @创建时间：2019/6/14
 */
class WebSoketImpl2 : IWebSoket {
    override var mOnTodayCallback: ((todayBean: SoketTodayBean) -> Unit)? = null
    override var mOnDepthCallback: ((mDepthBuyList: MutableList<DepthDataBean>, mDepthSellList: MutableList<DepthDataBean>) -> Unit)? = null
    override var mOnDealCallback: ((dealList: MutableList<SoketDealBean>) -> Unit)? = null
    override var mOnQueryKlineCallback: ((kList: MutableList<KLineEntity>) -> Unit)? = null
    override var mOnSubscribeKlineCallback: ((newKList: MutableList<KLineEntity>) -> Unit)? = null

    var nWebSocket: NWebSocket? = null
    var socket: WebSocket? = null
    var socketRequestId: String = ""
        get() {
            return System.currentTimeMillis().toString()
        }
    val gson: Gson by lazy { Gson() }
    val chartData: MutableList<KLineEntity> by lazy { mutableListOf<KLineEntity>() }
    var quote: Array<String>? = null
    val kNowList: MutableList<KLineEntity> by lazy { mutableListOf<KLineEntity>() }
    val kNewList: MutableList<KLineEntity> by lazy { mutableListOf<KLineEntity>() }
    val kOldList: MutableList<KLineEntity> by lazy { mutableListOf<KLineEntity>() }
    val depthBuyList: MutableList<DepthDataBean> by lazy { mutableListOf<DepthDataBean>() }
    val depthSellList: MutableList<DepthDataBean> by lazy { mutableListOf<DepthDataBean>() }
    private val buyList: MutableList<ShenDubean> by lazy { mutableListOf<ShenDubean>() }
    private val sellList: MutableList<ShenDubean> by lazy { mutableListOf<ShenDubean>() }
    private val newDealList: MutableList<NewDealBean> by lazy { mutableListOf<NewDealBean>() }
    private val marketList: MutableList<DataSource> = DataSource.getIntList()
    private val marketParamList: MutableList<String> by lazy {
        mutableListOf<String>(KLineParam.MARKET_MYSELF, KLineParam.MARKET_HUOBI)
    }

    final val DATA_TYPE_INIT = 0//k线初始化
    final val DATA_TYPE_GET_DATA = 1//k线获取数据
    final val DATA_TYPE_REFRESH = 2//k线加载历史数据
    var dataType = DATA_TYPE_INIT
    val KLINE_TYPE_NOWLINEK = "nowLineK"
    val KLINE_TYPE_OLDLINEK = "oldLineK"
    val KLINE_TYPE_NEWLINEK = "newLineK"
    var pairsBean: TransactionPairsBean? = null

    override fun initSocket(pair: String, mOnQueryKlineCallback: (kList: MutableList<KLineEntity>) -> Unit, mOnSubscribeKlineCallback: (newKList: MutableList<KLineEntity>) -> Unit, mOnTodayCallback: (todayBean: SoketTodayBean) -> Unit, mOnDepthCallback: (mDepthBuyList: MutableList<DepthDataBean>, mDepthSellList: MutableList<DepthDataBean>) -> Unit, mOnDealCallback: (dealList: MutableList<SoketDealBean>) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subscribeAllData(type: Int, pattern: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun changeType(type: Int, pattern: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subscribeDepthAndToday(amount: Int, depth: String, pair: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun initKSocket(market: String) {
        socket?.cancel()
        dataType = DATA_TYPE_INIT
        nWebSocket = NWebSocket.newInstance("$market", wsListener)
        socket = nWebSocket?.open()

    }

    /**
     * 获取K线数据
     */
    private fun getKDataRequest() {
        dataType = DATA_TYPE_GET_DATA
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
     * 切换k线时间间隔
     */
    private fun changeTimeRequest(time: String) {
        dataType = DATA_TYPE_GET_DATA
        val requestBean: KLineRequestBean = KLineRequestBean(KLineParam.METHOD_CHANGE_TIME, mutableListOf<String>())
        requestBean.params.add(time)
        val param: String = gson.toJson(requestBean, KLineRequestBean::class.java)
        NLog.i("changeTimeRequest>>$param")
        socket?.send(param)
    }


    /**
     * k线加载更多历史数据
     */
    private fun loadMoreRequest(oldTime: String) {
        dataType = DATA_TYPE_REFRESH
        val requestBean: KLineRequestBean = KLineRequestBean(KLineParam.METHOD_HISTORY, mutableListOf<String>())
        requestBean.params.add(oldTime)
        val param: String = gson.toJson(requestBean, KLineRequestBean::class.java)
        NLog.i("loadMoreRequest>>$param")
        socket?.send(param)
    }


    fun checkMarket() {
        val requestBean: KLineRequestBean = KLineRequestBean(KLineParam.METHOD_QUERYDATASOURCE, mutableListOf<String>())
        requestBean.params.add("${pairsBean?.currency?.toUpperCase()}${pairsBean?.mainCurrency?.toUpperCase()}")
        val param: String = gson.toJson(requestBean, KLineRequestBean::class.java)
        NLog.i("changeTimeRequest>>$param")
        socket?.send(param)
    }

    private val wsListener = object : WebSocketListener() {
        val gson: Gson = Gson()
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
        val DATA_DATASOURCE = 8
        override fun onMessage(webSocket: WebSocket, text: String) {
            Observable.create<Int> {
                try {
                    var soketbean: Soketbean = gson.fromJson(text, Soketbean::class.java)
                    val lineK = soketbean.lineK
                    val handicap = soketbean.handicap
                    val latestDeal = soketbean.latestDeal
                    val quotes = soketbean.quotes
                    val depth = soketbean.depth
                    val source = soketbean.DataSource
                    if (lineK != null) {
                        var kList = mutableListOf<KLineEntity>()
                        lineK.result?.let {
                            it.forEachIndexed { index, it ->
                                val bean = KLineEntity()
//                                bean.Date = TimeTool.format(pattern, it[0].toLong() * 1000)
//                                bean.Date = "${TimeTool.format3(pattern, it[0].toLong())}"
                                bean.Open = it[1].toFloat()
                                bean.Close = it[2].toFloat()
                                bean.High = it[3].toFloat()
                                bean.Low = it[4].toFloat()
                                bean.Volume = it[5].toFloat()
                                if (bean.High < bean.Open || bean.High < bean.Close || bean.High < bean.Low) {
                                    NLog.i("KLINE最高值出错>>>index=$index time=${it[0]}")
                                }
                                if (bean.Low > bean.Open || bean.Low > bean.Close || bean.Low > bean.High) {
                                    NLog.i("KLINE最小值出错>>>index=$index time=${it[0]}")
                                }
                                if (bean.Low > 8000 || bean.Open > 8000 || bean.Close > 8000 || bean.High > 8000) {
                                    NLog.i("KLINE出错>>>index=$index time=${it[0]}")
                                }
                                kList.add(bean)
                            }

//                            kList.forEach {
//                                it.Date = TimeTool.format(pattern, it.Date.toLong() * 1000)
//                            }
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
                    if (quotes != null) {//聚合行情
                        quote = quotes
                        it.onNext(DATA_QUOTES)
                    }
                    if (depth != null) {//深度图
                        depthBuyList.clear()
                        depthSellList.clear()
                        depth.asks.forEach {
                            val bean = DepthDataBean()
                            bean.price = it[0]
                            bean.volume = it[1]
                            depthBuyList.add(bean)
                        }
                        depth.bids.forEach {
                            val bean = DepthDataBean()
                            bean.price = it[0]
                            bean.volume = it[1]
                            depthSellList.add(bean)
                        }
                        it.onNext(DATA_DEPTH)
                    }

                    if (handicap != null) {
                        buyList.clear()
                        handicap.asks?.forEach {
                            val bean = ShenDubean(it[1], it[0])
                            buyList.add(bean)
                        }
                        sellList.clear()
                        handicap.bids?.forEach {
                            val bean = ShenDubean(it[1], it[0])
                            sellList.add(bean)
                        }
                        it.onNext(DATA_HANDICAP)
                    }

                    if (latestDeal != null) {
                        newDealList.addAll(0, latestDeal)
                        it.onNext(DATE_NEW_DEAL)
                    }

                    if (source != null && source.size > 1) {
                        marketList.clear()
                        marketList.addAll(source)
                        it.onNext(DATA_DATASOURCE)
                    }
                } catch (e: Exception) {
                    NLog.i("")
//                    it.onError(Throwable("不存在该条交易对的K线信息"))
                }
            }.subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        when (it) {
                            DATA_QUOTES -> {

                            }
                            DATA_NOW_LINEK -> {//k线数据
                            }
                            DATA_NEW_LINEK -> {

                            }
                            DATA_OLD_LINEK -> {

                            }
                            DATA_HANDICAP -> {//盘口数据

                            }
                            DATE_NEW_DEAL -> {//最新成交数据

                            }
                            DATA_DEPTH -> {

                                val sellList = mutableListOf<DepthDataBean>()
                                sellList.addAll(depthSellList)
                                val buyList = mutableListOf<DepthDataBean>()
                                buyList.addAll(depthBuyList)

                                sellList.sortByDescending { it.price }


                            }
                            DATA_DATASOURCE -> {

                            }
                        }
                    }, {
                       NLog.i(it.message)
                    })

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