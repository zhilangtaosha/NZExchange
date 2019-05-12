package com.nze.nzexchange.controller.otc

import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.SubOrderInfoBean
import com.nze.nzexchange.config.CurrencyTool
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.http.NRetrofit
import com.nze.nzexchange.bean.Result
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.controller.common.ShowBankPayMethodActivity
import com.nze.nzexchange.controller.common.ShowImagePayMethodActivity
import com.nze.nzexchange.http.CRetrofit
import com.nze.nzexchange.tools.TimeTool
import com.nze.nzexchange.tools.ViewFactory
import com.nze.nzexchange.widget.CommonTopBar
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.activity_buy_confirm.*
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

class SaleConfirmActivity : NBaseActivity() {
    var subOrderInfoBean: SubOrderInfoBean? = null
    var time: Long = 0
    var suborderId: String? = null
    private var userBean: UserBean? = UserBean.loadFromApp()
    override fun getRootView(): Int = R.layout.activity_buy_confirm

    override fun initView() {
        intent?.let {
            suborderId = it.getStringExtra(IntentConstant.PARAM_SUBORDERID)
        }
        (topbar_abc as CommonTopBar).setRightClick {

        }
        if (subOrderInfoBean != null) {
            refreshLayout()
        } else {
            SubOrderInfoBean.findSubOrderInfoNet(userBean?.userId!!, suborderId!!, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
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
                    confirmPay(subOrderInfoBean!!)
                }

        RxView.clicks(btn_cancle_abc)
                .compose(this.bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe {
                    cancelNet(subOrderInfoBean!!)
                }
    }

    fun refreshLayout() {
        subOrderInfoBean?.run {
            tv_order_no_abc.text = "订单编号:$suborderId"
            tv_status_abc.text = if (suborderStatus == SubOrderInfoBean.SUBORDERSTATUS_WAIT_PAY) {
//                btn_confirm_abc.text = "确认付款"
//                tv_message_abc.text = "请及时付款"
                "待付款"
            } else {

                tv_tip_abc.visibility = View.INVISIBLE
                tv_message_abc.text = "请及时放币"
                "待放币"
            }
            var type = ""
            var titleContent = ""
            if (userBean?.userId!! == userIdSell) {//商家
                type = if (transactionType == SubOrderInfoBean.TRANSACTIONTYPE_BUY) {//卖币
                    btn_cancle_abc.visibility = View.INVISIBLE
                    titleContent = "卖出${CurrencyTool.getCurrency(tokenId)}"
                    "确认收款"
                } else {//买币
                    if (suborderStatus == SubOrderInfoBean.SUBORDERSTATUS_WAIT_RELEASE) {
                        btn_confirm_abc.visibility = View.INVISIBLE
                        btn_cancle_abc.visibility = View.INVISIBLE
                    }
                    titleContent = "买入 ${CurrencyTool.getCurrency(tokenId)}"
                    "确认付款"
                }
            } else {//用户
                type = if (transactionType == SubOrderInfoBean.TRANSACTIONTYPE_BUY) {//买币
                    if (suborderStatus == SubOrderInfoBean.SUBORDERSTATUS_WAIT_RELEASE) {
                        btn_confirm_abc.visibility = View.INVISIBLE
                        btn_cancle_abc.visibility = View.INVISIBLE
                    }
                    titleContent = "买入 ${CurrencyTool.getCurrency(tokenId)}"
                    "确认付款"
                } else {//卖币
                    btn_cancle_abc.visibility = View.INVISIBLE
                    titleContent = "卖出${CurrencyTool.getCurrency(tokenId)}"
                    "确认收款"
                }
            }

            btn_confirm_abc.text = type
            topbar_abc.setTitle(titleContent)


            tv_price_abc.text = "${suborderPrice}CNY"
            tv_num_abc.text = "${suborderNum}${CurrencyTool.getCurrency(tokenId)}"
            tv_money_abc.text = "${suborderAmount}CNY"
            tv_message_abc.text = remark
            time = suborderOverTime

            accmoney
        }?.run {
            tv_name_abc.text = trueName
            tv_phone_abc.text = phone
            if (accmoneyWeixinurl != null && accmoneyWeixinurl.isNotEmpty()) {
                val wechat = ViewFactory.createPayMehod(R.layout.tv_paymethod_wechat)
                wechat.setOnClickListener {
                    ShowImagePayMethodActivity.skip(this@SaleConfirmActivity, CRetrofit.imageUrlJoin(accmoneyWeixinurl))
                }
                layout_pay_abc.addView(wechat)
            }
            if (accmoneyZfburl != null && accmoneyZfburl.isNotEmpty()) {
                val zfb = ViewFactory.createPayMehod(R.layout.tv_paymethod_zhifubao)
                layout_pay_abc.addView(zfb)
                zfb.setOnClickListener {
                    ShowImagePayMethodActivity.skip(this@SaleConfirmActivity, CRetrofit.imageUrlJoin(accmoneyZfburl))
                }
            }
            if (accmoneyBankcard != null && accmoneyBankcard.isNotEmpty()) {
                val bank = ViewFactory.createPayMehod(R.layout.tv_paymethod_bank)
                layout_pay_abc.addView(bank)
                bank.setOnClickListener {
                    ShowBankPayMethodActivity.skip(this@SaleConfirmActivity, this)
                }
            }
        }

        if (time > 0)
            Flowable.intervalRange(0, time, 0, 1, TimeUnit.SECONDS)
                    .compose(netTf())
                    .subscribe({
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


    //确认付款
    fun confirmPay(subOrderInfoBean: SubOrderInfoBean) {
        if (userBean?.userId!! == subOrderInfoBean.userIdSell) {//本人是商家
            if (subOrderInfoBean.transactionType == SubOrderInfoBean.TRANSACTIONTYPE_BUY) {//买币
                //商家确认收款
                NRetrofit.instance
                        .buyService()
                        .confirmPayment(subOrderInfoBean.userIdSell, subOrderInfoBean.suborderId, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                        .compose(netTf())
                        .subscribe({
                            confirm(it)
                        }, onError)
            } else {//卖币
                //商家确认付款
                NRetrofit.instance
                        .sellService()
                        .confirmReceipt(subOrderInfoBean.userIdSell, subOrderInfoBean.suborderId, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                        .compose(netTf())
                        .subscribe({
                            confirm(it)
                        }, onError)
            }
        } else {//本人是用户
            if (subOrderInfoBean.transactionType == SubOrderInfoBean.TRANSACTIONTYPE_BUY) {//买币
                //确认付款
                NRetrofit.instance
                        .buyService()
                        .confirmReceipt(subOrderInfoBean.userIdBu, subOrderInfoBean.suborderId, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                        .compose(netTf())
                        .subscribe({
                            confirm(it)
                        }, onError)
            } else {//卖币
                //确认收款
                NRetrofit.instance
                        .sellService()
                        .confirmPayment(subOrderInfoBean.userIdBu, subOrderInfoBean.suborderId, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                        .compose(netTf())
                        .subscribe({
                            confirm(it)
                        }, onError)
            }
        }
    }

    fun confirm(rs: Result<Boolean>) {
        showToast(rs.message)
        if (rs.success) {
            this@SaleConfirmActivity.finish()
            EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_CONFIRM_PAY))
            EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_REFRESH_ASSET))
        }
    }


    fun cancelNet(subOrderInfoBean: SubOrderInfoBean) {
//        if (NzeApp.instance.userId == subOrderInfoBean.userIdSell) {//本人是商家
//            NRetrofit.instance
//                    .sellService()
//                    .userCancelOrder(NzeApp.instance.userId, subOrderInfoBean.suborderId)
//                    .compose(netTf())
//                    .subscribe({
//                        cancel(it)
//                    }, onError)
//        } else {//本人是用户
        NRetrofit.instance
                .buyService()
                .userCancelOrder(userBean?.userId!!, subOrderInfoBean.suborderId, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                .compose(netTf())
                .subscribe({
                    cancel(it)
                }, onError)
//        }
    }

    fun cancel(rs: Result<Boolean>) {
        showToast(rs.message)
        if (rs.success)
            this@SaleConfirmActivity.finish()
    }
}
