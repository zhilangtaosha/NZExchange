package com.nze.nzexchange.controller.market.presenter

import com.nze.nzexchange.bean.SoketDealBean
import com.nze.nzexchange.bean.SoketMarketBean
import com.nze.nzexchange.bean.SoketRankBean
import com.nze.nzexchange.bean.SoketTodayBean
import com.nze.nzexchange.config.KLineParam
import com.nze.nzexchange.widget.chart.KLineEntity
import com.nze.nzexchange.widget.depth.DepthDataBean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/6/13
 */
interface IWebSoket {

    fun initSocket(key: String, marketUrl: String, onOpenCallback: (() -> Unit), onCloseCallback: (() -> Unit))

    fun addCallBack(key: String,
                    mOnQueryKlineCallback: ((kList: MutableList<KLineEntity>) -> Unit),
                    mOnSubscribeKlineCallback: ((newKList: MutableList<KLineEntity>) -> Unit),
                    mOnTodayCallback: ((todayBean: SoketTodayBean) -> Unit),
                    mOnDepthCallback: ((mDepthBuyList: MutableList<DepthDataBean>, mDepthSellList: MutableList<DepthDataBean>) -> Unit),
                    mOnDealCallback: ((dealList: MutableList<SoketDealBean>) -> Unit)
    )

    fun addRankCallBak(key: String, mOnQueryRankCallback: ((rankList: MutableList<SoketRankBean>) -> Unit))

    fun addMarketCallBack(key: String, onMarketRankCallback: ((marketList: MutableList<SoketMarketBean>) -> Unit))

    fun removeCallBack(key: String)

    fun subscribeAllData(pair: String, type: Int, pattern: String)

    fun changeType(type: Int, pattern: String)

    fun subscribeDepthAndToday(amount: Int = KLineParam.AMOUNT_DEPTH_10, depth: String = KLineParam.DEPTH_8, pair: String)

    fun queryRank()

    fun queryMarket()

    fun close()
}