package com.nze.nzexchange.bean

/**
 * 交易对
 */
data class TransactionPairBean(var name: String = "",
                               var change: Double = 0.0,
                               var exchange: Double = 0.0,
                               var cost: Double = 0.0,
                               var total24: Int = 0)