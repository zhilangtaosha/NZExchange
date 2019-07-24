package com.nze.nzexchange.controller.market.presenter

import com.nze.nzexchange.bean.*
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

    fun addAuthCallBack(key: String, mOnAuthCallBack: (rs: Boolean) -> Unit)

    fun addCurrentOrderCallBack(key: String, onQueryOrder: (MutableList<SoketOrderBean>) -> Unit, onSubscribeOrder: (order: SoketSubscribeOrderBean) -> Unit, mOnCurrentOrderCancel: ((rs: Boolean) -> Unit))

    fun addLimitDealCallBack(onLimitDeal: (rs: Boolean) -> Unit)
    fun addMarketDealCallBack(onMarketDeal: (rs: Boolean) -> Unit)
    fun addHistoryOrderCallBack(onQueryOrder: (MutableList<SoketOrderBean>) -> Unit)
    fun addAssetCallBack(key: String, queryCallBack: ((assetMap: HashMap<String, SoketAssetBean>) -> Unit), subscribeCallBack: ((assetMap: HashMap<String, SoketAssetBean>) -> Unit))

    fun removeCallBack(key: String)

    fun removeCallBack2()

    fun subscribeAllData(pair: String, type: Int, pattern: String)

    fun changeType(type: Int, pattern: String)

    fun subscribeDepthAndToday(amount: Int = KLineParam.AMOUNT_DEPTH_10, depth: String = KLineParam.DEPTH_8, pair: String)

    fun queryRank()

    fun queryMarket()
    /**
     * 身份认证
     */
    fun auth(token: String)

    /**
     * 查询当前订单
     */
    fun queryCurrentOrder(pair: String, offset: Int, limit: Int, side: Int)

    fun queryHistoryOrder(pair: String, startTime: Long, endTime: Long, offset: Int, limit: Int, side: Int)

    /**
     * 订阅订单
     */
    fun subscribeOrder(pair: String)

    /**
     * 挂限价单
    参数1：market，交易对名称
    参数2：side，购买或者出售。取值范围：1为出售，2为购买。
    参数3：amount，交易数量
    参数4：price，单价
    参数5：source，请求源，30字节。由客户端APP和WEB填写，撮合系统在请求响应和订单成交通知消息中原样返回，APP和WEB可以用于匹配。
     */
    fun limitDeal(pair: String, side: Int, amount: Double, price: Double)

    /**
     * 挂市价单
     * 市价不需要提交价格
     */
    fun marketDeal(pair: String, side: Int, amount: Double)

    /**
     * 取消订单
     */
    fun orderCancel(pair: String, id: Long)

    /**
     *查询资产
     */
    fun queryAsset(list: List<String>)

    /**
     * 订阅资产
     */
    fun subscribeAsset(list: List<String>)

    fun close()
}