package com.nze.nzexchange.controller.otc

import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.SubOrderInfoBean
import com.nze.nzexchange.config.CurrencyTool
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.http.NRetrofit
import com.nze.nzexchange.http.Result
import com.nze.nzexchange.tools.TimeTool
import com.nze.nzexchange.widget.CommonTopBar
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.activity_buy_confirm.*
import java.util.concurrent.TimeUnit

class SaleConfirmActivity : NBaseActivity() {
    var subOrderInfoBean: SubOrderInfoBean? = null
    var time: Long = 0
    var suborderId: String? = null

    override fun getRootView(): Int = R.layout.activity_buy_confirm

    override fun initView() {
        intent?.let {
            subOrderInfoBean = it.getParcelableExtra(IntentConstant.PARAM_PLACE_AN_ORDER)
            suborderId = it.getStringExtra(IntentConstant.PARAM_SUBORDERID)
        }
        (topbar_abc as CommonTopBar).setRightClick {

        }
        if (subOrderInfoBean != null) {
            refreshLayout()
        } else {
            SubOrderInfoBean.findSubOrderInfoNet(NzeApp.instance.userId, suborderId!!)
                    .compose(netTfWithDialog())
                    .subscribe({
                        this.subOrderInfoBean = it.result
                        refreshLayout()

                    }, onError)
        }

        RxView.clicks(btn_confirm_abc)
                .compose(this.bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe {
                    confirmReceiptNet(subOrderInfoBean?.userIdBu!!, subOrderInfoBean?.suborderId!!)
                            .compose(netTfWithDialog())
                            .subscribe({
                                if (it.success)
                                    this@SaleConfirmActivity.finish()
                            }, onError)
                }

        RxView.clicks(btn_cancle_abc)
                .compose(this.bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe {
                    cancelOrderNet(NzeApp.instance.userId, subOrderInfoBean?.suborderId!!)
                            .compose(netTf())
                            .subscribe({
                                if (it.success)
                                    this@SaleConfirmActivity.finish()
                            }, onError)
                }
    }

    fun refreshLayout() {
        subOrderInfoBean?.run {
            tv_order_no_abc.text = "订单编号:$suborderId"
            tv_status_abc.text = if (suborderStatus == SubOrderInfoBean.SUBORDERSTATUS_WAIT_PAY) {
                btn_confirm_abc.text = "确认付款"
                tv_message_abc.text = "请及时付款"
                "待付款"
            } else {
                btn_confirm_abc.visibility = View.INVISIBLE
                btn_cancle_abc.visibility = View.INVISIBLE
                tv_message_abc.text = "请及时放币"
                "待放币"
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

        if (time > 0)
            Flowable.intervalRange(0, time, 0, 1, TimeUnit.SECONDS)
                    .compose(netTf())
                    .subscribe({
                        NLog.i(it.toString())
                        tv_tip_abc.text = "请在${TimeTool.formatTime(time - it)}内完成付款，并点击确认付款，超时将自动取消订单"
                    }, onError, {})
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


    fun confirmReceiptNet(userIdBu: String, suborderId: String): Flowable<Result<Boolean>> {
        return Flowable.defer {
            NRetrofit.instance
                    .buyService()
                    .confirmReceipt(userIdBu, suborderId)
        }
    }

    fun cancelOrderNet(userId: String, orderId: String): Flowable<Result<Boolean>> {
        return Flowable.defer {
            NRetrofit.instance
                    .buyService()
                    .userCancelOrder(userId, orderId)
        }
    }

}
