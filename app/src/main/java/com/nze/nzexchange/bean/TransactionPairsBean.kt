package com.nze.nzexchange.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable
import kotlinx.android.parcel.Parcelize

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/7
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class TransactionPairsBean(
        val createTime: Long,
        var currency: String,//交易货币
        var exchangeRate: Double,//汇率
        var id: String,
        var mainCurrency: String = "",//计价币种
        val remark: String?,
        val status: Int,//状态:0 初始化 1 测试阶段  2 准备阶段 3 激活  4 暂停 5 停用
        var transactionPair: String,//交易对
        var gain: Double,
        var optional: Int = 0,
        var volume: Double,
        val popular: Int,//热门交易对1001：热门，1002：非热门
        val deal: Double,
        val feePrec: Int,//计费费用小数位
        var stockPrec: Int,//交易币个数小数位数
        var moneyPrec: Int,//计价币价格小数位数
        var minAmount: Double,//最小交易数量
        val statusInitTime: Long,//初始化状态时间
        val statusUseTime: Long,//激活使用状态时间
        var cny: Double = 0.0
) : Parcelable {

    constructor() : this(0, "", 0.0, "", "", "", 0, "", 0.0, 0, 0.0, 0, 0.0, 0, 0, 0, 0.0, 0, 0, 0.0) {}

    fun getPair(): String {
        return "${currency}${mainCurrency}"
    }

    fun setValueFromRankBean(rankBean: SoketRankBean) {
        currency = rankBean.getCurrency()
        mainCurrency = rankBean.getMainCurrency()
        transactionPair = rankBean.market
        cny = rankBean.cny
        exchangeRate = rankBean.last
        gain = rankBean.change
        id = rankBean.market
        volume = rankBean.volume
        stockPrec = rankBean.stock_prec
        moneyPrec = rankBean.money_prec
        minAmount = rankBean.min_amount
    }


    companion object {
        fun getAllTransactionPairs(): Flowable<Result<MutableList<TransactionPairsBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .getAllTransactionPairs()
            }
        }

        fun getTransactionPairs(mainCurrency: String, userId: String?): Flowable<Result<MutableList<TransactionPairsBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .getTransactionPairs(mainCurrency, userId)
            }
        }


        fun getOptionalTransactionPair(userId: String): Flowable<Result<MutableList<TransactionPairsBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .getOptionalTransactionPair(userId)
            }
        }

        fun findTransactionPairs(currency: String): Flowable<Result<MutableList<TransactionPairsBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .findTransactionPairs(currency)
            }
        }

        fun marketPopular(userId: String? = null): Flowable<Result<MutableList<TransactionPairsBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .marketPopular(userId)
            }
        }

        fun getListFromRankList(rankList: MutableList<SoketRankBean>): MutableList<TransactionPairsBean> {
            val list: MutableList<TransactionPairsBean> = mutableListOf()
            rankList.forEach {
                val pairsBean = TransactionPairsBean()
                pairsBean.setValueFromRankBean(it)
                list.add(pairsBean)
            }
            return list
        }
    }
}