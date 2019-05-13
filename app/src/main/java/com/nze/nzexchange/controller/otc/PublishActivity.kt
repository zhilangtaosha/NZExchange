package com.nze.nzexchange.controller.otc

import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.setTextFromHtml
import com.nze.nzexchange.http.NRetrofit
import com.nze.nzexchange.bean.Result
import com.nze.nzexchange.bean.UserAssetBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.AccountType
import com.nze.nzexchange.controller.common.AuthorityDialog
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.extend.removeE
import com.nze.nzexchange.tools.DoubleMath
import com.nze.nzexchange.tools.TextTool
import com.nze.nzexchange.validation.EmptyValidation
import com.nze.nzexchange.widget.CommonTopBar
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.activity_publish.*
import org.greenrobot.eventbus.EventBus
import retrofit2.http.Field
import java.util.concurrent.TimeUnit

/**
 * OTC发布购买和出售广告
 */
class PublishActivity : NBaseActivity(), View.OnClickListener {


    val TYPE_BUY: Int = 0
    val TYPE_SALE: Int = 1
    var currentType: Int = TYPE_BUY
    lateinit var topBar: CommonTopBar
    lateinit var tokenId: String
    lateinit var currency: String
    var flag: Boolean = true
    var userAssetBean: UserAssetBean? = null
    var priceTag = true
    var numTag = true
    var moneyTag = true


    override fun getRootView(): Int = R.layout.activity_publish

    private var userBean: UserBean? = UserBean.loadFromApp()

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
                    if (priceTag) {
                        moneyTag = false
                        var value = ""
                        var num = et_num_value_ap.text
                        if (it.isNotEmpty() && num.isNotEmpty())
                            value = DoubleMath.mul(it.toString().toDouble(), num.toString().toDouble()).removeE()
                        et_money_value_ap.setText(value)
                    } else {
                        priceTag = true
                    }
                }

        RxTextView.textChanges(et_num_value_ap)
                .subscribe {
                    if (numTag) {
                        var total = ""
                        var num = 0.0
                        val price = et_price_value_ap.text.toString()
                        if (it.isNotEmpty()) {
                            num = it.toString().toDouble()
                        }
                        if (currentType == TYPE_SALE && userAssetBean != null && num > userAssetBean!!.available) {
                            num = userAssetBean!!.available
                            val s = num.removeE()
                            et_num_value_ap.setText(s)
                            et_num_value_ap.setSelection(s.length)
                            showToast("只有${num}${currency}资产")
                        }
                        if (it.isNotEmpty() && price.isNotEmpty()) {
                            moneyTag = false
                            total = DoubleMath.mul(num, price.toDouble()).removeE()
                            et_money_value_ap.setText(total)
                        }

                    } else {
                        numTag = true
                    }
                }

        RxTextView.textChanges(et_money_value_ap)
                .subscribe {
                    if (moneyTag) {
                        numTag = false
                        var value = ""
                        val price = et_price_value_ap.text.toString()
                        if (it.isNotEmpty() && price.isNotEmpty()) {
                            value = DoubleMath.div(it.toString().toDouble(), price.toDouble()).removeE()
                        }
                        et_num_value_ap.setText(value)
                    } else {
                        moneyTag = true
                    }
                }

        RxView.clicks(btn_ap)
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe {
                    //发布广告
                    if (btn_ap.validate()) {
                        val money = et_money_value_ap.getContent().toDouble()
                        if (money < 400.0) {
                            showToast("最小交易金额为400")
                            return@subscribe
                        } else if (money > 100000) {
                            showToast("最大交易金额为10万")
                            return@subscribe
                        }

                        if (currentType == TYPE_SALE) {
                            submitNet(tokenId, et_num_value_ap.text.toString(), et_price_value_ap.text.toString(), et_message_ap.text.toString())
                                    .compose(netTfWithDialog())
                                    .subscribe({
                                        Toast.makeText(this@PublishActivity, it.message, Toast.LENGTH_SHORT).show()
                                        if (it.success) {
                                            this@PublishActivity.finish()
                                            EventBus.getDefault().post(EventCenter<Boolean>(EventCode.CODE_PULISH, true))
                                            EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_REFRESH_ASSET))
                                        } else {
                                            if (it.isCauseNotEmpty()) {
                                                AuthorityDialog.getInstance(this)
                                                        .show("进行OTC交易需要完成以下设置，请检查",
                                                                it.cause) {
                                                            finish()
                                                        }
                                            }
                                        }
                                    }, onError)
                        } else {
                            sellNet(tokenId, userBean?.userId!!, et_num_value_ap.text.toString(), et_price_value_ap.text.toString(), et_message_ap.text.toString(), userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                                    .compose(netTfWithDialog())
                                    .subscribe({
                                        Toast.makeText(this@PublishActivity, it.message, Toast.LENGTH_SHORT).show()
                                        if (it.success) {
                                            this@PublishActivity.finish()
                                            EventBus.getDefault().post(EventCenter<Boolean>(EventCode.CODE_PULISH, true))
                                            EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_REFRESH_ASSET))
                                        } else {
                                            if (it.isCauseNotEmpty()) {
                                                AuthorityDialog.getInstance(this)
                                                        .show("进行OTC交易需要完成以下设置，请检查",
                                                                it.cause) {
                                                            finish()
                                                        }
                                            }
                                        }
                                    }, onError)
                        }
                    }
                }

        changLayout()
        getBibiAsset()
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
            et_num_value_ap.hint = "请输入出售数量"
            et_message_ap.hint = TextTool.fromHtml("1.订单有效期为15分钟，请及时付款并点击「我已支付」按钮<br/>" +
                    "2.币由系统锁定托管，请安心下单")
        }
    }

    fun submitNet(tokenId: String, poolAllCount: String, poolPrice: String, remark: String): Flowable<Result<Boolean>> {
        return Flowable.defer {
            NRetrofit.instance
                    .buyService()
                    .pendingOrder(userBean!!.userId, tokenId, poolAllCount, poolPrice, remark, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
        }
    }

    fun sellNet(tokenId: String,
                userId: String,
                poolAllCount: String,
                poolPrice: String,
                remark: String,
                tokenUserId: String,
                tokenUserKey: String
    ): Flowable<Result<Boolean>> {
        return Flowable.defer {
            NRetrofit.instance
                    .sellService()
                    .pendingOrder(userId, tokenId, poolAllCount, poolPrice, remark, tokenUserId, tokenUserKey)
        }
    }

    /**
     * 获取发布币种的资产
     */
    fun getBibiAsset() {
        UserAssetBean.assetInquiry(userBean?.userId!!, tokenName = currency)
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        val list = it.result
                        if (list.size > 0)
                            userAssetBean = list[0]
                    }
                }, onError)
    }
}
