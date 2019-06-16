package com.nze.nzexchange.controller.market.presenter

import com.google.gson.Gson
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.ui.BaseActivityP
import com.nze.nzexchange.bean.*
import com.nze.nzexchange.config.KLineParam
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.http.NWebSocket
import com.nze.nzexchange.tools.TimeTool
import com.nze.nzexchange.widget.chart.KLineEntity
import com.nze.nzexchange.widget.depth.DepthDataBean
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.math.BigDecimal

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:张总的格式
 * 测试地址
 * http://www.blue-zero.com/WebSocket/
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
class WebSoketImpl : IWebSoket {


    var nWebSocket: NWebSocket? = null
    var socket: WebSocket? = null
    val gson: Gson by lazy { Gson() }
    lateinit var mPair: String
    var kType: Int = KLineParam.KLINE_TYPE_ONE_MIN
    var mPattern: String = TimeTool.PATTERN5
    val mDepthBuyList: MutableList<DepthDataBean> by lazy { mutableListOf<DepthDataBean>() }
    val mDepthSellList: MutableList<DepthDataBean> by lazy { mutableListOf<DepthDataBean>() }

    //今日行情
    var mTodayBean: SoketTodayBean? = null
    //深度
    var mDepthBean: SoketDepthBean? = null
    //最近成交
    val mDealList: MutableList<SoketDealBean> by lazy { mutableListOf<SoketDealBean>() }
    //k线
    val mKList: MutableList<KLineEntity> by lazy { mutableListOf<KLineEntity>() }
    val mNewKList: MutableList<KLineEntity> by lazy { mutableListOf<KLineEntity>() }
    override var mOnTodayCallback: ((todayBean: SoketTodayBean) -> Unit)? = null
    override var mOnDepthCallback: ((mDepthBuyList: MutableList<DepthDataBean>, mDepthSellList: MutableList<DepthDataBean>) -> Unit)? = null
    override var mOnDealCallback: ((dealList: MutableList<SoketDealBean>) -> Unit)? = null
    override var mOnQueryKlineCallback: ((kList: MutableList<KLineEntity>) -> Unit)? = null
    override var mOnSubscribeKlineCallback: ((newKList: MutableList<KLineEntity>) -> Unit)? = null

    override fun initSocket(
            pair: String,
            mOnQueryKlineCallback: ((kList: MutableList<KLineEntity>) -> Unit),
            mOnSubscribeKlineCallback: ((newKList: MutableList<KLineEntity>) -> Unit),
            mOnTodayCallback: ((todayBean: SoketTodayBean) -> Unit),
            mOnDepthCallback: ((mDepthBuyList: MutableList<DepthDataBean>, mDepthSellList: MutableList<DepthDataBean>) -> Unit),
            mOnDealCallback: ((dealList: MutableList<SoketDealBean>) -> Unit)
    ) {
        this.mOnQueryKlineCallback = mOnQueryKlineCallback
        this.mOnSubscribeKlineCallback = mOnSubscribeKlineCallback
        this.mOnTodayCallback = mOnTodayCallback
        this.mOnDepthCallback = mOnDepthCallback
        this.mOnDealCallback = mOnDealCallback
        this.mPair = pair
        nWebSocket = NWebSocket.newInstance(KLineParam.MARKET_MYSELF, wsListener)
        socket = nWebSocket?.open()
    }

    override fun subscribeAllData(type: Int, pattern: String) {
        queryKline(type, pattern)
        subscribeToday()
        subscribeDeals()
        subscribeDepth(KLineParam.AMOUNT_DEPTH, KLineParam.DEPTH_8)
    }


    override fun changeType(type: Int, pattern: String) {
        queryKline(type, pattern)
    }

    override fun subscribeDepthAndToday(amount: Int, depth: String, pair: String) {
        this.mPair = pair
        subscribeDepth(amount, depth, pair)
        subscribeToday()
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

    fun subscribeDepth(amount: Int, depth: String, pair: String = mPair) {//订阅深度
        mDepthBuyList.clear()
        mDepthSellList.clear()
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_SUBSCRIBE_DEPTH)
        requestBean.params.add(pair)
        requestBean.params.add(amount)
        requestBean.params.add(depth)
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("subscribeDepth>>$param")
        socket?.send(param)
    }


    fun queryKline(type: Int, pattern: String) {//查询K线
        this.kType = type
        this.mPattern = pattern
        mKList.clear()

        val requestBean = SoketRequestBean.create(KLineParam.METHOD_QUERY_KLINE, KLineParam.ID_KLINE)
        requestBean.params.add(mPair)
        requestBean.params.add(KLineParam.AMOUNT_KLINE)
        requestBean.params.add(kType)
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("queryKline>>$param")
        socket?.send(param)
    }

    fun subscribeKline() {//订阅K线
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_SUBSCRIBE_KLINE)
        requestBean.params.add(mPair)
        requestBean.params.add(kType)
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("subscribeKline>>$param")
        socket?.send(param)
    }

    override fun close() {
        nWebSocket?.close()
        socket?.cancel()
    }

    private val wsListener = object : WebSocketListener() {
        val gson: Gson = Gson()
        override fun onOpen(webSocket: WebSocket, response: Response) {
            NLog.i("nonOpen")


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
                if (text.contains("error")) {//查询或者订阅成功返回
                    try {
                        val queryBean: SoketQueryBean = gson.fromJson(text, SoketQueryBean::class.java)
                        if (queryBean.id == KLineParam.ID_KLINE) {//查询根据id确认
                            NLog.i("request>>>${queryBean.result}")
                            val klist: Array<Array<String>>? = gson.fromJson<Array<Array<String>>>(queryBean.result.toString(), Array<Array<String>>::class.java)
                            klist?.forEachIndexed { index, it ->
                                val bean = KLineEntity()
                                bean.Date = TimeTool.format(mPattern, BigDecimal(it[0]).toPlainString().toLong() * 1000)
                                bean.Open = it[1].toFloat()
                                bean.Close = it[2].toFloat()
                                bean.High = it[3].toFloat()
                                bean.Low = it[4].toFloat()
                                bean.Volume = it[5].toFloat()
                                mKList.add(bean)
                            }
                            it.onNext(KLineParam.DATA_KLINE_QUERY)

                        }
                    } catch (e: Exception) {
                        NLog.i("query出错")
                    }
                } else {//订阅
                    val subscribeBean: SoketSubscribeBean = gson.fromJson(text, SoketSubscribeBean::class.java)
                    when (subscribeBean.method) {
                        KLineParam.SUBSCRIBE_KLINE -> {//k线
                            try {
                                mNewKList.clear()
                                val klist = gson.fromJson<Array<Array<String>>>(subscribeBean.params.toString(), Array<Array<String>>::class.java)
                                klist.forEachIndexed { index, it ->
                                    val bean = KLineEntity()
                                    bean.Date = TimeTool.format(mPattern, BigDecimal(it[0]).toPlainString().toLong() * 1000)
                                    bean.Open = it[1].toFloat()
                                    bean.Close = it[2].toFloat()
                                    bean.High = it[3].toFloat()
                                    bean.Low = it[4].toFloat()
                                    bean.Volume = it[5].toFloat()
                                    mNewKList.add(bean)
                                }
                                if (mKList[mKList.size - 1].date == mNewKList[0].date) {
                                    mNewKList.removeAt(0)
                                }
                                mKList.addAll(mNewKList)

                                it.onNext(KLineParam.DATA_KLINE_SUBSCRIBE)
                            } catch (e: Exception) {
                                NLog.i("kline.update出错")
                            }
                        }
                        KLineParam.SUBSCRIBE_TODAY -> {//今日行情
                            try {
                                mTodayBean = gson.fromJson<SoketTodayBean>(subscribeBean.params[1].toString(), SoketTodayBean::class.java)
                                it.onNext(KLineParam.DATA_TODAY_SUBSCRIBE)
                            } catch (e: Exception) {
                                NLog.i("today.update出错")
                            }
                        }
                        KLineParam.SUBSCRIBE_DEPTH -> {//深度
                            try {
                                mDepthBean = gson.fromJson<SoketDepthBean>(subscribeBean.params[1].toString(), SoketDepthBean::class.java)
                                mDepthBean!!.asks?.forEach {
                                    //卖
                                    val bean = DepthDataBean()
                                    bean.price = it[0]
                                    bean.volume = it[1]
                                    mDepthSellList.add(bean)
                                }
                                mDepthBean!!.bids?.forEach {
                                    //买
                                    val bean = DepthDataBean()
                                    bean.price = it[0]
                                    bean.volume = it[1]
                                    mDepthBuyList.add(bean)
                                }
                                it.onNext(KLineParam.DATA_DEPTH_SUBSCRIBE)
                            } catch (e: Exception) {
                                NLog.i("depth.update出错")
                            }
                        }
                        KLineParam.SUBSCRIBE_DEALS -> {//最近成交列表
                            try {
                                mDealList.clear()
                                val dealList: Array<SoketDealBean> = gson.fromJson<Array<SoketDealBean>>(subscribeBean.params[1].toString(), Array<SoketDealBean>::class.java)
                                mDealList.addAll(dealList)
                                it.onNext(KLineParam.DATA_DEALS_SUBSCRIBE)
                            } catch (e: Exception) {
                                NLog.i("deals.update出错")
                            }
                        }
                    }

                }

            }.subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        when (it) {
                            KLineParam.DATA_KLINE_QUERY -> {
                                mOnQueryKlineCallback?.invoke(mKList)
                                subscribeKline()
                            }
                            KLineParam.DATA_KLINE_SUBSCRIBE -> {
                                mOnSubscribeKlineCallback?.invoke(mNewKList)
                            }
                            KLineParam.DATA_TODAY_SUBSCRIBE -> {
                                mOnTodayCallback?.invoke(mTodayBean!!)
                            }
                            KLineParam.DATA_DEPTH_SUBSCRIBE -> {
                                mOnDepthCallback?.invoke(mDepthBuyList, mDepthSellList)
                            }
                            KLineParam.DATA_DEALS_SUBSCRIBE -> {
                                mOnDealCallback?.invoke(mDealList)
                            }
                        }
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
