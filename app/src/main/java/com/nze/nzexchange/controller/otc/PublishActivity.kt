package com.nze.nzexchange.controller.otc

import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.utils.EventCenter
import com.nze.nzexchange.Extend.setTextFromHtml
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.tools.TextTool
import com.nze.nzexchange.widget.CommonTopBar
import kotlinx.android.synthetic.main.activity_publish.*

class PublishActivity : NBaseActivity(), View.OnClickListener {


    val TYPE_BUY: Int = 0
    val TYPE_SALE: Int = 1
    var currentType: Int = TYPE_BUY
    lateinit var topBar: CommonTopBar

    override fun getRootView(): Int = R.layout.activity_publish

    override fun initView() {
        topBar = ctb_ap
        topBar.setRightClick {
            if (currentType == TYPE_BUY) {
                currentType = TYPE_SALE
            } else {
                currentType = TYPE_BUY
            }
            changLayout()
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
            topBar.setTitle("我要购买")
            topBar.setRightText("我要出售")
            tv_handicap_ap.setTextFromHtml("当前盘口价格 <font color=\"#09A085\">6.75CNY</font>")
            et_num_value_ap.hint = "请输入购买数量"
            et_message_ap.hint = "下单后极速付款，到账后请及时放币"

        } else {
            topBar.setTitle("出售委托单")
            topBar.setRightText("出售委托单")
            tv_handicap_ap.setTextFromHtml("当前盘口价格 <font color=\"#FF4A5F\">6.75CNY</font>")
            et_num_value_ap.hint = "请输入购买数量"
            et_message_ap.hint = TextTool.fromHtml("1.订单有效期为15分钟，请及时付款并点击「我已支付」按钮<br/>" +
                    "2.币由系统锁定托管，请安心下单")
        }
    }
}
