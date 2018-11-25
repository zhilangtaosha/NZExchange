package com.nze.nzexchange.controller.otc

import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.SubOrderInfoBean.Companion.submitNet
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.setTextFromHtml
import com.nze.nzexchange.http.NRetrofit
import com.nze.nzexchange.http.Result
import com.nze.nzexchange.tools.DoubleMath
import com.nze.nzexchange.tools.TextTool
import com.nze.nzexchange.validation.EmptyValidation
import com.nze.nzexchange.widget.CommonTopBar
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.activity_publish.*
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

class PublishActivity : NBaseActivity(), View.OnClickListener {


    val TYPE_BUY: Int = 0
    val TYPE_SALE: Int = 1
    var currentType: Int = TYPE_BUY
    lateinit var topBar: CommonTopBar
    lateinit var tokenId: String
    lateinit var currency: String
    var flag: Boolean = true

    override fun getRootView(): Int = R.layout.activity_publish

    override fun initView() {
        intent?.let {
            tokenId = it.getStringExtra(IntentConstant.PARAM_TOKENID)
            currency = it.getStringExtra(IntentConstant.PARAM_CURRENCY)
        }
        topBar = ctb_ap
        topBar.setRightClick {
            if (currentType == TYPE_BUY) {
                currentType = TYPE_SALE
            } else {
                currentType = TYPE_BUY
            }
            changLayout()
        }
        tv_num_unit_ap.text = currency

        btn_ap.initValidator()
                .add(et_price_value_ap, EmptyValidation())
                .add(et_num_value_ap, EmptyValidation())
                .add(et_money_value_ap, EmptyValidation())
                .executeValidator()

        RxTextView.textChanges(et_price_value_ap)
                .subscribe {
                    if (flag) {
                        flag = false
                        var value = ""
                        var num = et_num_value_ap.text
                        if (it.isNotEmpty() && num.isNotEmpty())
                            value = DoubleMath.mul(it.toString().toDouble(), num.toString().toDouble()).toString()
                        et_money_value_ap.setText(value)
                    } else {
                        flag = true
                    }
                }

        RxTextView.textChanges(et_num_value_ap)
                .subscribe {
                    if (flag) {
                        flag = false
                        var value = ""
                        val price = et_price_value_ap.text.toString()
                        if (it.isNotEmpty() && price.isNotEmpty()) {
                            value = DoubleMath.mul(it.toString().toDouble(), price.toString().toDouble()).toString()
                        }
                        et_money_value_ap.setText(value)

                    } else {
                        flag = true
                    }
                }

        RxTextView.textChanges(et_money_value_ap)
                .subscribe {
                    if (flag) {
                        flag = false
                        var value = ""
                        val price = et_price_value_ap.text.toString()
                        if (it.isNotEmpty() && price.isNotEmpty()) {
                            value = DoubleMath.div(it.toString().toDouble(), price.toDouble()).toString()
                        }
                        et_num_value_ap.setText(value)
                    } else {
                        flag = true
                    }
                }

        RxView.clicks(btn_ap)
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe {
                    if (btn_ap.validate()) {
                        if (currentType == TYPE_SALE) {
                            submitNet(tokenId, NzeApp.instance.userId, et_num_value_ap.text.toString(), et_price_value_ap.text.toString(), et_message_ap.text.toString())
                                    .compose(netTfWithDialog())
                                    .subscribe({
                                        Toast.makeText(this@PublishActivity, it.message, Toast.LENGTH_SHORT).show()
                                        if (it.success) {
                                            this@PublishActivity.finish()
                                            EventBus.getDefault().post(EventCenter<Boolean>(EventCode.CODE_PULISH, true))
                                        }
                                    }, onError)
                        } else {
                            sellNet(tokenId, NzeApp.instance.userId, et_num_value_ap.text.toString(), et_price_value_ap.text.toString(), et_message_ap.text.toString())
                                    .compose(netTfWithDialog())
                                    .subscribe({
                                        Toast.makeText(this@PublishActivity, it.message, Toast.LENGTH_SHORT).show()
                                        if (it.success) {
                                            this@PublishActivity.finish()
                                            EventBus.getDefault().post(EventCenter<Boolean>(EventCode.CODE_PULISH, true))
                                        }
                                    }, onError)
                        }
                    }
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

    override fun onClick(v: View?) {
    }

    fun changLayout() {
        et_price_value_ap.text.clear()
        et_num_value_ap.text.clear()
        et_money_value_ap.text.clear()
        et_message_ap.text.clear()
        if (currentType == TYPE_BUY) {
            topBar.setTitle("购买委托单")
            topBar.setRightText("我要出售")
            tv_handicap_ap.setTextFromHtml("当前盘口价格 <font color=\"#09A085\">6.75CNY</font>")
            et_num_value_ap.hint = "请输入购买数量"
            et_message_ap.hint = "下单后极速付款，到账后请及时放币"


        } else {
            topBar.setTitle("出售委托单")
            topBar.setRightText("我要购买")
            tv_handicap_ap.setTextFromHtml("当前盘口价格 <font color=\"#FF4A5F\">6.75CNY</font>")
            et_num_value_ap.hint = "请输入购买数量"
            et_message_ap.hint = TextTool.fromHtml("1.订单有效期为15分钟，请及时付款并点击「我已支付」按钮<br/>" +
                    "2.币由系统锁定托管，请安心下单")
        }
    }

    fun submitNet(tokenId: String, userId: String, poolAllCount: String, poolPrice: String, remark: String): Flowable<Result<Boolean>> {
        return Flowable.defer {
            NRetrofit.instance
                    .buyService()
                    .pendingOrder(userId, tokenId, poolAllCount, poolPrice, remark)
        }
    }

    fun sellNet(tokenId: String, userId: String, poolAllCount: String, poolPrice: String, remark: String): Flowable<Result<Boolean>> {
        return Flowable.defer {
            NRetrofit.instance
                    .sellService()
                    .pendingOrder(userId, tokenId, poolAllCount, poolPrice, remark)
        }
    }
}
