package com.nze.nzexchange.config

class IntentConstant {

    companion object {
        const val PARAM_TYPE: String = "type"
        const val PARAM_ACCOUNT: String = "account"

        //设置支付方式
        const val TYPE_ZHIFUBAO = 0x001//支付宝
        const val TYPE_WECHAT = 0x002//微信

    }
}