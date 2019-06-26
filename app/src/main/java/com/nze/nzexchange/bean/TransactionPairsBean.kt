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
        val currency: String,//交易货币
        val exchangeRate: Double,//汇率
        val id: String,
        var mainCurrency: String = "",//计价币种
        val remark: String?,
        val status: Int,//状态:0 初始化 1 测试阶段  2 准备阶段 3 激活  4 暂停 5 停用
        val transactionPair: String,//交易对
        val gain: Double,
        var optional: Int = 0,
        val volume: Double,
        val popular: Int,//热门交易对1001：热门，1002：非热门
        val deal: Double,
        val feePrec: Int,//计费费用小数位
        val stockPrec: Int,//交易币个数小数位数
        val moneyPrec: Int,//计价币价格小数位数
        val minAmount: Double,//最小交易数量
        val statusInitTime: Long,//初始化状态时间
        val statusUseTime: Long//激活使用状态时间
) : Parcelable {

    constructor() : this(0, "", 0.0, "", "", "", 0, "", 0.0, 0, 0.0, 0, 0.0, 0, 0, 0, 0.0, 0, 0) {}

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
    }
}