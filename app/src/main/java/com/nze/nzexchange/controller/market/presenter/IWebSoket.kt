package com.nze.nzexchange.controller.market.presenter

import com.nze.nzexchange.bean.SoketDealBean
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
    var mOnTodayCallback: ((todayBean: SoketTodayBean) -> Unit)?
    var mOnDepthCallback: ((mDepthBuyList: MutableList<DepthDataBean>, mDepthSellList: MutableList<DepthDataBean>) -> Unit)?
    var mOnDealCallback: ((dealList: MutableList<SoketDealBean>) -> Unit)?
    var mOnQueryKlineCallback: ((kList: MutableList<KLineEntity>) -> Unit)?
    var mOnSubscribeKlineCallback: ((newKList: MutableList<KLineEntity>) -> Unit)?

    fun initSocket(
            pair: String,
            marketUrl: String,
            mOnQueryKlineCallback: ((kList: MutableList<KLineEntity>) -> Unit),
            mOnSubscribeKlineCallback: ((newKList: MutableList<KLineEntity>) -> Unit),
            mOnTodayCallback: ((todayBean: SoketTodayBean) -> Unit),
            mOnDepthCallback: ((mDepthBuyList: MutableList<DepthDataBean>, mDepthSellList: MutableList<DepthDataBean>) -> Unit),
            mOnDealCallback: ((dealList: MutableList<SoketDealBean>) -> Unit)
    )

    fun initCallBack(
            mOnQueryKlineCallback: ((kList: MutableList<KLineEntity>) -> Unit),
            mOnSubscribeKlineCallback: ((newKList: MutableList<KLineEntity>) -> Unit),
            mOnTodayCallback: ((todayBean: SoketTodayBean) -> Unit),
            mOnDepthCallback: ((mDepthBuyList: MutableList<DepthDataBean>, mDepthSellList: MutableList<DepthDataBean>) -> Unit),
            mOnDealCallback: ((dealList: MutableList<SoketDealBean>) -> Unit)
    )

    fun subscribeAllData(type: Int, pattern: String)

    fun changeType(type: Int, pattern: String)

    fun subscribeDepthAndToday(amount: Int = KLineParam.AMOUNT_DEPTH_10, depth: String = KLineParam.DEPTH_8, pair: String)

    fun close()
}