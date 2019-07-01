package com.nze.nzexchange.config

class IntentConstant {

    companion object {
        const val PARAM_PAY: String = "pay"
        const val PARAM_ACCOUNT: String = "account"
        const val PARAM_TOKENID = "tokenId"
        const val PARAM_CURRENCY = "currency"
        const val PARAM_ORDER_POOL = "orderPool"
        const val PARAM_PLACE_AN_ORDER = "placeAnOrder"
        const val PARAM_SUBORDERID = "suborderId"
        const val PARAM_COUNTRY_NAME = "countryName"
        const val PARAM_COUNTRY_NUMBER = "countryNumber"
        const val PARAM_SELECT_CURRENCY = "selectType"
        const val PARAM_ASSET = "asset"
        const val PARAM_DETAIL = "detail"
        const val PARAM_TRANSACTION_PAIR = "transactionPair"
        const val PARAM_IMAGE_PATH = "imagePath"
        const val PARAM_VIDEO_PATH = "videoPath"
        const val PARAM_TYPE = "type"
        const val PARAM_AMOUNT = "amount"
        const val PARAM_CODE = "code"
        const val PARAM_OTC_ACCOUNT = "otcAccount"
        const val PARAM_BIBI_ACCOUNT = "bibAccount"
        const val PARAM_LEGAL_ACCOUNT = "legalAccount"
        const val PARAM_BUNDLE = "bundle"
        const val INTENT_REAL_NAME_BEAN = "realName"
        const val PARAM_URL = "url"
        const val PARAM_ADDRESS = "address"
        const val PARAM_AUTHENTICATION = "authentication"//实名验证参数
        const val PARAM_LIST="list"

        //设置支付方式
        const val TYPE_ZHIFUBAO = 0x001//支付宝
        const val TYPE_WECHAT = 0x002//微信

    }
}