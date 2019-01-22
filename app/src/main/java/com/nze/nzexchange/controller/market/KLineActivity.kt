package com.nze.nzexchange.controller.market

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.http.NWebSocket
import com.nze.nzexchange.widget.chart.*
import com.nze.nzexchange.widget.chart.formatter.DateFormatter
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.FlowableOnSubscribe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_kline.*
import okhttp3.*
import okio.ByteString

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
    val fenshiPopup: FenshiPopup by lazy { FenshiPopup(this) }

    final val K_FENSHI = 0
    final val K_SHENDU = 1
    var kType: Int = K_FENSHI

    override fun getRootView(): Int = R.layout.activity_kline

    override fun initView() {
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
        getData()
//        btn1_ak.setOnClickListener {
//            val ws = KLinews()
//
//            val socket = NWebSocket.newInstance("ws://192.168.1.101:80/btcbch/websocket/007/fivesMinute", ws)
//            socket.open()
//        }
    }

    private fun getData() {
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


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_fenshi_kline -> {
                if (kType == K_FENSHI) {
                    fenshiPopup.showPopupWindow(fenshiLayout)
                    showToast("弹出")
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


    class KLinews : WebSocketListener() {
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
