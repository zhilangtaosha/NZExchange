package com.nze.nzexchange.controller.otc

import android.view.View
import android.widget.*
import com.jakewharton.rxbinding2.view.RxView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzexchange.R
import com.nze.nzexchange.R.id.*
import com.nze.nzexchange.bean.SubOrderInfoBean
import com.nze.nzexchange.config.CurrencyTool
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.http.NRetrofit
import com.nze.nzexchange.bean.Result
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.controller.common.AuthorityDialog
import com.nze.nzexchange.controller.common.ShowBankPayMethodActivity
import com.nze.nzexchange.controller.common.ShowImagePayMethodActivity
import com.nze.nzexchange.extend.setBg
import com.nze.nzexchange.extend.setBgByColor
import com.nze.nzexchange.extend.setTxtColor
import com.nze.nzexchange.tools.TimeTool
import com.nze.nzexchange.tools.ViewFactory
import com.nze.nzexchange.widget.CommonTopBar
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.activity_buy_confirm.*
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

/**
 * OTC交易订单的各种状态页面
 * 待付款
 * 待放币
 * 取消
 * 交易成功
 */
class OtcConfirmActivity : NBaseActivity() {


    private val orderNo: TextView by lazy { tv_order_no_abc }//订单编号
    private val copyIv: ImageView by lazy { iv_copy_abc }//复制按钮
    private val statusTv: TextView by lazy { tv_status_abc }//订单状态
    private val nameTv: TextView by lazy { tv_name_abc }//真实姓名
    private val phoneTv: TextView by lazy { tv_phone_abc }//联系方式
    private val priceTv: TextView by lazy { tv_price_abc }//交易单价
    private val numTv: TextView by lazy { tv_num_abc }//交易数量
    private val moneyLayout: LinearLayout by lazy { layout_money_abc }//交易金额
    private val moneyTv: TextView by lazy { tv_money_abc }//交易金额
    private val timeLayout: LinearLayout by lazy { layout_time_abc }//下单时间
    private val timeTv: TextView by lazy { tv_time_abc }//下单时间
    private val payLayout: LinearLayout by lazy { layout_pay_abc }//付款方式外围不加
    private val addPayLayout: LinearLayout by lazy { layout_add_pay_abc }//付款方式
    private val messageLayout: LinearLayout by lazy { layout_message_abc }//交易说明
    private val messageTv: TextView by lazy { tv_message_abc }//交易说明
    private val btnLayout: LinearLayout by lazy { layout_btn_abc }
    private val cancelBtn: Button by lazy { btn_cancle_abc }//取消订单按钮
    private val confirmBtn: RelativeLayout by lazy { btn_confirm_abc }//我已付款按钮
    private val clockTv: TextView by lazy { tv_clock_abc }//倒计时
    private val complainBtn: Button by lazy { btn_complain_abc }//申诉按钮
    private val releaseBtn: Button by lazy { btn_release_abc }//放币按钮
    private val view1: View by lazy { view1_abc }//放币按钮
    private val view2: View by lazy { view2_abc }//放币按钮


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

        SubOrderInfoBean.findSubOrderInfoNet(userBean?.userId!!, suborderId!!, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                .compose(netTfWithDialog())
                .subscribe({
                    this.subOrderInfoBean = it.result
                    refreshLayout()

                }, onError)

        RxView.clicks(confirmBtn)
                .compose(this.bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe {
                    //我已付款
                    pay()
                }

        RxView.clicks(releaseBtn)
                .compose(this.bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe {
                    //放币
                    release()
                }

        RxView.clicks(cancelBtn)
                .compose(this.bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe {
                    cancelNet(subOrderInfoBean!!)
                }

        RxView.clicks(complainBtn)
                .compose(this.bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe {
                }


    }

    fun refreshLayout() {
        subOrderInfoBean?.run {
            orderNo.text = "订单编号:$suborderId"
            var status = "待付款"
            var message = ""
            var titleContent = ""
//            if (userBean?.userId!! == userIdSell) {//商家
//                type = if (transactionType == SubOrderInfoBean.TRANSACTIONTYPE_BUY) {//卖币
//                    btn_cancle_abc.visibility = View.INVISIBLE
//                    titleContent = "卖出${CurrencyTool.getCurrency(tokenId)}"
//                    "确认收款"
//                } else {//买币
//                    if (suborderStatus == SubOrderInfoBean.SUBORDERSTATUS_WAIT_RELEASE) {
//                        btn_confirm_abc.visibility = View.INVISIBLE
//                        btn_cancle_abc.visibility = View.INVISIBLE
//                    }
//                    titleContent = "买入 ${CurrencyTool.getCurrency(tokenId)}"
//                    "确认付款"
//                }
//            } else {//用户
//                type = if (transactionType == SubOrderInfoBean.TRANSACTIONTYPE_BUY) {//买币
//                    if (suborderStatus == SubOrderInfoBean.SUBORDERSTATUS_WAIT_RELEASE) {
//                        btn_confirm_abc.visibility = View.INVISIBLE
//                        btn_cancle_abc.visibility = View.INVISIBLE
//                    }
//                    titleContent = "买入 ${CurrencyTool.getCurrency(tokenId)}"
//                    "确认付款"
//                } else {//卖币
//                    btn_cancle_abc.visibility = View.INVISIBLE
//                    titleContent = "卖出${CurrencyTool.getCurrency(tokenId)}"
//                    "确认收款"
//                }
//            }

            if (userBean!!.userId == userIdSell) {//商家
                if (transactionType == SubOrderInfoBean.TRANSACTIONTYPE_BUY) {//卖币
                    titleContent = "卖出${CurrencyTool.getCurrency(tokenId)}"
                    when (suborderStatus) {
                        SubOrderInfoBean.SUBORDERSTATUS_WAIT_PAY -> {//待付款
                            status = "待付款"
                            payLayout.visibility = View.GONE
                            messageLayout.visibility = View.GONE

                            cancelBtn.visibility = View.GONE
                            confirmBtn.visibility = View.GONE
                            complainBtn.visibility = View.VISIBLE
                            releaseBtn.visibility = View.VISIBLE
                            view1.visibility = View.GONE
                            view2.visibility = View.VISIBLE

                            complainBtn.isClickable = false
                            releaseBtn.isClickable = false
                        }
                        SubOrderInfoBean.SUBORDERSTATUS_WAIT_RELEASE -> {//待放币
                            status = "待放币"

                            payLayout.visibility = View.GONE
                            messageLayout.visibility = View.GONE

                            cancelBtn.visibility = View.GONE
                            confirmBtn.visibility = View.GONE
                            complainBtn.visibility = View.VISIBLE
                            releaseBtn.visibility = View.VISIBLE
                            view1.visibility = View.GONE
                            view2.visibility = View.VISIBLE

                            complainBtn.setTxtColor(R.color.color_main)
                            releaseBtn.setBgByColor(R.color.color_ff2b3e55)
                        }
                        SubOrderInfoBean.SUBORDERSTATUS_COMPLETED -> {//已完成
                            status = "已完成"
                            payLayout.visibility = View.GONE
                            messageLayout.visibility = View.GONE
                            btnLayout.visibility = View.GONE
                        }
                        SubOrderInfoBean.SUBORDERSTATUS_ACTIVE_CACEL, SubOrderInfoBean.SUBORDERSTATUS_OVERTIME_CACEL -> {//取消
                            status = "已取消"
                            moneyLayout.visibility = View.GONE
                            payLayout.visibility = View.GONE
                            messageLayout.visibility = View.GONE
                            btnLayout.visibility = View.GONE
                        }
                    }
                } else {//买币
                    titleContent = "买入 ${CurrencyTool.getCurrency(tokenId)}"
                    when (suborderStatus) {
                        SubOrderInfoBean.SUBORDERSTATUS_WAIT_PAY -> {//待付款
                            status = "待付款"
                            message = "下单后极速付款，到账后请及时放币"
                            cancelBtn.visibility = View.VISIBLE
                            confirmBtn.visibility = View.VISIBLE
                            complainBtn.visibility = View.GONE
                            releaseBtn.visibility = View.GONE
                            view1.visibility = View.VISIBLE
                            view2.visibility = View.GONE
                        }
                        SubOrderInfoBean.SUBORDERSTATUS_WAIT_RELEASE -> {//待放币
                            status = "待放币"
                            message = "下单后极速付款，到账后请及时放币"
                            cancelBtn.visibility = View.VISIBLE
                            confirmBtn.visibility = View.GONE
                            complainBtn.visibility = View.VISIBLE
                            releaseBtn.visibility = View.GONE
                            view1.visibility = View.VISIBLE
                            view2.visibility = View.GONE

                            complainBtn.setBgByColor(R.color.color_ff2b3e55)
                            complainBtn.setTxtColor(R.color.color_common)
                        }
                        SubOrderInfoBean.SUBORDERSTATUS_COMPLETED -> {//已完成
                            status = "已完成"
                            payLayout.visibility = View.GONE
                            messageLayout.visibility = View.GONE
                            btnLayout.visibility = View.GONE
                        }
                        SubOrderInfoBean.SUBORDERSTATUS_ACTIVE_CACEL, SubOrderInfoBean.SUBORDERSTATUS_OVERTIME_CACEL -> {//取消
                            status = "已取消"
                            moneyLayout.visibility = View.GONE
                            payLayout.visibility = View.GONE
                            messageLayout.visibility = View.GONE
                            btnLayout.visibility = View.GONE

                        }
                    }
                }

            } else {//普通用户
                if (transactionType == SubOrderInfoBean.TRANSACTIONTYPE_BUY) {//买币
                    titleContent = "买入 ${CurrencyTool.getCurrency(tokenId)}"
                    when (suborderStatus) {
                        SubOrderInfoBean.SUBORDERSTATUS_WAIT_PAY -> {//待付款
                            status = "待付款"
                            message = "下单后极速付款，到账后请及时放币"
                            cancelBtn.visibility = View.VISIBLE
                            confirmBtn.visibility = View.VISIBLE
                            complainBtn.visibility = View.GONE
                            releaseBtn.visibility = View.GONE
                            view1.visibility = View.VISIBLE
                            view2.visibility = View.GONE
                        }
                        SubOrderInfoBean.SUBORDERSTATUS_WAIT_RELEASE -> {//待放币
                            status = "待放币"
                            message = "下单后极速付款，到账后请及时放币"
                            cancelBtn.visibility = View.VISIBLE
                            confirmBtn.visibility = View.GONE
                            complainBtn.visibility = View.VISIBLE
                            releaseBtn.visibility = View.GONE
                            view1.visibility = View.VISIBLE
                            view2.visibility = View.GONE

                            complainBtn.setBgByColor(R.color.color_ff2b3e55)
                            complainBtn.setTxtColor(R.color.color_common)
                        }
                        SubOrderInfoBean.SUBORDERSTATUS_COMPLETED -> {//已完成
                            status = "已完成"
                            payLayout.visibility = View.GONE
                            messageLayout.visibility = View.GONE
                            btnLayout.visibility = View.GONE
                        }
                        SubOrderInfoBean.SUBORDERSTATUS_ACTIVE_CACEL, SubOrderInfoBean.SUBORDERSTATUS_OVERTIME_CACEL -> {//取消
                            status = "已取消"
                            moneyLayout.visibility = View.GONE
                            payLayout.visibility = View.GONE
                            messageLayout.visibility = View.GONE
                            btnLayout.visibility = View.GONE

                        }
                    }
                } else {//卖币
                    titleContent = "卖出${CurrencyTool.getCurrency(tokenId)}"
                    when (suborderStatus) {
                        SubOrderInfoBean.SUBORDERSTATUS_WAIT_PAY -> {//待付款
                            status = "待付款"
                            payLayout.visibility = View.GONE
                            messageLayout.visibility = View.GONE

                            cancelBtn.visibility = View.GONE
                            confirmBtn.visibility = View.GONE
                            complainBtn.visibility = View.VISIBLE
                            releaseBtn.visibility = View.VISIBLE
                            view1.visibility = View.GONE
                            view2.visibility = View.VISIBLE

                            complainBtn.isClickable = false
                            releaseBtn.isClickable = false
                        }
                        SubOrderInfoBean.SUBORDERSTATUS_WAIT_RELEASE -> {//待放币
                            status = "待放币"

                            payLayout.visibility = View.GONE
                            messageLayout.visibility = View.GONE

                            cancelBtn.visibility = View.GONE
                            confirmBtn.visibility = View.GONE
                            complainBtn.visibility = View.VISIBLE
                            releaseBtn.visibility = View.VISIBLE
                            view1.visibility = View.GONE
                            view2.visibility = View.VISIBLE

                            complainBtn.setTxtColor(R.color.color_main)
                            releaseBtn.setBgByColor(R.color.color_ff2b3e55)
                        }
                        SubOrderInfoBean.SUBORDERSTATUS_COMPLETED -> {//已完成
                            status = "已完成"
                            payLayout.visibility = View.GONE
                            messageLayout.visibility = View.GONE
                            btnLayout.visibility = View.GONE
                        }
                        SubOrderInfoBean.SUBORDERSTATUS_ACTIVE_CACEL, SubOrderInfoBean.SUBORDERSTATUS_OVERTIME_CACEL -> {//取消
                            status = "已取消"
                            moneyLayout.visibility = View.GONE
                            payLayout.visibility = View.GONE
                            messageLayout.visibility = View.GONE
                            btnLayout.visibility = View.GONE
                        }
                    }
                }
            }



            topbar_abc.setTitle(titleContent)

            statusTv.text = status
            tv_price_abc.text = "${suborderPrice}CNY"
            tv_num_abc.text = "${suborderNum}${CurrencyTool.getCurrency(tokenId)}"
            tv_money_abc.text = "${suborderAmount}CNY"
            tv_message_abc.text = remark
            timeTv.text = TimeTool.format(TimeTool.PATTERN_DEFAULT, suborderCreateTime)

            time = suborderOverTime

            accmoney
        }?.run {
            tv_name_abc.text = trueName
            tv_phone_abc.text = phone
            if (accmoneyWeixinurl != null && accmoneyWeixinurl.isNotEmpty()) {
                val wechat = ViewFactory.createPayMehod(R.layout.tv_paymethod_wechat)
                wechat.setOnClickListener {
                    ShowImagePayMethodActivity.skip(this@OtcConfirmActivity, accmoneyWeixinurl)
                }
                addPayLayout.addView(wechat)
            }
            if (accmoneyZfburl != null && accmoneyZfburl.isNotEmpty()) {
                val zfb = ViewFactory.createPayMehod(R.layout.tv_paymethod_zhifubao)
                addPayLayout.addView(zfb)
                zfb.setOnClickListener {
                    ShowImagePayMethodActivity.skip(this@OtcConfirmActivity, accmoneyZfburl)
                }
            }
            if (accmoneyBankcard != null && accmoneyBankcard.isNotEmpty()) {
                val bank = ViewFactory.createPayMehod(R.layout.tv_paymethod_bank)
                addPayLayout.addView(bank)
                bank.setOnClickListener {
                    ShowBankPayMethodActivity.skip(this@OtcConfirmActivity, this)
                }
            }
        }

        if (time > 0)
            Flowable.intervalRange(0, time, 0, 1, TimeUnit.SECONDS)
                    .compose(netTf())
                    .subscribe({
                        clockTv.text = "${TimeTool.formatTime(time - it)}"
                    }, onError, {})
    }

    /**
     * 付款
     */
    fun pay() {
        if (userBean?.userId!! == subOrderInfoBean!!.userIdSell) {//本人是商家
            NRetrofit.instance
                    .sellService()
                    .confirmReceipt(subOrderInfoBean!!.userIdSell, subOrderInfoBean!!.suborderId, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                    .compose(netTf())
                    .subscribe({
                        confirm(it)
                    }, onError)
        } else {
            NRetrofit.instance
                    .buyService()
                    .confirmReceipt(subOrderInfoBean!!.userIdBu, subOrderInfoBean!!.suborderId, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                    .compose(netTf())
                    .subscribe({
                        confirm(it)
                    }, onError)
        }
    }

    /**
     * 收款，放币
     */
    fun release() {
        if (userBean?.userId!! == subOrderInfoBean!!.userIdSell) {//本人是商家
            NRetrofit.instance
                    .buyService()
                    .confirmPayment(subOrderInfoBean!!.userIdSell, subOrderInfoBean!!.suborderId, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                    .compose(netTf())
                    .subscribe({
                        confirm(it)
                    }, onError)
        } else {
            NRetrofit.instance
                    .sellService()
                    .confirmPayment(subOrderInfoBean!!.userIdBu, subOrderInfoBean!!.suborderId, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                    .compose(netTf())
                    .subscribe({
                        confirm(it)
                    }, onError)

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
        if (rs.success) {
            this@OtcConfirmActivity.finish()
            EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_CONFIRM_PAY))
            EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_REFRESH_ASSET))
        } else {
            try {
                if (rs.isCauseNotEmpty()) {
                    AuthorityDialog.getInstance(this)
                            .show("OTC交易需要完成以下设置，请检查"
                                    , rs.cause) {
                            }
                }
            } catch (e: Exception) {
                showToast("放币功能异常，请联系客服")
            }
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
            this@OtcConfirmActivity.finish()
    }
}
