package com.nze.nzexchange.controller.market

import android.net.Uri
import android.view.View
import android.widget.*
import com.google.gson.Gson
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.NewDealBean
import com.nze.nzexchange.bean.Soketbean
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.bean2.ShenDubean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.config.KLineParam
import com.nze.nzexchange.config.KLineRequestBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.http.NWebSocket
import com.nze.nzexchange.tools.TimeTool
import com.nze.nzexchange.widget.LinearLayoutAsListView
import com.nze.nzexchange.widget.chart.*
import com.nze.nzexchange.widget.chart.formatter.DateFormatter
import com.nze.nzexchange.widget.depth.DepthData
import com.nze.nzexchange.widget.depth.DepthDataBean
import com.nze.nzexchange.widget.depth.DepthMapView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_kline.*
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.greenrobot.eventbus.EventBus
import org.jetbrains.annotations.Nls
import org.json.JSONObject
import java.util.*

/**
 * http://www.blue-zero.com/WebSocket/
 */
class KLineActivity : NBaseActivity(), View.OnClickListener, NBaseFragment.OnFragmentInteractionListener {

    val transactionNameTv: TextView by lazy { tv_transaction_name_kline }//交易对名称
    val switchLeftIv: ImageView by lazy { iv_switch_left_kline }//左切换按钮
    val switchRightIv: ImageView by lazy { iv_switch_right_kline }
    val marketNameTv: TextView by lazy { tv_market_name_kline }//市场名称
    val pairNameTv: TextView by lazy { tv_pair_name_kline }//交易对名称
    val backMySelf: ImageView by lazy { iv_back_kline }//回到公司交易平台

    val switchIv: ImageView by lazy { iv_switch_kline }//切换全屏按钮
    val selfSelectIv: ImageView by lazy { iv_self_select_kline }//自选按钮
    val costTv: TextView by lazy { tv_cost_kline }//兑换价格
    val hightCostTv: TextView by lazy { tv_hight_cost_kline }//24小时最高价
    val lowCostTv: TextView by lazy { tv_low_cost_kline }//24h最低价
    val volumeTv: TextView by lazy { tv_volume_kline }//24h成交量
    val rangeTv: TextView by lazy { tv_range_kline }//24h涨跌幅
    val fenshiLayout: RelativeLayout by lazy { layout_fenshi_kline }
    val fenshiTv: TextView by lazy { tv_fenshi_kline }//分时选择按钮
    val fenshiView: View by lazy { view_fenshi_kline }
    val shenduTv: TextView by lazy { tv_shendu_kline }
    val shenduView: View by lazy { view_shendu_kline }
    val hideCb: CheckBox by lazy { cb_hide_kline }
    val kChart: KLineChartView by lazy {
        chart_kline.apply {
            dateTimeFormatter = DateFormatter()
            setGridRows(4)
            setGridColumns(4)
        }
    }
    val chartData: MutableList<KLineEntity> by lazy { mutableListOf<KLineEntity>() }
    val kNowList: MutableList<KLineEntity> by lazy { mutableListOf<KLineEntity>() }
    val kNewList: MutableList<KLineEntity> by lazy { mutableListOf<KLineEntity>() }
    val kOldList: MutableList<KLineEntity> by lazy { mutableListOf<KLineEntity>() }
    val chartAdapter by lazy { KLineChartAdapter() }
    var pattern: String = TimeTool.PATTERN5
    val fenshiPopup: FenshiPopup by lazy {
        //切换时间价格
        FenshiPopup(this).apply {
            onItemClick = { position, item ->
                fenshiTv.text = item
                when (position) {
                    0 -> {
                        pattern = TimeTool.PATTERN5
                        kChart.setMainDrawLine(true)
                        changeTimeRequest(KLineParam.TIME_ONE_MIN)
                    }
                    1 -> {
                        pattern = TimeTool.PATTERN5
                        kChart.setMainDrawLine(false)
//                        changeTimeRequest(KLineParam.TIME_ONE_MIN)
                    }
                    2 -> {
                        pattern = TimeTool.PATTERN9
                        kChart.setMainDrawLine(false)
                        changeTimeRequest(KLineParam.TIME_FIVE_MIN)
                    }
                    3 -> {
                        pattern = TimeTool.PATTERN9
                        kChart.setMainDrawLine(false)
                        changeTimeRequest(KLineParam.TIME_FIFTEEN_MIN)
                    }
                    4 -> {
                        pattern = TimeTool.PATTERN9
                        kChart.setMainDrawLine(false)
                        changeTimeRequest(KLineParam.TIME_THIRTY_MIN)
                    }
                    5 -> {
                        pattern = TimeTool.PATTERN9
                        kChart.setMainDrawLine(false)
                        changeTimeRequest(KLineParam.TIME_ONE_HOUR)
                    }
                    6 -> {
                    }
                    7 -> {
                        pattern = TimeTool.PATTERN10
                        kChart.setMainDrawLine(false)
                        changeTimeRequest(KLineParam.TIME_ONE_DAY)
                    }
                    8 -> {
                        pattern = TimeTool.PATTERN10
                        kChart.setMainDrawLine(false)
                        changeTimeRequest(KLineParam.TIME_ONE_WEEK)
                    }
                }
            }
        }
    }
    val depthView: DepthMapView by lazy { depth_view }
    val orderTv: TextView by lazy { tv_order_kline }
    val orderView: View by lazy { view_order_kline }
    val newDealTv: TextView by lazy { tv_new_deal_kline }
    val newDealView: View by lazy { view_new_deal_kline }
    val currencyDetailTv: TextView by lazy { tv_currency_detail_kline }
    val currencyDetailView: View by lazy { view_currency_detail_kline }
    val SELECT_ORDER = 0
    val SELECT_NEW_DEAL = 1
    val SELECT_CURRENCY_DETAIL = 2
    var currentSelect = SELECT_ORDER


    val orderLayout: LinearLayout by lazy { layout_order_kline }
    val newDealLayout: LinearLayout by lazy { layout_new_deal_kline }
    val currencyDetailLayout: RelativeLayout by lazy { layout_currency_detail_kline }
    val buyLv: LinearLayoutAsListView by lazy { lv_buy_kline }
    val sellLv: LinearLayoutAsListView by lazy { lv_sell_kline }
    val newDealLv: LinearLayoutAsListView by lazy { lv_new_deal_kline }
    val buyBtn: Button by lazy { btn_buy_kline }
    val saleBtn: Button by lazy { btn_sale_kline }

    var pairsBean: TransactionPairsBean? = null
    var userBean: UserBean? = UserBean.loadFromApp()
    var nWebSocket: NWebSocket? = null
    var socket: WebSocket? = null


    final val K_FENSHI = 0
    final val K_SHENDU = 1
    var kType: Int = K_FENSHI

    final val DATA_TYPE_INIT = 0//k线初始化
    final val DATA_TYPE_GET_DATA = 1//k线获取数据
    final val DATA_TYPE_REFRESH = 2//k线加载历史数据
    var dataType = DATA_TYPE_INIT
    val KLINE_TYPE_NOWLINEK = "nowLineK"
    val KLINE_TYPE_OLDLINEK = "oldLineK"
    val KLINE_TYPE_NEWLINEK = "newLineK"

    private val buyAdapter: KLineBuyAdapter by lazy { KLineBuyAdapter(this) }
    private val sellAdapter: KLineSellAdapter by lazy { KLineSellAdapter(this) }
    private val newDealAdapter: KLineNewDealAdapter by lazy { KLineNewDealAdapter(this) }
    private val buyList: MutableList<ShenDubean> by lazy { mutableListOf<ShenDubean>() }
    private val sellList: MutableList<ShenDubean> by lazy { mutableListOf<ShenDubean>() }
    private val newDealList: MutableList<NewDealBean> by lazy { mutableListOf<NewDealBean>() }

    var socketRequestId: String = ""
        get() {
            return System.currentTimeMillis().toString()
        }
    val gson: Gson by lazy { Gson() }
    var isSocketOpen: Boolean = false


    private val marketList: MutableList<String> by lazy {
        mutableListOf<String>("AUS", "火币")
    }
    private val marketParamList: MutableList<String> by lazy {
        mutableListOf<String>(KLineParam.MARKET_MYSELF, KLineParam.MARKET_HUOBI)
    }
    private var marketIndex: Int = 0

    override fun getRootView(): Int = R.layout.activity_kline

    override fun initView() {
        intent?.let {
            pairsBean = it.getParcelableExtra(IntentConstant.PARAM_TRANSACTION_PAIR)
        }
        fenshiTv.setOnClickListener(this)
        shenduTv.setOnClickListener(this)
        orderTv.setOnClickListener(this)
        newDealTv.setOnClickListener(this)
        currencyDetailTv.setOnClickListener(this)
        buyBtn.setOnClickListener(this)
        saleBtn.setOnClickListener(this)
        transactionNameTv.setOnClickListener(this)
        switchLeftIv.setOnClickListener(this)
        switchRightIv.setOnClickListener(this)

        pairNameTv.text = pairsBean?.transactionPair

        fenshiPopup.setOnBeforeShowCallback { popupRootView, anchorView, hasShowAnima ->
            if (anchorView != null) {
                fenshiPopup.offsetY = anchorView.height
                return@setOnBeforeShowCallback true
            }
            false
        }
        kChart.adapter = chartAdapter

        //默认展示分时图
        kChart.setMainDrawLine(true)
        selectKline(kType)
        select(currentSelect)
        // getKData("oneMinute", userBean?.userId ?: System.currentTimeMillis().toString())

        getDepthData()


//        test()
        changMarket(0)
//        initKSocket(KLineParam.MARKET_MYSELF)
    }

    private fun test() {

        getKlineData()

        buyAdapter.group = ShenDubean.getList()
        buyLv.adapter = buyAdapter

        sellAdapter.group = ShenDubean.getList()
        sellLv.adapter = sellAdapter
    }

    //ws://192.168.1.101:800/btcbch/007/fivesMinute
    private fun initKSocket(market: String) {
//        if (nWebSocket!=null&&socket != null) {
//            nWebSocket?.close()
//            socket?.cancel()
//            socket = null
//            nWebSocket = null
//        }
//        nWebSocket = NWebSocket.newInstance("${NWebSocket.K_URL}/${pairsBean?.currency?.toLowerCase()}${pairsBean?.mainCurrency?.toLowerCase()}/${userId}/${type}", wsListener)
        socket?.cancel()
        dataType = DATA_TYPE_INIT
        nWebSocket = NWebSocket.newInstance("${NWebSocket.K_URL}/$market", wsListener)
        socket = nWebSocket?.open()

    }

    /**
     * 获取K线数据
     */
    private fun getKDataRequest() {
        dataType = DATA_TYPE_GET_DATA
        val requestBean: KLineRequestBean = KLineRequestBean(KLineParam.METHOD_GET_K, mutableListOf<String>())
        if (marketIndex % marketList.size == 0) {
            requestBean.params.add("${pairsBean?.currency?.toUpperCase()}${pairsBean?.mainCurrency?.toUpperCase()}")
        } else {
            requestBean.params.add("${pairsBean?.mainCurrency?.toUpperCase()}${pairsBean?.currency?.toUpperCase()}")
        }
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
        chartAdapter.clearData()
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

    /**
     * K线测试数据
     */
    private fun getKlineData() {
        Observable.create<List<KLineEntity>> {
            val list = DataRequest.getALL(this@KLineActivity).subList(0, 500)
//            DataHelper.calculate(list)
            it.onNext(list)
            it.onComplete()
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    chartData.addAll(it)
                    chartAdapter.addFooterData(it)
                    chartAdapter.notifyDataSetChanged()
                }

    }

    /**
     * 切换市场
     */
    fun changMarket(index: Int) {
        val len = marketList.size
        val i = index % len
        marketNameTv.text = marketList[i]
//        if (nWebSocket != null && socket != null) {
//            nWebSocket?.close()
//            socket?.close(1000, "")
//            socket = null
//            nWebSocket = null
//        }
        chartAdapter.clearData()
        initKSocket(marketParamList[i])
    }

    /**
     * 深度图测试数据
     */
    private fun getDepthData() {
        val jsonData = DepthData.depthB
        val listDepthBuy = ArrayList<DepthDataBean>()
        val listDepthSell = ArrayList<DepthDataBean>()

        val jsonObject = JSONObject(jsonData)
        val jsonTick = jsonObject.optJSONObject("tick")
        val arrBids = jsonTick.optJSONArray("bids")
        val arrAsks = jsonTick.optJSONArray("asks")

        var obj: DepthDataBean
        var price: String
        var volume: String

        for (i in 0 until arrBids.length()) {
            obj = DepthDataBean()
            price = arrBids.getString(i).replace("[", "").replace("]", "").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            volume = arrBids.getString(i).replace("[", "").replace("]", "").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            obj.volume = java.lang.Float.valueOf(volume)
            obj.price = java.lang.Float.valueOf(price)
            listDepthBuy.add(obj)
        }
        for (i in 0 until arrAsks.length()) {
            obj = DepthDataBean()
            price = arrAsks.getString(i).replace("[", "").replace("]", "").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            volume = arrAsks.getString(i).replace("[", "").replace("]", "").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            obj.volume = java.lang.Float.valueOf(volume)
            obj.price = java.lang.Float.valueOf(price)
            listDepthSell.add(obj)
        }
        depthView.setData(listDepthBuy, listDepthSell)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_switch_left_kline -> {
                changMarket(Math.abs(--marketIndex))
            }
            R.id.iv_switch_right_kline -> {
                changMarket(Math.abs(++marketIndex))
            }
            R.id.tv_fenshi_kline -> {
                if (kType == K_FENSHI) {
                    fenshiPopup.showPopupWindow(fenshiLayout)
                    return
                }
                selectKline(K_FENSHI)
            }
            R.id.tv_shendu_kline -> {
                selectKline(K_SHENDU)
            }
            R.id.tv_order_kline -> {
                currentSelect = SELECT_ORDER
                select(currentSelect)
            }
            R.id.tv_new_deal_kline -> {
                currentSelect = SELECT_NEW_DEAL
                select(currentSelect)
            }
            R.id.tv_currency_detail_kline -> {
                currentSelect = SELECT_CURRENCY_DETAIL
                select(currentSelect)
            }
            R.id.btn_buy_kline -> {
                this@KLineActivity.finish()
                EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_REFRESH_MAIN_ACT, 2))
                EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_TRADE_BIBI, 1))
            }
            R.id.btn_sale_kline -> {
                this@KLineActivity.finish()
                EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_REFRESH_MAIN_ACT, 2))
                EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_TRADE_BIBI, 0))
            }
            R.id.tv_transaction_name_kline -> {
                finish()
            }
        }
    }

    fun selectKline(type: Int) {
        kType = type
        fenshiTv.isSelected = type == K_FENSHI
        fenshiView.visibility = if (type == K_FENSHI) View.VISIBLE else View.GONE
        shenduTv.isSelected = type == K_SHENDU
        shenduView.visibility = if (type == K_SHENDU) View.VISIBLE else View.GONE

        kChart.visibility = if (type == K_FENSHI) View.VISIBLE else View.GONE
        depthView.visibility = if (type == K_SHENDU) View.VISIBLE else View.GONE
    }

    private fun select(select: Int) {
        orderTv.isSelected = select == SELECT_ORDER
        newDealTv.isSelected = select == SELECT_NEW_DEAL
        currencyDetailTv.isSelected = select == SELECT_CURRENCY_DETAIL
        orderView.visibility = if (select == SELECT_ORDER) View.VISIBLE else View.GONE
        newDealView.visibility = if (select == SELECT_NEW_DEAL) View.VISIBLE else View.GONE
        currencyDetailView.visibility = if (select == SELECT_CURRENCY_DETAIL) View.VISIBLE else View.GONE
        orderLayout.visibility = if (select == SELECT_ORDER) View.VISIBLE else View.GONE
        newDealLayout.visibility = if (select == SELECT_NEW_DEAL) View.VISIBLE else View.GONE
        currencyDetailLayout.visibility = if (select == SELECT_CURRENCY_DETAIL) View.VISIBLE else View.GONE
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOverridePendingTransitionMode(): TransitionMode = TransitionMode.DEFAULT

    override fun isBindEventBusHere(): Boolean = false

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkDisConnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContainerTargetView(): View? = null

    private val wsListener = object : WebSocketListener() {
        val gson: Gson = Gson()
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            isSocketOpen = true
            NLog.i("onOpen")
//            if (marketIndex == 0)
            getKDataRequest()
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
        override fun onMessage(webSocket: WebSocket, text: String) {
            NLog.i("text>>>$text")
            Observable.create<Int> {
                var soketbean: Soketbean = gson.fromJson(text, Soketbean::class.java)
                val lineK = soketbean.lineK
                val handicap = soketbean.handicap
                val latestDeal = soketbean.latestDeal
                if (lineK != null) {
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
//                        kList.sortBy { TimeTool.dateToStamp(it.Date, pattern) }
                        kList=  kList.distinctBy { it.date.toLong() }.toMutableList()
                        kList.sortBy { it.date.toLong() }
                        kList.forEach {
                            it.Date = TimeTool.format(pattern,it.Date.toLong() * 1000)
                        }
                        DataHelper.calculate(kList)
                    }
                    when (lineK.type) {
                        KLINE_TYPE_NOWLINEK -> {
                            kNowList.clear()
                            kNowList.addAll(kList)
                            it.onNext(DATA_NOW_LINEK)
                        }
                        KLINE_TYPE_NEWLINEK -> {
                            kNewList.clear()
                            kNewList.addAll(kList)
                            it.onNext(DATA_NEW_LINEK)
                        }
                        KLINE_TYPE_OLDLINEK -> {
                            kOldList.clear()
                            kOldList.addAll(kList)
                            it.onNext(DATA_OLD_LINEK)
                        }
                    }
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
                    newDealList.clear()
                    newDealList.addAll(latestDeal)
                    it.onNext(DATE_NEW_DEAL)
                }
            }.subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        when (it) {
                            DATA_NOW_LINEK -> {//k线数据
//                                chartData.addAll(kList)
                                NLog.i("nowLineK>>>>>>>>>>")
                                chartAdapter.addFooterData(kNowList)
//                                chartAdapter.addFooterData(chartData)
                                chartAdapter.notifyDataSetChanged()
                            }
                            DATA_NEW_LINEK -> {
                                NLog.i("newLineK>>>>>>>>>")
                                chartAdapter.addHeaderData(kNewList)
                                chartAdapter.notifyDataSetChanged()
                            }
                            DATA_OLD_LINEK -> {
                                chartAdapter.addFooterData(kOldList)
                                chartAdapter.notifyDataSetChanged()
                            }
                            DATA_HANDICAP -> {//盘口数据
                                buyAdapter.group = buyList
                                buyLv.adapter = buyAdapter

                                sellAdapter.group = sellList
                                sellLv.adapter = sellAdapter
                            }
                            DATE_NEW_DEAL -> {//最新成交数据
                                newDealAdapter.group = newDealList
                                newDealLv.adapter = newDealAdapter
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
//            if (isChangeMarket) {
//                initKSocket(marketParamList[marketIndex % marketList.size])
//            }
        }
    }

    override fun onFragmentInteraction(uri: Uri) {
    }


    override fun onDestroy() {
        super.onDestroy()
        nWebSocket?.close()
        socket?.cancel()
    }

}
