package com.nze.nzexchange.bean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:法币提现费率
 * @创建时间：2019/5/20
 */
data class LegalFeeBean(
        val feeAmt: Any,
        val feeAmthigh: Double,
        val feeAmtlow: Double,
        val feeCurAmt: Any,
        val feeId: String,
        val feeMemo: Any,
        val feeName: String,
        val feeOrd: Any,
        val feeRate: Double,
        val feeRate1: Any,
        val feeReturn: Any,
        val feeType1: Any,
        val feeType2: Any
)