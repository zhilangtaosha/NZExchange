package com.nze.nzexchange.bean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/4
 */
class AllOrderFilterBean {
    var currency: String? = null//
    var mainCurrency: String? = null
    var tradeType: Int? = null//交易类型，0卖出，1买入，null两者都有即不做筛选
    var orderStatus: Int? = null//1003已成交，1004已撤销,null不做状态筛选
}