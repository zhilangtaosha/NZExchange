package com.nze.nzexchange.controller.market.presenter

import android.os.Handler
import com.google.gson.Gson
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.bean.*
import com.nze.nzexchange.config.KLineParam
import com.nze.nzexchange.config.KLineParam.Companion.ID_BIBI_ORDER
import com.nze.nzexchange.database.dao.impl.SoketPairDaoImpl
import com.nze.nzexchange.http.NWebSocket
import com.nze.nzexchange.tools.TimeTool
import com.nze.nzexchange.widget.chart.KLineEntity
import com.nze.nzexchange.widget.depth.DepthDataBean
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject
import java.math.BigDecimal
import java.util.*

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

    var mMarketUrl: String? = null
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
    //涨幅榜
    val mRankList: MutableList<SoketRankBean> by lazy { mutableListOf<SoketRankBean>() }
    //行情
    val mMarketList: MutableList<SoketMarketBean> by lazy { mutableListOf<SoketMarketBean>() }
    //身份认证
    var isAuth: Boolean = false
    //当前订单
    var orderRs: SoketOrderResultBean? = null
    //历史订单
    var orderHistoryRs: SoketOrderResultBean? = null
    //下单结果
    var dealRs: Boolean = false
    //订阅订单
    var subscribeOrderRs: SoketSubscribeOrderBean? = null
    //取消订单
    var mOrderCancelRs: Boolean = false
    var mOrderCancelBean: SoketOrderBean? = null
    //资产查询
    val mAssetMap = hashMapOf<String, SoketAssetBean>()
    var mBibiTrade: WsBibiTradeBean? = null

    //--------------------------------------------------------------------------------------------------
    val mOnTodayMap: MutableMap<String, ((todayBean: SoketTodayBean) -> Unit)> by lazy {
        mutableMapOf<String, ((todayBean: SoketTodayBean) -> Unit)>()
    }
    val mOnDepthMap: MutableMap<String, ((mDepthBuyList: MutableList<DepthDataBean>, mDepthSellList: MutableList<DepthDataBean>) -> Unit)> by lazy {
        mutableMapOf<String, ((mDepthBuyList: MutableList<DepthDataBean>, mDepthSellList: MutableList<DepthDataBean>) -> Unit)>()
    }
    val mOnDealMap: MutableMap<String, ((dealList: MutableList<SoketDealBean>) -> Unit)> by lazy {
        mutableMapOf<String, ((dealList: MutableList<SoketDealBean>) -> Unit)>()
    }
    val OnQueryKlineMap: MutableMap<String, ((kList: MutableList<KLineEntity>) -> Unit)> by lazy {
        mutableMapOf<String, ((kList: MutableList<KLineEntity>) -> Unit)>()
    }
    val mOnSubscribeKlineMap: MutableMap<String, ((newKList: MutableList<KLineEntity>) -> Unit)> by lazy {
        mutableMapOf<String, ((newKList: MutableList<KLineEntity>) -> Unit)>()
    }
    val mOnQueryRankMap: MutableMap<String, ((rankList: MutableList<SoketRankBean>) -> Unit)> by lazy {
        mutableMapOf<String, ((rankList: MutableList<SoketRankBean>) -> Unit)>()
    }
    val mOnOpenMap: MutableMap<String, (() -> Unit)> by lazy {
        mutableMapOf<String, (() -> Unit)>()
    }
    val mOnCloseMap: MutableMap<String, (() -> Unit)> by lazy {
        mutableMapOf<String, (() -> Unit)>()
    }
    val mOnMarketRankMap: MutableMap<String, ((marketList: MutableList<SoketMarketBean>) -> Unit)> by lazy {
        mutableMapOf<String, ((marketList: MutableList<SoketMarketBean>) -> Unit)>()
    }

    val mOnAuthMap: MutableMap<String, ((rs: Boolean) -> Unit)> by lazy {
        mutableMapOf<String, ((rs: Boolean) -> Unit)>()
    }

    val mOnQueryCurrentOrderMap: MutableMap<String, ((orderList: MutableList<SoketOrderBean>) -> Unit)> by lazy {
        mutableMapOf<String, ((orderList: MutableList<SoketOrderBean>) -> Unit)>()
    }

    val mOnSubscribeOrderMap: MutableMap<String, ((order: SoketSubscribeOrderBean) -> Unit)> by lazy {
        mutableMapOf<String, ((order: SoketSubscribeOrderBean) -> Unit)>()
    }
    val mOnQueryAssetMap: MutableMap<String, ((assetMap: HashMap<String, SoketAssetBean>) -> Unit)> by lazy {
        mutableMapOf<String, ((assetMap: HashMap<String, SoketAssetBean>) -> Unit)>()
    }
    val mOnSubscribeAssetMap: MutableMap<String, ((assetMap: HashMap<String, SoketAssetBean>) -> Unit)> by lazy {
        mutableMapOf<String, ((assetMap: HashMap<String, SoketAssetBean>) -> Unit)>()
    }
    var mOnCurrentOrderCancel: ((rs: Boolean, bean: SoketOrderBean?) -> Unit)? = null
    var mOnLimitDeal: ((rs: WsBibiTradeBean) -> Unit)? = null
    var mOnMarketDeal: ((rs: WsBibiTradeBean) -> Unit)? = null
    var mOnQueryHistoryOrder: ((orderList: MutableList<SoketOrderBean>) -> Unit)? = null
    //---------------------------------------------------------------------------------------------
    val mSoketPairDao by lazy { SoketPairDaoImpl() }
    val mAllCommandMap: MutableMap<String, String> by lazy { mutableMapOf<String, String>() }


    override fun initSocket(key: String, marketUrl: String, onOpenCallback: (() -> Unit)?, onCloseCallback: (() -> Unit)?) {
        this.mMarketUrl = marketUrl
        if (onOpenCallback != null)
            mOnOpenMap.put(key, onOpenCallback)
        if (onCloseCallback != null)
            mOnCloseMap.put(key, onCloseCallback)
        socket?.cancel()
        NLog.i("请求接口：$marketUrl")
        nWebSocket = NWebSocket.newInstance(marketUrl, wsListener)
        socket = nWebSocket?.open()
    }

    override fun addCallBack(
            key: String,
            mOnQueryKlineCallback: (kList: MutableList<KLineEntity>) -> Unit,
            mOnSubscribeKlineCallback: (newKList: MutableList<KLineEntity>) -> Unit,
            mOnTodayCallback: (todayBean: SoketTodayBean) -> Unit,
            mOnDepthCallback: (mDepthBuyList: MutableList<DepthDataBean>, mDepthSellList: MutableList<DepthDataBean>) -> Unit,
            mOnDealCallback: (dealList: MutableList<SoketDealBean>) -> Unit
    ) {

        this.OnQueryKlineMap.put(key, mOnQueryKlineCallback)
        this.mOnSubscribeKlineMap.put(key, mOnSubscribeKlineCallback)
        this.mOnTodayMap.put(key, mOnTodayCallback)
        this.mOnDepthMap.put(key, mOnDepthCallback)
        this.mOnDealMap.put(key, mOnDealCallback)

    }

    override fun addRankCallBak(key: String, mOnQueryRankCallback: (rankList: MutableList<SoketRankBean>) -> Unit) {
        this.mOnQueryRankMap.put(key, mOnQueryRankCallback)
    }

    override fun addMarketCallBack(key: String, onMarketRankCallback: (marketList: MutableList<SoketMarketBean>) -> Unit) {
        this.mOnMarketRankMap.put(key, onMarketRankCallback)
    }

    override fun addAuthCallBack(key: String, mOnAuthCallBack: (rs: Boolean) -> Unit) {
        this.mOnAuthMap.put(key, mOnAuthCallBack)
    }

    override fun addCurrentOrderCallBack(
            key: String,
            onQueryOrder: (MutableList<SoketOrderBean>) -> Unit,
            onSubscribeOrder: (order: SoketSubscribeOrderBean) -> Unit,
            mOnCurrentOrderCancel: ((rs: Boolean, bean: SoketOrderBean?) -> Unit)
    ) {
        this.mOnQueryCurrentOrderMap.put(key, onQueryOrder)
        this.mOnSubscribeOrderMap.put(key, onSubscribeOrder)
        this.mOnCurrentOrderCancel = mOnCurrentOrderCancel
    }

    override fun addLimitDealCallBack(onLimitDeal: (rs: WsBibiTradeBean) -> Unit) {
        this.mOnLimitDeal = onLimitDeal
    }

    override fun addMarketDealCallBack(onMarketDeal: (rs: WsBibiTradeBean) -> Unit) {
        this.mOnMarketDeal = onMarketDeal
    }

    override fun addHistoryOrderCallBack(onQueryOrder: (MutableList<SoketOrderBean>) -> Unit) {
        this.mOnQueryHistoryOrder = onQueryOrder
    }

    override fun addAssetCallBack(key: String, queryCallBack: (assetMap: HashMap<String, SoketAssetBean>) -> Unit, subscribeCallBack: (assetMap: HashMap<String, SoketAssetBean>) -> Unit) {
        this.mOnQueryAssetMap.put(key, queryCallBack)
        this.mOnSubscribeAssetMap.put(key, subscribeCallBack)
    }


    override fun removeCallBack(key: String) {
        this.OnQueryKlineMap.remove(key)
        this.mOnSubscribeKlineMap.remove(key)
        this.mOnTodayMap.remove(key)
        this.mOnDepthMap.remove(key)
        this.mOnDealMap.remove(key)
        this.mOnQueryRankMap.remove(key)
        this.mOnMarketRankMap.remove(key)
        this.mOnAuthMap.remove(key)
        this.mOnQueryCurrentOrderMap.remove(key)
        this.mOnSubscribeOrderMap.remove(key)
        this.mOnQueryAssetMap.remove(key)
        this.mOnSubscribeAssetMap.remove(key)
        this.mOnQueryHistoryOrder = null
    }

    override fun removeCallBack3(key: String) {
        when (key) {
            "bibi" -> {
                this.mOnQueryCurrentOrderMap.remove(key)
                this.mOnSubscribeOrderMap.remove(key)
            }
        }

    }


    override fun removeCallBack2() {
        this.mOnLimitDeal = null
        this.mOnMarketDeal = null
        this.mOnCurrentOrderCancel = null
    }

    //--------------------------币币交易页面------------------------------------------------------------------------

    fun queryBibiMarket() {
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_MARKET_RANK, KLineParam.ID_BIBI_MARKET)
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("queryBibiMarket>>$param")
        socket?.send(param)
    }

    //--------------------------------------------------------------------------------------------------
    override fun subscribeAllData(pair: String, type: Int, pattern: String) {
        this.mPair = pair
        queryKline(type, pattern)
        subscribeToday()
        subscribeDeals()
        subscribeDepth(KLineParam.AMOUNT_DEPTH_100, KLineParam.DEPTH_1)
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
        mDealList.clear()
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

    override fun queryRank() {//查询涨幅榜
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_QUERY_RANK, KLineParam.ID_RANK)
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("queryRank>>$param")
        socket?.send(param)
    }

    override fun queryMarket(key: String) {
        var requestBean: SoketRequestBean? = null
        when (key) {
            "side" -> {
                requestBean = SoketRequestBean.create(KLineParam.METHOD_MARKET_RANK, KLineParam.ID_SIDE_MARKET)
            }
            "market" -> {
                requestBean = SoketRequestBean.create(KLineParam.METHOD_MARKET_RANK, KLineParam.ID_MARKET)
            }
            "bibi" -> {
                requestBean = SoketRequestBean.create(KLineParam.METHOD_MARKET_RANK, KLineParam.ID_BIBI_MARKET)
            }
            "home" -> {
                requestBean = SoketRequestBean.create(KLineParam.METHOD_MARKET_RANK, KLineParam.ID_HOME_MARKET)
            }
            else -> {
                requestBean = SoketRequestBean.create(KLineParam.METHOD_MARKET_RANK, KLineParam.ID_MARKET)
            }
        }

        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("queryMarket>>$param")
        socket?.send(param)
    }

    override fun auth(token: String) {
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_AUTH, KLineParam.ID_AUTH)
        requestBean.params.add(token)
        requestBean.params.add("Android")
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("auth>>$param")
        socket?.send(param)
    }

    override fun queryCurrentOrder(key: String, pair: String, offset: Int, limit: Int, side: Int) {
        var requestBean: SoketRequestBean? = null
        when (key) {
            "bibi" -> {
                requestBean = SoketRequestBean.create(KLineParam.METHOD_QUERY_ORDER, KLineParam.ID_BIBI_ORDER)
            }
            "order" -> {
                requestBean = SoketRequestBean.create(KLineParam.METHOD_QUERY_ORDER, KLineParam.ID_ORDER)
            }
            else -> {
                requestBean = SoketRequestBean.create(KLineParam.METHOD_QUERY_ORDER, KLineParam.ID_ORDER)
            }
        }

        requestBean.params.add(pair)
        requestBean.params.add(offset)
        requestBean.params.add(limit)
        requestBean.params.add(side)
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("queryCurrentOrder>>$param")
        socket?.send(param)
    }

    override fun subscribeOrder(pair: String) {
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_SUBSCRIBE_ORDER)
        requestBean.params.add(pair)
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("subscribeOrder>>$param")
        socket?.send(param)
    }

    override fun limitDeal(pair: String, side: Int, amount: Double, price: Double) {
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_LIMIT_DEAL, KLineParam.ID_LIMIT_DEAL)
        requestBean.params.add(pair)
        requestBean.params.add(side)
        requestBean.params.add("$amount")
        requestBean.params.add("$price")
        requestBean.params.add("Android")
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("limitDeal>>$param")
        socket?.send(param)
    }

    override fun marketDeal(pair: String, side: Int, amount: Double) {
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_MARKET_DEAL, KLineParam.ID_MARKET_DEAL)
        requestBean.params.add(pair)
        requestBean.params.add(side)
        requestBean.params.add("${amount}")
        requestBean.params.add("Android")
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("marketDeal>>$param")
        socket?.send(param)
    }

    override fun orderCancel(pair: String, id: Long) {
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_ORDER_CANCEL, KLineParam.ID_ORDER_CANCEL)
        requestBean.params.add(pair)
        requestBean.params.add(id)
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("orderCancel>>$param")
        socket?.send(param)
    }


    override fun queryHistoryOrder(pair: String, startTime: Long, endTime: Long, offset: Int, limit: Int, side: Int) {
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_ORDER_HISTORY, KLineParam.ID_ORDER_HISTORY)
        requestBean.params.add(pair)
        requestBean.params.add(startTime)
        requestBean.params.add(endTime)
        requestBean.params.add(offset)
        requestBean.params.add(limit)
        requestBean.params.add(side)
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("queryHistoryOrder>>$param")
        socket?.send(param)
    }

    override fun queryAsset(list: List<String>) {
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_QUERY_ASSET, KLineParam.ID_ASSET)
        list.forEach {
            requestBean.params.add(it)
        }
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("queryAsset>>$param")
        socket?.send(param)
    }

    override fun subscribeAsset(list: List<String>) {
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_SUBSCRIBE_ASSET)
        list.forEach {
            requestBean.params.add(it)
        }
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("subscribeAsset>>$param")
        socket?.send(param)
    }

    var isReconnnect = true
    var subscribe: Disposable? = null
    val mHandler: Handler = Handler()

    val pingRunnable: Runnable = object : Runnable {
        override fun run() {
            checkHeart()
        }
    }

    fun checkHeart() {
//        isReconnnect = true
        val requestBean = SoketRequestBean.create(KLineParam.METHOD_PING, KLineParam.ID_PING)
        val param: String = gson.toJson(requestBean, SoketRequestBean::class.java)
        NLog.i("checkHeart>>$param")
        socket?.send(param)
        mHandler.postDelayed(pingRunnable, 60 * 1000)
//        subscribe = Observable.create<Boolean> {
//            var i = 20
//            while (i > 0) {
//                if (!isReconnnect) {
//                    NLog.i("checkHeart postDelayed....${isReconnnect}")
//                    mHandler.postDelayed(pingRunnable, 60 * 1000)
//                    if (!subscribe?.isDisposed!!) {
//                        subscribe?.dispose()
//                    }
//                    break
//                } else {
//                    try {
//                        Thread.sleep(1000)
//                    } catch (e: Exception) {
//                        mHandler.postDelayed(pingRunnable, 60 * 1000)
//                    }
//                }
//                i--
//            }
//            it.onNext(isReconnnect)
//            it.onComplete()
//        }.subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe {
//                    if (it) {
//                        NLog.i("checkHeart init....${it} ${isReconnnect}")
//                        mHandler.removeCallbacks(pingRunnable)
//                        if (mOnOpenMap.size > 0)
//                            this.mOnOpenMap.clear()
//                        initSocket("", mMarketUrl!!, null, null)
//                    }
//                }

    }

    //----------------------------------------------------------------------------------------------

    override fun close() {
        nWebSocket?.close()
        socket?.cancel()
    }

    //----------------------------------------------------------------------------------------------
    private val wsListener = object : WebSocketListener() {
        val gson: Gson = Gson()
        override fun onOpen(webSocket: WebSocket, response: Response) {
            NLog.i("nonOpen")
            mOnOpenMap.forEach {
                it.value.invoke()
            }
            checkHeart()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            NLog.i("nonFailure")
            mOnCloseMap.forEach {
                it.value.invoke()
            }
            if (mOnOpenMap.size > 0)
                mOnOpenMap.clear()
            initSocket("recontect", mMarketUrl!!, {

            }, null)
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
                        val errorBean = queryBean.error
                        when (queryBean.id) {
                            KLineParam.ID_KLINE -> {//查询根据id确认
//                            NLog.i("request>>>${queryBean.result}")
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
                            KLineParam.ID_RANK -> {
                                try {
                                    val rankList: Array<SoketRankBean>? = gson.fromJson<Array<SoketRankBean>>(queryBean.result.toString().replace("/", "-"), Array<SoketRankBean>::class.java)
                                    if (rankList != null && rankList.size > 0) {
                                        mRankList.clear()
                                        mRankList.addAll(rankList)
                                        it.onNext(KLineParam.DATA_RANK_QUERY)
                                    }
                                } catch (e: Exception) {
                                    NLog.i("rank出错>>${e.message}")
                                }
                            }
                            KLineParam.ID_MARKET, KLineParam.ID_BIBI_MARKET, KLineParam.ID_SIDE_MARKET, KLineParam.ID_HOME_MARKET -> {
                                try {
                                    val marketList: Array<SoketMarketBean>? = gson.fromJson<Array<SoketMarketBean>>(queryBean.result.toString().replace("/", "-"), Array<SoketMarketBean>::class.java)
                                    if (marketList != null && marketList.size > 0) {
                                        mMarketList.clear()
                                        mMarketList.addAll(marketList.toMutableList())
                                        when (queryBean.id) {
                                            KLineParam.ID_BIBI_MARKET -> {
                                                it.onNext(KLineParam.ID_BIBI_MARKET)
                                            }
                                            KLineParam.ID_SIDE_MARKET -> {
                                                it.onNext(KLineParam.ID_SIDE_MARKET)
                                            }
                                            KLineParam.ID_MARKET -> {
                                                it.onNext(KLineParam.ID_MARKET)
                                            }
                                            KLineParam.ID_HOME_MARKET -> {
                                                it.onNext(KLineParam.ID_HOME_MARKET)
                                            }
                                        }
                                        mSoketPairDao.add(mMarketList)
                                    }
                                } catch (e: Exception) {
                                    NLog.i("market出错>>${e.message}")
                                }
                            }
                            KLineParam.ID_AUTH -> {
                                isAuth = queryBean.error == null
                                it.onNext(KLineParam.DATA_AUTH)
                            }
                            KLineParam.ID_ORDER, KLineParam.ID_BIBI_ORDER -> {
                                try {
                                    orderRs = gson.fromJson<SoketOrderResultBean>(queryBean.result.toString(), SoketOrderResultBean::class.java)
                                    if (queryBean.id == KLineParam.ID_BIBI_ORDER) {
                                        it.onNext(KLineParam.ID_BIBI_ORDER)
                                    } else {
                                        it.onNext(KLineParam.ID_ORDER)
                                    }
                                } catch (e: Exception) {
                                    NLog.i("current order出错>>${e.message}")
                                    orderRs = SoketOrderResultBean(0, 0, 0, arrayListOf())
                                    it.onNext(KLineParam.ID_ORDER)
                                }
                            }
                            KLineParam.ID_LIMIT_DEAL -> {
                                dealRs = errorBean == null
                                if (dealRs) {
                                    mBibiTrade = WsBibiTradeBean(dealRs, 0, "")
                                } else {
                                    mBibiTrade = WsBibiTradeBean(dealRs, errorBean!!.code, errorBean!!.message)
                                }
                                it.onNext(KLineParam.DATA_LIMIT_DEAL)
                            }
                            KLineParam.ID_MARKET_DEAL -> {
                                dealRs = queryBean.error == null
                                if (dealRs) {
                                    mBibiTrade = WsBibiTradeBean(dealRs, 0, "")
                                } else {
                                    mBibiTrade = WsBibiTradeBean(dealRs, errorBean!!.code, errorBean!!.message)
                                }
                                it.onNext(KLineParam.DATA_MARKET_DEAL)
                            }
                            KLineParam.ID_ORDER_CANCEL -> {
                                mOrderCancelRs = queryBean.error == null
                                if (mOrderCancelRs)
                                    mOrderCancelBean = gson.fromJson<SoketOrderBean>(queryBean.result.toString(), SoketOrderBean::class.java)
                                it.onNext(KLineParam.DATA_ORDER_CANCEL)
                            }
                            KLineParam.ID_ORDER_HISTORY -> {
                                try {
                                    orderHistoryRs = gson.fromJson<SoketOrderResultBean>(queryBean.result.toString(), SoketOrderResultBean::class.java)
                                    it.onNext(KLineParam.DATA_ORDER_HISTORY)
                                } catch (e: Exception) {
                                    orderHistoryRs = SoketOrderResultBean(0, 0, 0, arrayListOf())
                                    it.onNext(KLineParam.DATA_ORDER_HISTORY)
                                }
                            }
                            KLineParam.ID_ASSET -> {
                                mAssetMap.clear()
                                val result = JSONObject(queryBean.result.toString())
                                val keys = result.keys()
                                keys.forEach {
                                    val obj = result.getJSONObject(it)
                                    val assetBean = gson.fromJson<SoketAssetBean>(obj.toString(), SoketAssetBean::class.java)
                                    mAssetMap.put(it, assetBean)
                                }
                                it.onNext(KLineParam.DATA_ASSET_QUERY)
                            }
                            KLineParam.ID_PING -> {
                                isReconnnect = false
                                it.onNext(-1000)
                            }
                        }
                    } catch (e: Exception) {
                        NLog.i("query出错>>${e.message}")
                    }
                } else {//订阅
                    val subscribeBean: SoketSubscribeBean = gson.fromJson(text, SoketSubscribeBean::class.java)
                    when (subscribeBean.method) {
                        KLineParam.SUBSCRIBE_KLINE -> {//k线
                            try {
                                mNewKList.clear()
//                                NLog.i("kline update>>>${subscribeBean.params}")
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
                                NLog.i("kline.update出错>>${e.message}")
                            }
                        }
                        KLineParam.SUBSCRIBE_TODAY -> {//今日行情
                            try {
                                val rs = gson.fromJson<Array<Any>>(subscribeBean.params.toString(), Array<Any>::class.java)
                                mTodayBean = gson.fromJson<SoketTodayBean>(rs[1].toString(), SoketTodayBean::class.java)
                                it.onNext(KLineParam.DATA_TODAY_SUBSCRIBE)
                                NLog.i("today 发送到主线程 ${mTodayBean?.last}")
                            } catch (e: Exception) {
                                NLog.i("today.update出错>>${e.message}")
                            }
                        }
                        KLineParam.SUBSCRIBE_DEPTH -> {//深度
                            try {
                                val s = subscribeBean.params.toString()
                                val sub = s.substring(s.indexOf(",") + 1, s.lastIndexOf(","))
                                val dbean = gson.fromJson<SoketDepthBean>(sub, SoketDepthBean::class.java)

                                val rs = gson.fromJson<Array<Any>>(s, Array<Any>::class.java)
                                val isClear: Boolean = rs[0] as Boolean
                                if (isClear) {
                                    mDepthSellList.clear()
                                    mDepthBuyList.clear()
                                }
//                                mDepthBean = gson.fromJson<SoketDepthBean>(rs[1].toString(), SoketDepthBean::class.java)
                                dbean!!.asks?.forEach {
                                    //卖
                                    val bean = DepthDataBean()
                                    bean.price = it[0].toDouble()
                                    bean.volume = it[1].toDouble()
                                    bean.priceStr = it[0]
                                    bean.volueStr = it[1]
                                    if (isClear) {
                                        mDepthSellList.add(bean)
                                    } else {
                                        if (bean.volume != 0.0) {
                                            val i = mDepthSellList.indexOfFirst {
                                                it.price == bean.price
                                            }
                                            if (i >= 0) {
                                                mDepthSellList[i].volume = bean.volume
                                                mDepthSellList[i].volueStr = bean.volueStr
                                            } else {
                                                mDepthSellList.add(bean)
                                            }
                                        } else {
                                            val i = mDepthSellList.indexOfFirst {
                                                it.price == bean.price
                                            }
                                            mDepthSellList.removeAt(i)
                                        }
                                    }
                                }
                                dbean!!.bids?.forEach {
                                    //买
                                    val bean = DepthDataBean()
                                    bean.price = it[0].toDouble()
                                    bean.volume = it[1].toDouble()
                                    bean.priceStr = it[0]
                                    bean.volueStr = it[1]
                                    if (isClear) {
                                        mDepthBuyList.add(bean)
                                    } else {
                                        if (bean.volume != 0.0) {
                                            var isAdd = true
                                            for (buy in mDepthBuyList) {
                                                if (buy.price == bean.price) {
                                                    buy.volume = bean.volume
                                                    buy.volueStr = bean.volueStr
                                                    isAdd = false
                                                    return@forEach
                                                }
                                            }
                                            if (isAdd) {
                                                mDepthBuyList.add(bean)
                                            }
                                        } else {
                                            val i = mDepthBuyList.indexOfFirst {
                                                it.price == bean.price
                                            }
                                            mDepthBuyList.removeAt(i)
                                        }
                                    }
                                }
                                it.onNext(KLineParam.DATA_DEPTH_SUBSCRIBE)
                            } catch (e: Exception) {
                                NLog.i("depth.update出错>>${e.message}")
                            }
                        }
                        KLineParam.SUBSCRIBE_DEALS -> {//最近成交列表
                            try {
                                val rs = gson.fromJson<Array<Any>>(subscribeBean.params.toString(), Array<Any>::class.java)
                                val dealList: Array<SoketDealBean> = gson.fromJson<Array<SoketDealBean>>(rs[1].toString(), Array<SoketDealBean>::class.java)
                                mDealList.addAll(dealList)
                                mDealList.sortByDescending { it.time }
                                it.onNext(KLineParam.DATA_DEALS_SUBSCRIBE)
                            } catch (e: Exception) {
                                NLog.i("deals.update出错>>${e.message}")
                            }
                        }
                        KLineParam.SUBSCRIBE_ORDER -> {//订阅当前委托单
                            try {
                                val rs = gson.fromJson<Array<Any>>(subscribeBean.params.toString(), Array<Any>::class.java)
                                val orderBean = gson.fromJson<SoketOrderBean>(rs[1].toString(), SoketOrderBean::class.java)
                                subscribeOrderRs = SoketSubscribeOrderBean(rs[0].toString().toDouble().toInt(), orderBean)
                                it.onNext(KLineParam.DATA_ORDER_SUBSCRIBE)
                            } catch (e: Exception) {
                                NLog.i("order.update出错>>${e.message}")
                            }
                        }
                        KLineParam.SUBSCRIBE_ASSET -> {//订阅资产
//                            mAssetMap.clear()
                            val rs = gson.fromJson<Array<Any>>(subscribeBean.params.toString(), Array<Any>::class.java)
                            rs.forEach {
                                val result = JSONObject(it.toString())
                                val keys = result.keys()
                                keys.forEach {
                                    val obj = result.getJSONObject(it)
                                    val assetBean = gson.fromJson<SoketAssetBean>(obj.toString(), SoketAssetBean::class.java)
                                    mAssetMap.put(it, assetBean)
                                }
                            }
                            it.onNext(KLineParam.DATA_ASSET_SUBSCRIBE)
                        }
                    }
                }
            }.subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        when (it) {
                            KLineParam.DATA_KLINE_QUERY -> {
//                                mOnQueryKlineCallback?.invoke(mKList)
                                OnQueryKlineMap.forEach {
                                    it.value.invoke(mKList)
                                }
                                subscribeKline()
                            }
                            KLineParam.DATA_KLINE_SUBSCRIBE -> {
//                                mOnSubscribeKlineCallback?.invoke(mNewKList)
                                mOnSubscribeKlineMap.forEach {
                                    it.value.invoke(mNewKList)
                                }
                            }
                            KLineParam.DATA_TODAY_SUBSCRIBE -> {
                                NLog.i("today 主线程接收 ${mTodayBean?.last}")
                                mOnTodayMap.forEach {
                                    it.value.invoke(mTodayBean!!)
                                }
//                                mOnTodayCallback?.invoke(mTodayBean!!)
                            }
                            KLineParam.DATA_DEPTH_SUBSCRIBE -> {
                                mOnDepthMap.forEach {
                                    it.value.invoke(mDepthBuyList, mDepthSellList)
                                }
//                                mOnDepthCallback?.invoke(mDepthBuyList, mDepthSellList)
                            }
                            KLineParam.DATA_DEALS_SUBSCRIBE -> {
                                mOnDealMap.forEach {
                                    it.value.invoke(mDealList)
                                }
//                                mOnDealCallback?.invoke(mDealList)
                            }
                            KLineParam.DATA_RANK_QUERY -> {
                                mOnQueryRankMap.forEach {
                                    it.value.invoke(mRankList)
                                }
                            }
                            KLineParam.ID_MARKET -> {
                                mOnMarketRankMap["market"]?.invoke(mMarketList)
                            }
                            KLineParam.ID_BIBI_MARKET -> {
                                mOnMarketRankMap["bibi"]?.invoke(mMarketList)
                            }
                            KLineParam.ID_SIDE_MARKET -> {
                                mOnMarketRankMap["side"]?.invoke(mMarketList)
                            }
                            KLineParam.ID_HOME_MARKET -> {
                                mOnMarketRankMap["home"]?.invoke(mMarketList)
                            }
                            KLineParam.DATA_AUTH -> {
                                mOnAuthMap.forEach {
                                    it.value.invoke(isAuth)
                                }
                            }
                            KLineParam.ID_BIBI_ORDER -> {
                                if (orderRs == null)
                                    orderRs = SoketOrderResultBean(0, 0, 0, arrayListOf())
                                mOnQueryCurrentOrderMap["bibi"]?.invoke(orderRs!!.records.toMutableList())
//                                mOnQueryCurrentOrderMap.forEach {
//                                    it.value.invoke(orderRs!!.records.toMutableList())
//                                }
                            }
                            KLineParam.ID_ORDER -> {
                                if (orderRs == null)
                                    orderRs = SoketOrderResultBean(0, 0, 0, arrayListOf())
                                mOnQueryCurrentOrderMap["order"]?.invoke(orderRs!!.records.toMutableList())
                            }
                            KLineParam.DATA_LIMIT_DEAL -> {//限价交易
                                mOnLimitDeal?.invoke(mBibiTrade!!)
                            }
                            KLineParam.DATA_MARKET_DEAL -> {//市价交易
                                mOnMarketDeal?.invoke(mBibiTrade!!)
                            }
                            KLineParam.DATA_ORDER_SUBSCRIBE -> {
                                mOnSubscribeOrderMap.forEach {
                                    it.value.invoke(subscribeOrderRs!!)
                                }
                            }
                            KLineParam.DATA_ORDER_CANCEL -> {
                                if (mOrderCancelRs) {
                                    mOnCurrentOrderCancel?.invoke(mOrderCancelRs, mOrderCancelBean)
                                } else {
                                    mOnCurrentOrderCancel?.invoke(mOrderCancelRs, null)
                                }
                            }
                            KLineParam.DATA_ORDER_HISTORY -> {
                                mOnQueryHistoryOrder?.invoke(orderHistoryRs!!.records.toMutableList())
                            }
                            KLineParam.DATA_ASSET_QUERY -> {
                                mOnQueryAssetMap.forEach {
                                    it.value.invoke(mAssetMap)
                                }
                            }
                            KLineParam.DATA_ASSET_SUBSCRIBE -> {
                                mOnSubscribeAssetMap.forEach {
                                    it.value.invoke(mAssetMap)
                                }
                            }
                        }
                    }, {
                        NLog.i("出错了 。。。${it.message}")
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
