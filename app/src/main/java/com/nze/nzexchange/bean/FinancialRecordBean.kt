package com.nze.nzexchange.bean

import com.google.android.gms.common.internal.AccountType
import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable
import retrofit2.http.Query

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:财务记录，在资产币种详情页面中
 * @创建时间：2019/5/17
 */
data class FinancialRecordBean(
        val amount: Double,
        val createTime: Long,
        val fee: Double,
        //发起位置 1:legalCurrency / 法币  2:outside / 场外  3:coin / 币币
        val from: String,
        val id: Int,
        /**
         * 状态 1001:提币，1002:扣款成功，
         * 1003:发送提币请求成功，2001:充币到账通知，
         * 2002:充币到账确认，2004:充币到账取消，2005:参数错误 3001，审核中
         */
        val status: Int,
        val to: String,//转入地址
        val token: String,//,币种
        val type: Int,
        //1,类型 0:资产划转 / 1:区块链交易
        val userId: String,
        val txid: String
) {

    fun getType(currentType: String): String {
        var s = ""
        if (type == 0) {//划转
            return if (from == currentType) {
                "转到${
                when (to) {
                    ACCOUNT_LEGAL -> "法币账户"
                    ACCOUNT_OUTSIDE -> "OTC账户"
                    ACCOUNT_COIN -> "币币账户"
                    else -> ""
                }
                }"
            } else {
                "${
                when (from) {
                    ACCOUNT_LEGAL -> "法币账户"
                    ACCOUNT_OUTSIDE -> "OTC账户"
                    ACCOUNT_COIN -> "币币账户"
                    else -> ""
                }
                }转入"
            }
        } else {//提币和充币
            return when (status) {
                1001, 1002, 1003, 1004 -> {
                    "提币"
                }
                2001, 2002, 2004 -> {
                    "充币"
                }
                else -> ""
            }
        }
    }

    fun getStatus(): String {
        return if (type == 0) {
            "已完成"
        } else {
            when (status) {
                1001, 2001 -> "审核中"
                1002, 2002 -> "成功"
                1004, 2004 -> "失败"
                else -> "出错"
            }
        }
    }

    companion object {
        //账户类型
        val ACCOUNT_LEGAL = "legalCurrency"
        val ACCOUNT_OUTSIDE = "outside"
        val ACCOUNT_COIN = "coin"


        fun getFinancialRecord(
                userId: String,
                token: String,
                from: String,
                pageNumber: Int? = null,
                pageSize: Int? = null
        ): Flowable<Result<MutableList<FinancialRecordBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .bibiService()
                        .getFinancialRecord(userId, token, from, pageNumber, pageSize)
            }
        }
    }
}