package com.nze.nzexchange.controller.common

import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.BusFlowTag
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.http.CommonRequest

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:根据业务简码判断权限
 * 发布广告：需要有输入资金密码的界面
OTC卖出：交币前需要有输入资金密码的界面
提币：需要有输入资金密码的界面
币币交易：需要有输入资金密码的界面
法币体现： 需要有输入资金密码的界面
 * @创建时间：2019/5/16
 */
class CheckPermission {

    companion object {
        val CURRENCY_RECHARGE = "biacc_fill"//充币
        val SET_PAY_METHOD = "me_accmoney"//设置收款方式
        val OTC_SEND_ADVERT = "otc_send_advert"//OTC发布广告
        val OTC_COMM_TRADE_SELL = " otc_comm_trade_sell"//OTC买入
        val OTC_COMM_TRADE_BUY = "otc_comm_trade_buy"//OTC卖出
        val BIACC_GET = "biacc_get"//提币
        val BIBI_TRADE = "bibi_trade"//币币交易
        val ACC_PICKFUND = "acc_pickFund"//法币提现


        fun getInstance(): CheckPermission {
            return CheckPermission()
        }
    }


    fun commonCheck(act: NBaseActivity, busTag: String, onPass: (() -> Unit)? = null, onReject: (() -> Unit)? = null) {
        CommonRequest.busCheck(UserBean.loadFromApp()!!, BusFlowTag.SET_PAY_METHOD)
                .compose(act.netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        onPass?.invoke()
                    } else {
                        if (it.isCauseNotEmpty())
                            AuthorityDialog.getInstance(act)
                                    .show("设置收款方式需要完成以下设置，请检查",
                                            it.cause) {
                                    }
                        onReject?.invoke()
                    }
                }, {})
    }

}