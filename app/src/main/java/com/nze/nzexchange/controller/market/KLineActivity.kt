package com.nze.nzexchange.controller.market

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.*
import com.google.gson.Gson
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.*
import com.nze.nzexchange.bean2.ShenDubean
import com.nze.nzexchange.config.*
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.main.MainActivity
import com.nze.nzexchange.controller.market.presenter.KLineP
import com.nze.nzexchange.controller.market.presenter.WebSoketImpl
import com.nze.nzexchange.extend.*
import com.nze.nzexchange.http.NWebSocket
import com.nze.nzexchange.tools.TimeTool
import com.nze.nzexchange.widget.LinearLayoutAsListView
import com.nze.nzexchange.widget.chart.*
import com.nze.nzexchange.widget.chart.draw.Status
import com.nze.nzexchange.widget.chart.formatter.DateFormatter
import com.nze.nzexchange.widget.depth.DepthData
import com.nze.nzexchange.widget.depth.DepthDataBean
import com.nze.nzexchange.widget.depth.DepthMapView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_kline.*
import net.grandcentrix.tray.AppPreferences
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.util.*

/**
 * http://www.blue-zero.com/WebSocket/
 */
class KLineActivity : NBaseActivity(), View.OnClickListener, NBaseFragment.OnFragmentInteractionListener {
    val webSoketP by lazy { WebSoketImpl() }
    val kLineP: KLineP by lazy { KLineP(this) }
    val transactionNameTv: TextView by lazy { tv_transaction_name_kline }//交易对名称
    val switchLeftIv: ImageView by lazy { iv_switch_left_kline }//左切换按钮
    val switchRightIv: ImageView by lazy { iv_switch_right_kline }
    val marketNameTv: TextView by lazy { tv_market_name_kline }//市场名称
    val pairNameTv: TextView by lazy { tv_pair_name_kline }//交易对名称
    val backMySelf: ImageView by lazy {
        iv_back_kline.apply {
            setOnClickListener {
                marketIndex = 0
                changMarket(marketIndex)
            }
        }
    }//回到公司交易平台

    val switchIv: ImageView by lazy { iv_switch_kline }//切换全屏按钮
    val selfSelectTv: TextView by lazy { iv_self_select_kline }//自选按钮
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
    val setIv: ImageView by lazy { iv_setting_kline }
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
    var pattern: String = TimeTool.PATTERN5
    val fenshiPopup: FenshiPopup by lazy {
        //切换时间价格
        FenshiPopup(this).apply {
            onItemClick = { position, item ->
                kChart.hideSelectData()
                fenshiTv.text = item
                when (position) {
                    0 -> {
                        pattern = TimeTool.PATTERN5
                        kChart.setMainDrawLine(true)
                        webSoketP.changeType(KLineParam.KLINE_TYPE_ONE_MIN, pattern)
                    }
                    1 -> {
                        pattern = TimeTool.PATTERN5
                        kChart.setMainDrawLine(false)
                        webSoketP.changeType(KLineParam.KLINE_TYPE_ONE_MIN, pattern)
                    }
                    2 -> {
                        pattern = TimeTool.PATTERN9
                        kChart.setMainDrawLine(false)
                        webSoketP.changeType(KLineParam.KLINE_TYPE_FIVE_MIN, pattern)
                    }
                    3 -> {
                        pattern = TimeTool.PATTERN9
                        kChart.setMainDrawLine(false)
                        webSoketP.changeType(KLineParam.KLINE_TYPE_FIFTEEN_MIN, pattern)
                    }
                    4 -> {
                        pattern = TimeTool.PATTERN9
                        kChart.setMainDrawLine(false)
                        webSoketP.changeType(KLineParam.KLINE_TYPE_THIRTY_MIN, pattern)
                    }
                    5 -> {
                        pattern = TimeTool.PATTERN9
                        kChart.setMainDrawLine(false)
                        webSoketP.changeType(KLineParam.KLINE_TYPE_ONE_HOUR, pattern)
                    }
                    6 -> {
                        pattern = TimeTool.PATTERN9
                        kChart.setMainDrawLine(false)
                        webSoketP.changeType(KLineParam.KLINE_TYPE_FOUR_HOUR, pattern)
                    }
                    7 -> {
                        pattern = TimeTool.PATTERN10
                        kChart.setMainDrawLine(false)
                        webSoketP.changeType(KLineParam.KLINE_TYPE_ONE_DAY, pattern)
                    }
                    8 -> {
                        pattern = TimeTool.PATTERN10
                        kChart.setMainDrawLine(false)
                        webSoketP.changeType(KLineParam.KLINE_TYPE_ONE_WEEK, pattern)
                    }
                }
            }
        }
    }
    val setPopup: KLineSetPopup by lazy {
        KLineSetPopup(this).apply {
            onKClick = {
                refreshKLineConfig()
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

    final val K_FENSHI = 0
    final val K_SHENDU = 1
    var kType: Int = K_FENSHI

    private val buyAdapter: KLineBuyAdapter by lazy { KLineBuyAdapter(this) }
    private val sellAdapter: KLineSellAdapter by lazy { KLineSellAdapter(this) }
    private val newDealAdapter: KLineNewDealAdapter by lazy { KLineNewDealAdapter(this) }

    //------------------------币种详情--------------------------------------------
    private val tokenNameTv: TextView by lazy { tv_token_name_kline }//交易币名称
    private val issueTimeTv: TextView by lazy { issue_time_value_kline }//发行时间
    private val issueAmountTv: TextView by lazy { issue_amount_value_kline }//发行总量
    private val circulatioAmountTv: TextView by lazy { circulatio_amount_value_kline }//流通总量
    private val crowdFundingPriceTv: TextView by lazy { crowd_funding_price_value_kline }//众筹价格
    private val whitePaperTv: TextView by lazy { white_paper_value_kline }//白皮书地址
    private val officialWebsiteTv: TextView by lazy { official_website_value_kline }//官网地址
    private val blockQueryTv: TextView by lazy { block_query_value_kline }//区块查询地址
    private val introductionTv: TextView by lazy { introduction_value_kline }//简介


    private var marketIndex: Int = 0
    private var isFirst = true


    companion object {
        fun skip(context: Context, bean: TransactionPairsBean) {
            val intent = Intent(context, KLineActivity::class.java)
            intent.putExtra(IntentConstant.PARAM_TRANSACTION_PAIR, bean)
            context.startActivity(intent)
        }
    }

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
        selfSelectTv.setOnClickListener(this)
        setIv.setOnClickListener(this)

        pairNameTv.text = pairsBean?.transactionPair

        if (userBean != null) {
            selfSelectTv.visibility = View.VISIBLE
            selfSelectTv.isSelected = pairsBean?.optional == 1
        } else {
            selfSelectTv.visibility = View.GONE
        }

        fenshiPopup.setOnBeforeShowCallback { popupRootView, anchorView, hasShowAnima ->
            if (anchorView != null) {
                fenshiPopup.offsetY = anchorView.height
                return@setOnBeforeShowCallback true
            }
            false
        }
        setPopup.setOnBeforeShowCallback { popupRootView, anchorView, hasShowAnima ->
            if (anchorView != null) {
                setPopup.offsetY = anchorView.height
                return@setOnBeforeShowCallback true
            }
            false
        }
        kChart.adapter = chartAdapter

        //默认展示分时图
        kChart.setMainDrawLine(true)
        kChart.setRefreshListener {
            kChart.refreshEnd()
        }
        selectKline(kType)
        select(currentSelect)


//        getDepthData()

        //测试数据
//        test()


        getTokenInfo()

        refreshLayout()
        refreshKLineConfig()
        initSoket()

        //先关闭测试币种详情接口
        changMarket(0)
    }

    fun initSoket() {
        webSoketP.initSocket(
                "${pairsBean?.currency?.toUpperCase()}${pairsBean?.mainCurrency?.toUpperCase()}",
                {
                    //查询k线
                    chartData.addAll(it)
                    chartAdapter.addFooterData(chartData)
                    chartAdapter.notifyDataSetChanged()
                },
                {
                    //订阅k线
                    chartData.addAll(it)
                    chartAdapter.addHeaderData(chartData)
                    chartAdapter.notifyDataSetChanged()
                },
                {
                    //订阅今日行情
                    costTv.text = it.last.formatForPrice()
                    hightCostTv.text = it.high.formatForPrice()
                    lowCostTv.text = it.low.formatForPrice()
                    volumeTv.text = it.volume.retainInt()
                    setPriceWave(it.open, it.last)
                },
                { mDepthBuyList, mDepthSellList ->
                    //订阅深度
                    buyAdapter.group = mDepthBuyList
                    buyLv.adapter = buyAdapter

                    sellAdapter.group = mDepthSellList
                    sellLv.adapter = sellAdapter

                    depthView.setData(mDepthBuyList, mDepthSellList)
                },
                {
                    //订阅最近成交列表
                    newDealAdapter.group = it.take(20).toMutableList()
                    newDealLv.adapter = newDealAdapter
                })
        webSoketP.subscribeAllData(KLineParam.KLINE_TYPE_ONE_MIN, pattern)
    }

    fun refreshLayout() {
        selfSelectTv.isSelected = pairsBean!!.optional == 1
    }


    /**
     * 切换市场
     */
    fun changMarket(index: Int) {
        //切换前清空数据
        costTv.text = "0"
        hightCostTv.text = "0"
        lowCostTv.text = "0"
        volumeTv.text = "0"
        rangeTv.text = "0%"
        chartAdapter.clearData()
        depthView.clearData()
        buyAdapter.clearGroup(true)
        buyLv.adapter = buyAdapter
        sellAdapter.clearGroup(true)
        sellLv.adapter = sellAdapter
        newDealAdapter.clearGroup(true)
        newDealLv.adapter = newDealAdapter

        //切换市场
        chartAdapter.clearData()

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
            obj.volume = volume.toDouble()
            obj.price = price.toDouble()
            listDepthBuy.add(obj)
        }
        for (i in 0 until arrAsks.length()) {
            obj = DepthDataBean()
            price = arrAsks.getString(i).replace("[", "").replace("]", "").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            volume = arrAsks.getString(i).replace("[", "").replace("]", "").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            obj.volume = volume.toDouble()
            obj.price = price.toDouble()
            listDepthSell.add(obj)
        }
        depthView.setData(listDepthBuy, listDepthSell)
    }

    /**
     * 获取币种详情
     */
    fun getTokenInfo() {
        TokenInfoBean.getTokenInfo(pairsBean?.currency!!)
                .compose(netTf())
                .subscribe({
                    if (it.success) {
                        it.result.let {
                            tokenNameTv.text = it.tokenSymbol
                            issueTimeTv.text = it.tokenTime
                            issueAmountTv.text = it.tokenAmount
                            circulatioAmountTv.text = it.tokenCirculate
                            crowdFundingPriceTv.text = it.tokenPrice
                            whitePaperTv.text = it.tokenPaper
                            officialWebsiteTv.text = it.tokenWebsite
                            blockQueryTv.text = it.tokenChaintype
                            introductionTv.text = it.tokenDesc
                        }
                    }
                }, {
                    NLog.i("")
                })
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
                skipActivity(MainActivity::class.java)
                this@KLineActivity.finish()
                EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_REFRESH_MAIN_ACT, 2))
                EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_TRADE_BIBI, 1))
                EventBus.getDefault().post(EventCenter<TransactionPairsBean>(EventCode.CODE_SELECT_TRANSACTIONPAIR, pairsBean))
            }
            R.id.btn_sale_kline -> {
                skipActivity(MainActivity::class.java)
                this@KLineActivity.finish()
                EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_REFRESH_MAIN_ACT, 2))
                EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_TRADE_BIBI, 0))
                EventBus.getDefault().post(EventCenter<TransactionPairsBean>(EventCode.CODE_SELECT_TRANSACTIONPAIR, pairsBean))
            }
            R.id.tv_transaction_name_kline -> {
                finish()
            }
            R.id.iv_self_select_kline -> {
                kLineP.optionalHandler(userBean!!, pairsBean!!) {
                    selfSelectTv.isSelected = it == 1
                }
            }
            R.id.iv_setting_kline -> {
                setPopup.showPopupWindow(setIv)
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


    override fun onFragmentInteraction(uri: Uri) {
    }


    override fun onDestroy() {
        super.onDestroy()
        webSoketP.close()
    }


    /**
     * 设置涨跌幅
     */
    fun setPriceWave(start: Double, end: Double) {
        if (start != 0.0) {
            var w = (end - start) / start
            if (w >= 0) {
                rangeTv.setTxtColor(R.color.color_up)
                costTv.setTxtColor(R.color.color_up)
            } else {
                rangeTv.setTxtColor(R.color.color_down)
                costTv.setTxtColor(R.color.color_down)
            }
            rangeTv.text = "${(w.mul(100.toDouble())).twoPlace()}%"
        } else {
            rangeTv.text = "0%"
        }

    }

    val appPreferences: AppPreferences by lazy { AppPreferences(this) }
    var mainImage = KLineParam.STATUS_MAIN_EMPTY
    var subImage = KLineParam.STATUS_SUB_EMPTY
    fun refreshKLineConfig() {
        mainImage = appPreferences.getString(Preferences.KLINE_MAIN_IMAGE, KLineParam.STATUS_MAIN_EMPTY)!!
        subImage = appPreferences.getString(Preferences.KLINE_SUB_IMAGE, KLineParam.STATUS_SUB_EMPTY)!!
        kChart.hideSelectData()
        when (mainImage) {//主图
            KLineParam.STATUS_MA -> {
                kChart.changeMainDrawType(Status.MA)
            }
            KLineParam.STATUS_BOLL -> {
                kChart.changeMainDrawType(Status.BOLL)
            }
            KLineParam.STATUS_MAIN_EMPTY -> {
                kChart.changeMainDrawType(Status.NONE)
            }
        }
        when (subImage) {//副图
            KLineParam.STATUS_MACD -> {
                kChart.setChildDraw(0)
            }
            KLineParam.STATUS_KDJ -> {
                kChart.setChildDraw(1)
            }
            KLineParam.STATUS_RSI -> {
                kChart.setChildDraw(2)
            }
            KLineParam.STATUS_WR -> {
                kChart.setChildDraw(3)
            }
            KLineParam.STATUS_SUB_EMPTY -> {
                kChart.hideChildDraw()
            }
        }
    }
}
