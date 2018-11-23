package com.nze.nzexchange.controller.otc

import android.content.Intent
import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding2.widget.RxTextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.OrderPoolBean
import com.nze.nzexchange.bean.SubOrderInfoBean.Companion.submitNet
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.otc.main.OtcContentFragment
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.tools.DoubleMath
import com.nze.nzexchange.tools.ViewFactory
import com.nze.nzexchange.validation.EmptyValidation
import kotlinx.android.synthetic.main.activity_buy.*

class BuyActivity : NBaseActivity(), View.OnClickListener {

    var type = OtcContentFragment.TYPE_BUY
    lateinit var orderPoolBean: OrderPoolBean
    private var price: Double = 0.0
    private var flag: Boolean = true


    override fun getRootView(): Int = R.layout.activity_buy

    override fun initView() {
        intent?.let {
            type = it.getIntExtra(OtcContentFragment.PARAM_TYPE, OtcContentFragment.TYPE_BUY)!!
            orderPoolBean = it.getParcelableExtra(IntentConstant.PARAM_ORDER_POOL)
        }
        if (type == OtcContentFragment.TYPE_BUY) {
            ctb_ab.setTitle("买入UCC")
        } else {
            ctb_ab.setTitle("买出UCC")
        }

        orderPoolBean.run {
            tv_seller_ab.text = nick
            tv_order_num_abc.text = "${totalOrder}单"
            tv_price_value_ab.text = poolPrice.toString()
            price = poolPrice
            accmoney
        }.run {
            if (accmoneyWeixinurl.isNotEmpty())
                layout_pay_ab.addView(ViewFactory.createRightPayMethod(R.mipmap.wechat_icon))
            if (accmoneyZfburl.isNotEmpty())
                layout_pay_ab.addView(ViewFactory.createRightPayMethod(R.mipmap.zhifubao_icon))
            if (accmoneyBankcard.isNotEmpty())
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_confirm_ab -> {
                if (type == OtcContentFragment.TYPE_BUY) {
//                    skipActivity(BuyConfirmActivity::class.java)
                    orderPoolBean.run {
                        submitNet(poolId, userId, NzeApp.instance.userId, et_num_value_ab.getContent(), tokenId)
                                .compose(netTfWithDialog())
                                .subscribe({
                                    showToast(it.message)
                                    if (it.success) {
                                        startActivity(Intent(this@BuyActivity, SaleConfirmActivity::class.java)
                                                .putExtra(IntentConstant.PARAM_PLACE_AN_ORDER, it.result))
                                    }
                                }, onError,{
                                    this@BuyActivity.finish()
                                })
                    }
                } else {
                    skipActivity(SaleConfirmActivity::class.java)
                    Toast.makeText(this, "sale...", Toast.LENGTH_SHORT).show()
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
