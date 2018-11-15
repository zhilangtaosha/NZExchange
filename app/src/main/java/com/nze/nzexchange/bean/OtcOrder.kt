package com.nze.nzexchange.bean

data class OtcOrder(var type: String,
                    var time: String,
                    var status: String,
                    var price: String,
                    var num: String,
                    var total: String,
                    var countDown: Int = 0) {

    companion object {
        fun getNoComplete(): MutableList<OtcOrder> = mutableListOf<OtcOrder>().apply {
            add(OtcOrder("卖出UCC", "06/18 12:08", "待放币", "1.6328", "3652.477", "4251.11", 60 * 60))
            add(OtcOrder("买入UCC", "06/18 12:08", "代付款", "1.6328", "3652.477", "4251.11", 60 * 60))

        }

        fun getComplete(): MutableList<OtcOrder> = mutableListOf<OtcOrder>().apply {
            add(OtcOrder("卖出UCC", "06/18 12:08", "已完成", "1.6328", "3652.477", "4251.11"))
            add(OtcOrder("买入UCC", "06/18 12:08", "已完成", "1.6328", "3652.477", "4251.11"))
        }


        fun getCancel(): MutableList<OtcOrder> = mutableListOf<OtcOrder>().apply {
            add(OtcOrder("卖出UCC", "06/18 12:08", "卖家取消", "1.6328", "3652.477", "4251.11"))
            add(OtcOrder("买入UCC", "06/18 12:08", "订单超时", "1.6328", "3652.477", "4251.11"))

        }

    }
}