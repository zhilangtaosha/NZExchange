package com.nze.nzexchange.controller.bibi

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.nze.nzexchange.bean.*
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
        override fun removeOrderCallBack(key: String) {
            webSoket.removeOrderCallBack(key)
        }

        override fun addAssetCallBack(key: String, queryCallBack: (assetMap: HashMap<String, SoketAssetBean>) -> Unit, subscribeCallBack: (assetMap: HashMap<String, SoketAssetBean>) -> Unit) {
            webSoket.addAssetCallBack(key, queryCallBack, subscribeCallBack)
        }

        override fun queryAsset(list: List<String>) {
            webSoket.queryAsset(list)
        }

        override fun subscribeAsset(list: List<String>) {
            webSoket.subscribeAsset(list)
        }

        override fun addHistoryOrderCallBack(onQueryOrder: (MutableList<SoketOrderBean>) -> Unit) {
            webSoket.addHistoryOrderCallBack(onQueryOrder)
        }

        override fun queryHistoryOrder(pair: String, startTime: Long, endTime: Long, offset: Int, limit: Int, side: Int) {
            webSoket.queryHistoryOrder(pair, startTime, endTime, offset, limit, side)
        }

        override fun orderCancel(pair: String, id: Long) {
            webSoket.orderCancel(pair, id)
        }

        override fun addLimitDealCallBack(onLimitDeal: (rs: Boolean) -> Unit) {
            webSoket.addLimitDealCallBack(onLimitDeal)
        }

        override fun addMarketDealCallBack(onMarketDeal: (rs: Boolean) -> Unit) {
            webSoket.addMarketDealCallBack(onMarketDeal)
        }

        override fun removeCallBack2() {
            webSoket.removeCallBack2()
        }

        override fun limitDeal(pair: String, side: Int, amount: Double, price: Double) {
            webSoket.limitDeal(pair, side, amount, price)
        }

        override fun marketDeal(pair: String, side: Int, amount: Double) {
            webSoket.marketDeal(pair, side, amount)
        }

        override fun addCurrentOrderCallBack(key: String, onQueryOrder: (MutableList<SoketOrderBean>) -> Unit, onSubscribeOrder: (order: SoketSubscribeOrderBean) -> Unit, mOnCurrentOrderCancel: ((rs: Boolean) -> Unit)) {
            webSoket.addCurrentOrderCallBack(key, onQueryOrder, onSubscribeOrder, mOnCurrentOrderCancel)
        }

        override fun queryCurrentOrder(pair: String, offset: Int, limit: Int, side: Int) {
            webSoket.queryCurrentOrder(pair, offset, limit,side)
        }

        override fun subscribeOrder(pair: String) {
            webSoket.subscribeOrder(pair)
        }

        private val webSoket: IWebSoket by lazy { WebSoketImpl() }

        override fun initSocket(key: String, marketUrl: String, onOpenCallback: (() -> Unit), onCloseCallback: (() -> Unit)) {
            webSoket.initSocket(key, marketUrl, onOpenCallback, onCloseCallback)
        }

        override fun addCallBack(
                key: String,
                mOnQueryKlineCallback: (kList: MutableList<KLineEntity>) -> Unit,
                mOnSubscribeKlineCallback: (newKList: MutableList<KLineEntity>) -> Unit,
                mOnTodayCallback: (todayBean: SoketTodayBean) -> Unit,
                mOnDepthCallback: (mDepthBuyList: MutableList<DepthDataBean>, mDepthSellList: MutableList<DepthDataBean>) -> Unit,
                mOnDealCallback: (dealList: MutableList<SoketDealBean>) -> Unit) {
            webSoket.addCallBack(
                    key,
                    mOnQueryKlineCallback,
                    mOnSubscribeKlineCallback,
                    mOnTodayCallback,
                    mOnDepthCallback,
                    mOnDealCallback)
        }

        override fun addRankCallBak(key: String, mOnQueryRankCallback: (rankList: MutableList<SoketRankBean>) -> Unit) {
            webSoket.addRankCallBak(key, mOnQueryRankCallback)
        }

        override fun addMarketCallBack(key: String, onMarketRankCallback: (marketList: MutableList<SoketMarketBean>) -> Unit) {
            webSoket.addMarketCallBack(key, onMarketRankCallback)
        }

        override fun addAuthCallBack(key: String, mOnAuthCallBack: (rs: Boolean) -> Unit) {
            webSoket.addAuthCallBack(key, mOnAuthCallBack)
        }


        override fun removeCallBack(key: String) {
            webSoket.removeCallBack(key)
        }

        override fun subscribeAllData(pair: String, type: Int, pattern: String) {
            webSoket.subscribeAllData(pair, type, pattern)
        }

        override fun subscribeDepthAndToday(amount: Int, depth: String, pair: String) {
            webSoket.subscribeDepthAndToday(amount, depth, pair)
        }

        override fun changeType(type: Int, pattern: String) {
            webSoket.changeType(type, pattern)
        }

        override fun queryRank() {
            webSoket.queryRank()
        }

        override fun queryMarket() {
            webSoket.queryMarket()
        }

        override fun auth(token: String) {
            webSoket.auth(token)
        }

        override fun close() {
            webSoket.close()
        }
    }


}