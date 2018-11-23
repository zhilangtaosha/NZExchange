package com.nze.nzexchange.controller.otc

import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.SubOrderInfoBean
import com.nze.nzexchange.config.Currency
import com.nze.nzexchange.config.CurrencyTool
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.tools.TimeTool
import com.nze.nzexchange.widget.CommonTopBar
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.activity_buy_confirm.*
import java.util.concurrent.TimeUnit

class SaleConfirmActivity : NBaseActivity() {
    var subOrderInfoBean: SubOrderInfoBean? = null
    var time: Long = 0

    override fun getRootView(): Int = R.layout.activity_buy_confirm

    override fun initView() {
        intent?.let {
            subOrderInfoBean = it.getParcelableExtra(IntentConstant.PARAM_PLACE_AN_ORDER)
        }
        (topbar_abc as CommonTopBar).setRightClick {

        }
        if (subOrderInfoBean != null) {
            subOrderInfoBean?.run {
                tv_order_no_abc.text = "订单编号:$poolId"
                tv_status_abc.text = if (transactionType == SubOrderInfoBean.TRANSACTIONTYPE_BUY) {
                    "待付款"
                } else {
                    "待收款"
                }
                tv_price_abc.text = "${suborderPrice}CNY"
                tv_num_abc.text = "${suborderNum}${CurrencyTool.getCurrency(tokenId)}"
                tv_money_abc.text = "${suborderAmount}CNY"

                time = (suborderOverTime - suborderCreateTime) / 1000

                accmoney
            }?.run {
                tv_name_abc.text = trueName
                tv_phone_abc.text = phone
            }

            Flowable.intervalRange(0, time, 0, 1, TimeUnit.SECONDS)
                    .compose(netTf())
                    .subscribe({
                        NLog.i(it.toString())
                        tv_tip_abc.text = "请在${TimeTool.formatTime(time - it)}内完成付款，并点击确认付款，超时将自动取消订单"
                    }, onError, {})

        } else {

        }


    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOverridePendingTransitionMode(): BaseActivity.TransitionMode = BaseActivity.TransitionMode.DEFAULT

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
