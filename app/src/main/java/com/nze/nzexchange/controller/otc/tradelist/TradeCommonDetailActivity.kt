package com.nze.nzexchange.controller.otc.tradelist

import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.SubOrderInfoBean
import com.nze.nzexchange.config.CurrencyTool
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.tools.TimeTool
import com.nze.nzexchange.tools.getNColor
import kotlinx.android.synthetic.main.activity_buy_confirm.*
import kotlinx.android.synthetic.main.activity_trade_complete_detail.*

class TradeCommonDetailActivity : NBaseActivity() {
    var type: Int = TradeCommonFragment.TYPE_COMPLETED
    var suborderId: String? = null
    lateinit var subOrderInfoBean: SubOrderInfoBean

    override fun getRootView(): Int = R.layout.activity_trade_complete_detail

    override fun initView() {
        intent?.let {
            type = it.getIntExtra(TradeCommonFragment.PARAM_TYPE, TradeCommonFragment.TYPE_COMPLETED)
            suborderId = it.getStringExtra(IntentConstant.PARAM_SUBORDERID)
        }

        if (type == TradeCommonFragment.TYPE_COMPLETED) {
            tv_status_atcd.setTextColor(getNColor(R.color.color_FF6D87A8))
            layout_complete_atcd.visibility = View.VISIBLE
        } else {
            tv_status_atcd.setTextColor(getNColor(R.color.color_FF7F8592))
            layout_complete_atcd.visibility = View.INVISIBLE

        }

        SubOrderInfoBean.findSubOrderInfoNet(NzeApp.instance.userId, suborderId!!)
                .compose(netTfWithDialog())
                .subscribe({
                    this.subOrderInfoBean = it.result
                    if (it.success)
                        refreshLayout()

                }, {
                    NLog.i("")
                })
    }

    fun refreshLayout() {
        subOrderInfoBean.run {
            tv_order_no_atcd.text = "订单编号:$suborderId"
            tv_status_atcd.text = SubOrderInfoBean.getStatus(suborderStatus)
            tv_price_atcd.text = "${suborderPrice}CNY"
            tv_num_atcd.text = "${suborderNum}${CurrencyTool.getCurrency(tokenId)}"
            tv_money_atcd.text = "${suborderAmount}CNY"
            tv_order_time_atcd.text = TimeTool.format(TimeTool.PATTERN_DEFAULT, suborderCreateTime)

            tv_pay_money_atcd.text = "${suborderAmount}CNY"
            tv_pay_time_atcd.text = TimeTool.format(TimeTool.PATTERN_DEFAULT, suborderPayTime)
            tv_pass_time_atcd.text = TimeTool.format(TimeTool.PATTERN_DEFAULT, suborderReleaseTime)

            accmoney
        }.run {
            tv_name_atcd.text = trueName
            tv_receive_atcd.text = trueName

        }
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOverridePendingTransitionMode(): TransitionMode = TransitionMode.DEFAULT

    override fun isBindEventBusHere(): Boolean = false

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkDisConnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContainerTargetView(): View? = null

}
