package com.nze.nzexchange.controller.market

import android.support.v4.view.ViewPager
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.KLineBean
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.http.NWebSocket
import com.nze.nzexchange.tools.TimeTool
import com.nze.nzexchange.tools.dp2px
import com.nze.nzexchange.tools.getNColor
import com.nze.nzexchange.widget.chart.*
import com.nze.nzexchange.widget.chart.formatter.DateFormatter
import com.nze.nzexchange.widget.depth.DepthData
import com.nze.nzexchange.widget.depth.DepthDataBean
import com.nze.nzexchange.widget.depth.DepthMapView
import com.nze.nzexchange.widget.indicator.indicator.IndicatorViewPager
import com.nze.nzexchange.widget.indicator.indicator.ScrollIndicatorView
import com.nze.nzexchange.widget.indicator.indicator.slidebar.ColorBar
import com.nze.nzexchange.widget.indicator.indicator.transition.OnTransitionTextListener
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.FlowableOnSubscribe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_kline.*
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import java.util.ArrayList

class KLineActivity : NBaseActivity(), View.OnClickListener {


    val transactionNameTv: TextView by lazy { tv_transaction_name_kline }//交易对名称
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
    val chartAdapter by lazy { KLineChartAdapter() }
    val fenshiPopup: FenshiPopup by lazy {
        FenshiPopup(this).apply {
            onItemClick = {
                showToast("position>>$it")
                when (it) {
                    0 -> {
                        kChart.setMainDrawLine(true)
                    }
                    1 -> {
                        kChart.setMainDrawLine(false)
                    }
                    2 -> {
                    }
                    3 -> {
                    }
                    4 -> {
                    }
                    5 -> {
                    }
                    6 -> {
                    }
                    7 -> {
                    }
                    8 -> {
                    }
                    9 -> {
                    }
                    10 -> {
                    }
                    11 -> {
                    }
                }
            }
        }
    }
    val depthView: DepthMapView by lazy { depth_view }


    var pairsBean: TransactionPairsBean? = null
    var userBean: UserBean? = UserBean.loadFromApp()
    var nWebSocket: NWebSocket? = null
    var socket: WebSocket? = null


    final val K_FENSHI = 0
    final val K_SHENDU = 1
    var kType: Int = K_FENSHI

    final val DATA_TYPE_INIT = 0
    final val DATA_TYPE_REFRESH = 1
    final val DATA_TYPE_LISTENER = 2
    var dataType = DATA_TYPE_INIT


    val viewPager: ViewPager by lazy { vp_kline }
    val scrollIndicatorView: ScrollIndicatorView by lazy { siv_kline }
    private lateinit var indicatorViewPager: IndicatorViewPager
    private val tabs by lazy {
        mutableListOf<String>().apply {
            add("委托订单")
            add("最新成交")
            add("币种详情")
        }
    }

    override fun getRootView(): Int = R.layout.activity_kline

    override fun initView() {
        intent?.let {
            pairsBean = it.getParcelableExtra(IntentConstant.PARAM_TRANSACTION_PAIR)
        }
        fenshiTv.setOnClickListener(this)
        shenduTv.setOnClickListener(this)


        fenshiPopup.setOnBeforeShowCallback { popupRootView, anchorView, hasShowAnima ->
            if (anchorView != null) {
                fenshiPopup.offsetY = anchorView.height
                return@setOnBeforeShowCallback true
            }
            false
        }
        kChart.adapter = chartAdapter
        selectKline(kType)
        // getKData("oneMinute", userBean?.userId ?: System.currentTimeMillis().toString())
        getKlineData()
        getDepthData()
        initViewPager()
    }

    //ws://192.168.1.101:800/btcbch/007/fivesMinute
    private fun getKData(type: String, userId: String) {
        nWebSocket = NWebSocket.newInstance("${NWebSocket.K_URL}/${pairsBean?.currency?.toLowerCase()}${pairsBean?.mainCurrency?.toLowerCase()}/${userId}/${type}", wsListener)
        socket = nWebSocket?.open()
    }

    private fun getKlineData() {
        Observable.create<List<KLineEntity>> {
            val list = DataRequest.getALL(this@KLineActivity).subList(0, 500)
            DataHelper.calculate(list)
            it.onNext(list)
            it.onComplete()
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    chartData.addAll(it)
                    chartAdapter.addFooterData(chartData)
                    chartAdapter.notifyDataSetChanged()
                }

    }

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

    private fun initViewPager() {
        scrollIndicatorView.onTransitionListener = OnTransitionTextListener().setColor(getNColor(R.color.color_main), getNColor(R.color.color_head))

        val colorBar = ColorBar(this, getNColor(R.color.color_main), 3)
        colorBar.setWidth(dp2px(60F))
        scrollIndicatorView.setScrollBar(colorBar)

        viewPager.offscreenPageLimit = 2
        indicatorViewPager = IndicatorViewPager(scrollIndicatorView, viewPager)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
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
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            NLog.i("text>>>$text")
            if (dataType == DATA_TYPE_INIT) {
                Observable.create<List<KLineEntity>> {
                    var kLineBean: KLineBean = gson.fromJson(text, KLineBean::class.java)
                    val rs = kLineBean.result
                    val list: MutableList<KLineEntity> = mutableListOf()
                    rs.forEach {
                        val bean = KLineEntity()
                        bean.Date = TimeTool.format(TimeTool.PATTERN7, it[0])

                    }
                }.subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {

                        }
            } else if (dataType == DATA_TYPE_REFRESH) {

            } else {

            }

            super.onMessage(webSocket, text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            NLog.i("bytes>>>$bytes")
            super.onMessage(webSocket, bytes)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
        }
    }
}
