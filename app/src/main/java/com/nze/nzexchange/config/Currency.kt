package com.nze.nzexchange.config

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:数字货币类型
 * @创建时间：2018/11/22
 */
enum class Currency(var tokenId: String, var type: String) {
    ETH("token02", "ETH"),
    BTC("token01", "BTC"),
    EOS("token03", "EOS"),
    USDT("token04", "USDT")

}

class CurrencyTool {
    companion object {

        fun getCurrency(tokenId: String): String = when (tokenId) {
            Currency.ETH.tokenId -> Currency.ETH.type
            Currency.BTC.tokenId -> Currency.BTC.type
            Currency.EOS.tokenId -> Currency.EOS.type
            Currency.USDT.tokenId -> Currency.USDT.type
            else -> Currency.ETH.type
        }
    }
}