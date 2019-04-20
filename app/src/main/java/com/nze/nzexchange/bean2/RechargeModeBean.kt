package com.nze.nzexchange.bean2

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/20
 */
data class RechargeModeBean(
      val rechargeName:String,
      val mode:Int
) {

    companion object {
        val BANK=0
        val OSKO=1
        val BPAY=2


        fun getList()= mutableListOf<RechargeModeBean>().apply {
            add(RechargeModeBean("中国工商银行卡", BANK))
            add(RechargeModeBean("OSKO", OSKO))
            add(RechargeModeBean("BPAY", BPAY))
        }

    }
}