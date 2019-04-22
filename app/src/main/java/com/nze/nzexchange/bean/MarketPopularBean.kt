package com.nze.nzexchange.bean

import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:首页热门交易对
 * @创建时间：2019/4/22
 */
data class MarketPopularBean(
        val createTime: Long,//创建时间
        val currency: String,//交易币种
        val exchangeRate: Double,//汇率
        val gain: Double,//涨跌幅
        val id: String,
        val mainCurrency: String,//,计价币种
        val popular: Int,//热门交易对状态
        val remark: String,//备注信息
        val status: Int,//状态
        val transactionPair: String//交易对名称
) {
    companion object {

        fun marketPopular(): Flowable<Result<MutableList<MarketPopularBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .marketPopular()
            }
        }
    }
}