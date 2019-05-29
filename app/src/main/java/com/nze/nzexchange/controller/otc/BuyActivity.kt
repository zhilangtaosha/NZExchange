package com.nze.nzexchange.controller.otc

import android.content.Intent
import android.view.View
import android.widget.EditText
import com.jakewharton.rxbinding2.widget.RxTextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.OrderPoolBean
import com.nze.nzexchange.bean.SubOrderInfoBean.Companion.sellNet
import com.nze.nzexchange.bean.SubOrderInfoBean.Companion.submitNet
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.CurrencyTool
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.AuthorityDialog
import com.nze.nzexchange.controller.common.FundPasswordPopup
import com.nze.nzexchange.controller.otc.main.OtcContentFragment
import com.nze.nzexchange.extend.*
import com.nze.nzexchange.tools.DoubleMath
import com.nze.nzexchange.tools.ViewFactory
import com.nze.nzexchange.tools.editjudge.EditCurrencyWatcher
import com.nze.nzexchange.tools.editjudge.EditLegalWatcher
import com.nze.nzexchange.validation.EmptyValidation
import kotlinx.android.synthetic.main.activity_buy.*
import org.greenrobot.eventbus.EventBus

/**
 * OTC购买和出售
 */
class BuyActivity : NBaseActivity(), View.OnClickListener {
    val numEt: EditText by lazy {
        et_num_value_ab.apply {
            addTextChangedListener(EditCurrencyWatcher(this))
        }
    }
    val moneyEt: EditText by lazy {
        et_money_value_ab.apply {
            addTextChangedListener(EditLegalWatcher(this))
        }
    }
    var type = OtcContentFragment.TYPE_BUY
    lateinit var orderPoolBean: OrderPoolBean
    private var price: Double = 0.0
    private var flag: Boolean = true
    private var userBean: UserBean? = UserBean.loadFromApp()
    val fundPopup: FundPasswordPopup by lazy {
        FundPasswordPopup(this).apply {
            onPasswordClick = {
                orderPoolBean.run {
                    sellNet(poolId, userId, userBean?.userId!!, numEt.getContent(), tokenId, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey, it)
                            .compose(netTfWithDialog())
                            .subscribe({
                                if (it.success) {
                                    EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_REFRESH_ASSET))
                                    startActivity(Intent(this@BuyActivity, OtcConfirmActivity::class.java)
                                            .putExtra(OtcContentFragment.PARAM_TYPE, type)
                                            .putExtra(IntentConstant.PARAM_SUBORDERID, it.result.suborderId))
                                    finish()
                                } else {
                                    showToast(it.message)
                                }
                            }, {
                                this@BuyActivity.finish()
                            })
                }
            }
        }
    }


    override fun getRootView(): Int = R.layout.activity_buy

    override fun initView() {
        intent?.let {
            type = it.getIntExtra(OtcContentFragment.PARAM_TYPE, OtcContentFragment.TYPE_BUY)!!
            orderPoolBean = it.getParcelableExtra(IntentConstant.PARAM_ORDER_POOL)
        }
        if (type == OtcContentFragment.TYPE_BUY) {
            ctb_ab.setTitle("买入${CurrencyTool.getCurrency(orderPoolBean.tokenId)}")
            tv_all_ab.text = "全部买入"
        } else {
            ctb_ab.setTitle("卖出${CurrencyTool.getCurrency(orderPoolBean.tokenId)}")
            tv_all_ab.text = "全部卖出"
        }

        orderPoolBean.run {
            tv_seller_ab.text = nick
            tv_order_num_abc.text = "${totalOrder}单"
            tv_price_value_ab.text = poolPrice.toString()
            tv_num_unit_ab.text = CurrencyTool.getCurrency(tokenId)

            price = poolPrice
            accmoney
        }?.run {
            if (accmoneyWeixinurl != null && accmoneyWeixinurl.isNotEmpty())
                layout_pay_ab.addView(ViewFactory.createRightPayMethod(R.mipmap.wechat_icon))
            if (accmoneyZfburl != null && accmoneyZfburl.isNotEmpty())
                layout_pay_ab.addView(ViewFactory.createRightPayMethod(R.mipmap.zhifubao_icon))
            if (accmoneyBankcard != null && accmoneyBankcard.isNotEmpty())
                layout_pay_ab.addView(ViewFactory.createRightPayMethod(R.mipmap.card_icon))
        }


        btn_confirm_ab.initValidator()
                .add(numEt, EmptyValidation())
                .add(moneyEt, EmptyValidation())
                .executeValidator()
                .setOnClickListener(this)

        RxTextView.textChanges(numEt)
                .subscribe {
                    if (flag) {
                        flag = false
                        var value = 0.0
                        if (it.isNotEmpty() && price > 0.0) {
                            val amount = it.toString().toDouble()
                            value = DoubleMath.mul(amount, price)
//                            if (amount > orderPoolBean.poolLeftamount)
//                                showToast("交易数量超过委托数量")

//                            if (value <= 0.0)
//                                showToast("交易金额不能为0")

                        }
                        moneyEt.setText(value.formatForLegal())
                    } else {
                        flag = true
                    }
                }

        RxTextView.textChanges(moneyEt)
                .subscribe {
                    if (flag) {
                        flag = false
                        var value = ""
                        if (it.isNotEmpty() && price > 0.0)
                            value = DoubleMath.divByFloor(it.toString().toDouble(), price, 8).formatForCurrency()
                        numEt.setText(value)
                    } else {
                        flag = true
                    }
                }

        tv_all_ab.setOnClickListener(this)
        btn_cancle_ab.setOnClickListener(this)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_confirm_ab -> {
                val amount = numEt.getContent().toDouble()
                if (amount > orderPoolBean.poolLeftamount) {
                    showToast("交易数量超过委托数量")
                    return
                }
                val value = DoubleMath.mul(amount, price).formatForLegal2()
                if (value < 400) {
                    showToast("最小交易金额为400")
                    return
                }

                if (type == OtcContentFragment.TYPE_BUY) {
//                    skipActivity(SaleConfirmActivity::class.java)
                    orderPoolBean.run {
                        submitNet(poolId, userId, userBean?.userId!!, numEt.getContent(), tokenId, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                                .compose(netTfWithDialog())
                                .subscribe({
                                    if (it.success) {
//                                        EventBus.getDefault().post(EventCenter)
                                        startActivity(Intent(this@BuyActivity, OtcConfirmActivity::class.java)
                                                .putExtra(OtcContentFragment.PARAM_TYPE, type)
                                                .putExtra(IntentConstant.PARAM_SUBORDERID, it.result.suborderId))
                                        finish()
                                    } else {
                                        if (it.isCauseNotEmpty()) {
                                            AuthorityDialog.getInstance(this@BuyActivity)
                                                    .show("进行OTC交易需要完成以下设置，请检查",
                                                            it.cause) {
                                                        finish()
                                                    }
                                        }
                                    }
                                }, {
                                    this@BuyActivity.finish()
                                })
                    }
                } else {//卖
                    fundPopup.showPopupWindow()

                }
            }
            R.id.tv_all_ab -> {
                numEt.setText(orderPoolBean.poolLeftamount.formatForCurrency())
            }
            R.id.btn_cancle_ab -> this@BuyActivity.finish()
            else -> {
            }
        }
    }


}
