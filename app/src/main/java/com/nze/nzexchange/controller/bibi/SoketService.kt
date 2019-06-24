package com.nze.nzexchange.controller.bibi

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.nze.nzexchange.bean.SoketDealBean
import com.nze.nzexchange.bean.SoketTodayBean
import com.nze.nzexchange.config.KLineParam
import com.nze.nzexchange.controller.market.presenter.IWebSoket
import com.nze.nzexchange.controller.market.presenter.WebSoketImpl
import com.nze.nzexchange.http.NWebSocket
import com.nze.nzexchange.widget.chart.KLineEntity
import com.nze.nzexchange.widget.depth.DepthDataBean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/6/13
 */
class SoketService : Service() {
    private val binder: SoketBinder = SoketBinder()

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

    class SoketBinder : Binder(), IWebSoket {


        private val webSoket: IWebSoket by lazy { WebSoketImpl() }
        override var mOnTodayCallback: ((todayBean: SoketTodayBean) -> Unit)? = null
        override var mOnDepthCallback: ((mDepthBuyList: MutableList<DepthDataBean>, mDepthSellList: MutableList<DepthDataBean>) -> Unit)? = null
        override var mOnDealCallback: ((dealList: MutableList<SoketDealBean>) -> Unit)? = null
        override var mOnQueryKlineCallback: ((kList: MutableList<KLineEntity>) -> Unit)? = null
        override var mOnSubscribeKlineCallback: ((newKList: MutableList<KLineEntity>) -> Unit)? = null

        override fun initSocket(
                pair: String,
                marketUrl: String,
                mOnQueryKlineCallback: ((kList: MutableList<KLineEntity>) -> Unit),
                mOnSubscribeKlineCallback: ((newKList: MutableList<KLineEntity>) -> Unit),
                mOnTodayCallback: ((todayBean: SoketTodayBean) -> Unit),
                mOnDepthCallback: ((mDepthBuyList: MutableList<DepthDataBean>, mDepthSellList: MutableList<DepthDataBean>) -> Unit),
                mOnDealCallback: ((dealList: MutableList<SoketDealBean>) -> Unit)
        ) {
            webSoket.initSocket(
                    pair,
                    marketUrl,
                    mOnQueryKlineCallback,
                    mOnSubscribeKlineCallback,
                    mOnTodayCallback,
                    mOnDepthCallback,
                    mOnDealCallback)
        }

        override fun initCallBack(
                mOnQueryKlineCallback: (kList: MutableList<KLineEntity>) -> Unit,
                mOnSubscribeKlineCallback: (newKList: MutableList<KLineEntity>) -> Unit,
                mOnTodayCallback: (todayBean: SoketTodayBean) -> Unit,
                mOnDepthCallback: (mDepthBuyList: MutableList<DepthDataBean>, mDepthSellList: MutableList<DepthDataBean>) -> Unit,
                mOnDealCallback: (dealList: MutableList<SoketDealBean>) -> Unit) {
            webSoket.initCallBack(
                    mOnQueryKlineCallback,
                    mOnSubscribeKlineCallback,
                    mOnTodayCallback,
                    mOnDepthCallback,
                    mOnDealCallback)
        }

        override fun subscribeAllData(type: Int, pattern: String) {
            webSoket.subscribeAllData(type, pattern)
        }

        override fun subscribeDepthAndToday(amount: Int, depth: String, pair: String) {
            webSoket.subscribeDepthAndToday(amount, depth, pair)
        }

        override fun changeType(type: Int, pattern: String) {
            webSoket.changeType(type, pattern)
        }

        override fun close() {
            webSoket.close()
        }
    }


}