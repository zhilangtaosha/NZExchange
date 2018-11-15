package com.nze.nzexchange.bean

import com.nze.nzexchange.R

data class PayMethodBean(var iconId: Int,
                         var method: String,
                         var status: String) {

    companion object {
        fun getPayList(): MutableList<PayMethodBean> {
            return mutableListOf<PayMethodBean>().apply {
                add(PayMethodBean(R.mipmap.card_icon, "银行卡", "去设置"))
                add(PayMethodBean(R.mipmap.zhifubao_icon, "支付宝", "去设置"))
                add(PayMethodBean(R.mipmap.wechat_icon, "微信", "去设置"))
            }
        }
    }
}