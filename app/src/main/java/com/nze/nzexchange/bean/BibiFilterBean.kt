package com.nze.nzexchange.bean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/5
 */
data class BibiFilterBean(var currency: String, var isChoice: Boolean = false) {

    companion object {
        fun getList() = mutableListOf<BibiFilterBean>().apply {
            add(BibiFilterBean("USDT"))
            add(BibiFilterBean("BTC"))
            add(BibiFilterBean("ETH"))
            add(BibiFilterBean("EOS"))
            add(BibiFilterBean("LTC"))
            add(BibiFilterBean("BCH"))
        }
    }
}