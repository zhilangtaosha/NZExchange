package com.nze.nzexchange.config

class IntentConstant {

    companion object {
        const val PARAM_PAY: String = "pay"
        const val PARAM_ACCOUNT: String = "account"
        const val PARAM_TOKENID = "tokenId"
        const val PARAM_CURRENCY = "currency"
        const val PARAM_ORDER_POOL = "orderPool"
        const val PARAM_PLACE_AN_ORDER = "placeAnOrder"

        //设置支付方式
        const val TYPE_ZHIFUBAO = 0x001//支付宝
        const val TYPE_WECHAT = 0x002//微信

    }
}