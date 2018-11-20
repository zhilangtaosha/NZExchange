package com.nze.nzexchange.bean

/**
 * 交易对
 */
data class TransactionPairBean(var name: String = "",
                               var change: Double = 0.0,
                               var exchange: Double = 0.0,
                               var cost: Double = 0.0,
                               var total24: Int = 0) {
    companion object {
        fun getList(): MutableList<TransactionPairBean> = mutableListOf<TransactionPairBean>().apply {
            for (i in 0..9) {
                val change: Double = if (i % 2 == 0) 0.1 else -0.2
                add(TransactionPairBean("CET/BTC", change, 823.97586, 3813.65, 213368))
            }
        }
    }
}