package com.nze.nzexchange.controller.otc

import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding2.widget.RxTextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.R.id.et_num_value_ab
import com.nze.nzexchange.bean.OrderPoolBean
import com.nze.nzexchange.bean.SubOrderInfoBean.Companion.sellNet
import com.nze.nzexchange.bean.SubOrderInfoBean.Companion.submitNet
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.CurrencyTool
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.otc.main.OtcContentFragment
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.tools.DoubleMath
import com.nze.nzexchange.tools.ViewFactory
import com.nze.nzexchange.validation.EmptyValidation
import kotlinx.android.synthetic.main.activity_buy.*
import org.greenrobot.eventbus.EventBus

/**
 * 发布购买和出售委托单
 */
class BuyActivity : NBaseActivity(), View.OnClickListener {

    var type = OtcContentFragment.TYPE_BUY
    lateinit var orderPoolBean: OrderPoolBean
    private var price: Double = 0.0
    private var flag: Boolean = true
    private var userBean: UserBean? = UserBean.loadFromApp()

    override fun getRootView(): Int = R.layout.activity_buy

    override fun initView() {
        intent?.let {
            type = it.getIntExtra(OtcContentFragment.PARAM_TYPE, OtcContentFragment.TYPE_BUY)!!
            orderPoolBean = it.getParcelableExtra(IntentConstant.PARAM_ORDER_POOL)
        }
        if (type == OtcContentFragment.TYPE_BUY) {
            ctb_ab.setTitle("买入${CurrencyTool.getCurrency(orderPoolBean.tokenId)}")
        } else {
            ctb_ab.setTitle("卖出${CurrencyTool.getCurrency(orderPoolBean.tokenId)}")
        }

        orderPoolBean.run {
            tv_seller_ab.text = nick
            tv_order_num_abc.text = "${totalOrder}单"
            tv_price_value_ab.text = poolPrice.toString()
            tv_num_unit_ab.text = CurrencyTool.getCurrency(tokenId)
            tv_money_unit_ab.text = CurrencyTool.getCurrency(tokenId)

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
                .add(et_num_value_ab, EmptyValidation())
                .add(et_money_value_ab, EmptyValidation())
                .executeValidator()
                .setOnClickListener(this)

        RxTextView.textChanges(et_num_value_ab)
                .subscribe {
                    if (flag) {
                        flag = false
                        var value = ""
                        if (it.isNotEmpty() && price > 0.0)
                            value = DoubleMath.mul(it.toString().toDouble(), price).toString()
                        et_money_value_ab.setText(value)
                    } else {
                        flag = true
                    }
                }

        RxTextView.textChanges(et_money_value_ab)
                .subscribe {
                    if (flag) {
                        flag = false
                        var value = ""
                        if (it.isNotEmpty() && price > 0.0)
                            value = DoubleMath.div(it.toString().toDouble(), price).toString()
                        et_num_value_ab.setText(value)
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
                if (type == OtcContentFragment.TYPE_BUY) {
//                    skipActivity(BuyConfirmActivity::class.java)
                    orderPoolBean.run {
                        submitNet(poolId, userId, userBean?.userId!!, et_num_value_ab.getContent(), tokenId)
                                .compose(netTfWithDialog())
                                .subscribe({
                                    showToast(it.message)
                                    if (it.success) {
//                                        EventBus.getDefault().post(EventCenter)
                                        startActivity(Intent(this@BuyActivity, SaleConfirmActivity::class.java)
                                                .putExtra(OtcContentFragment.PARAM_TYPE, type)
                                                .putExtra(IntentConstant.PARAM_SUBORDERID, it.result.suborderId))
                                    }
                                }, onError, {
                                    this@BuyActivity.finish()
                                })
                    }
                } else {
                    orderPoolBean.run {
                        sellNet(poolId, userId, userBean?.userId!!, et_num_value_ab.getContent(), tokenId)
                                .compose(netTfWithDialog())
                                .subscribe({
                                    showToast(it.message)
                                    if (it.success) {
                                        EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_REFRESH_ASSET))
                                        startActivity(Intent(this@BuyActivity, SaleConfirmActivity::class.java)
                                                .putExtra(OtcContentFragment.PARAM_TYPE, type)
                                                .putExtra(IntentConstant.PARAM_SUBORDERID, it.result.suborderId))
                                    }
                                }, onError, {
                                    this@BuyActivity.finish()
                                })
                    }
                }
            }
            R.id.tv_all_ab -> {
                et_num_value_ab.setText(orderPoolBean.poolLeftamount.toString())
            }
            R.id.btn_cancle_ab -> this@BuyActivity.finish()
            else -> {
            }
        }
    }


}
