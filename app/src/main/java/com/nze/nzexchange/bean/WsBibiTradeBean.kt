package com.nze.nzexchange.bean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:Websoket 币币交易结果返回
 * @创建时间：2019/8/5
 */
data class WsBibiTradeBean(
        val success: Boolean,
        val code: Int,
        val message: String
) {

    fun getMsg(): String {
        return when (code) {
            10 -> "余额不足"
            11 -> "交易数量太小"
            12 -> "没有足够交易量"
            else -> "交易失败"
        }
    }
}