package com.nze.nzexchange.controller.my.asset.transfer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.Result
import com.nze.nzexchange.bean.UserAssetBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.AccountType
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.AuthorityDialog
import com.nze.nzexchange.controller.my.asset.SelectCurrencyActivity
import com.nze.nzexchange.extend.format
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.http.NRetrofit
import com.nze.nzexchange.tools.editjudge.EditTextJudgeNumberWatcher
import com.nze.nzexchange.validation.EmptyValidation
import com.nze.nzexchange.widget.CommonButton
import com.nze.nzexchange.widget.CommonTopBar
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.activity_transfer.*

class TransferActivity : NBaseActivity(), View.OnClickListener {

    val topBar: CommonTopBar by lazy { ctb_at }
    val currencyTv: TextView by lazy { tv_currency_at }
    val selectCurrency: TextView by lazy { tv_select_currency_at }
    val swapIv: ImageView by lazy { iv_swap_at }
    val fromAccountTv: TextView by lazy { tv_from_account_at }
    val toAccountTv: TextView by lazy { tv_to_account_at }
    val transferEt: EditText by lazy { et_transfer_at }
    val unitTv: TextView by lazy { tv_unit_at }
    val allTv: TextView by lazy { tv_all_at }
    val availableTv: TextView by lazy { tv_available_at }
    val transferBtn: CommonButton by lazy { btn_transfer_at }

    var userAssetBean: UserAssetBean? = null
    var type: Int = AccountType.BIBI
    var toType: Int = AccountType.OTC
    var userBean = UserBean.loadFromApp()

    val TRANSFER_OTC = "outside"
    val TRANSFER_BIBI = "coin"


    var bundle: Bundle? = null
    lateinit var otcList: ArrayList<UserAssetBean>
    lateinit var bibiList: ArrayList<UserAssetBean>
    val dialog: TransferSuccessDialog  by lazy { TransferSuccessDialog(this) }
    var editTextJudgeNumberWatcher: EditTextJudgeNumberWatcher? = null

    companion object {
        fun skip(context: Context, bundle: Bundle) {
            val intent = Intent(context, TransferActivity::class.java)
            intent.putExtra(IntentConstant.PARAM_BUNDLE, bundle)
            context.startActivity(intent)
        }
    }

    override fun getRootView(): Int = R.layout.activity_transfer

    override fun initView() {
        topBar.setRightClick {
            skipActivity(TransferHistoryActivity::class.java)
        }
        intent?.let {
            bundle = it.getBundleExtra(IntentConstant.PARAM_BUNDLE)

        }
        bundle?.let {
            type = it.getInt(IntentConstant.PARAM_TYPE)
            userAssetBean = it.getParcelable(IntentConstant.PARAM_ASSET)
            otcList = it.getParcelableArrayList(IntentConstant.PARAM_OTC_ACCOUNT)
            bibiList = it.getParcelableArrayList(IntentConstant.PARAM_BIBI_ACCOUNT)
        }

        refreshLayout(userAssetBean!!, false)

        transferBtn.initValidator()
                .add(transferEt, EmptyValidation())
                .executeValidator()

        selectCurrency.setOnClickListener(this)
        swapIv.setOnClickListener(this)
        allTv.setOnClickListener(this)
        transferBtn.setOnCommonClick(this)
        swapAccount(type)


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_select_currency_at -> {
                SelectCurrencyActivity.skipForResult(this, AccountType.OTC)
            }
            R.id.iv_swap_at -> {
                if (type == AccountType.BIBI) {
                    type = AccountType.OTC
                    toType = AccountType.BIBI
                } else {
                    type = AccountType.BIBI
                    toType = AccountType.OTC
                }
                swapAccount(type)
            }
            R.id.tv_all_at -> {
                transferEt.setText(userAssetBean!!.available.format(userAssetBean!!.decimalPrec))
            }
            R.id.btn_transfer_at -> {
                if (transferBtn.validate()) {
                    val amount = transferEt.getContent()
                    if (amount.toDouble() <= 0) {
                        showToast("划转金额不能为0")
                        return
                    }
                    var from = TRANSFER_BIBI
                    var to = TRANSFER_OTC
                    if (type == AccountType.OTC) {
                        from = TRANSFER_OTC
                        to = TRANSFER_BIBI
                    }
                    transferAssets(userBean!!.userId, userAssetBean!!.currency, from, to, amount.toDouble(), null, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                }
            }
        }
    }

    fun swapAccount(type: Int) {
        when (type) {
            AccountType.BIBI -> {
                fromAccountTv.text = "币币账户"
                toAccountTv.text = "OTC账户"
                toType = AccountType.OTC
            }
            AccountType.OTC -> {
                fromAccountTv.text = "OTC账户"
                toAccountTv.text = "币币账户"
                toType = AccountType.BIBI
            }
            AccountType.LEGAL -> {
                fromAccountTv.text = "法币账户"
                toAccountTv.text = "选择账户"
            }
        }

        refreshLayout(userAssetBean!!, true)
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
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

    override fun getContainerTargetView(): View? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val bean = data?.extras?.getParcelable<UserAssetBean>(IntentConstant.PARAM_ASSET)

            refreshLayout(bean!!, true)
        }
    }

    fun refreshLayout(bean: UserAssetBean, isRefreshData: Boolean) {
        if (isRefreshData && type == AccountType.BIBI) {
            for (b in bibiList) {
                if (bean.currency == b.currency) {
                    userAssetBean = b
                    break
                }
            }
        } else if (isRefreshData) {
            for (b in otcList) {
                if (bean.currency == b.currency) {
                    userAssetBean = b
                    break
                }
            }
        }
        userAssetBean?.let {
            currencyTv.text = it.currency
            availableTv.text = "可用${it.available.format(userAssetBean!!.decimalPrec)}${it.currency}"
            unitTv.text = it.currency
            if (editTextJudgeNumberWatcher != null)
                transferEt.removeTextChangedListener(editTextJudgeNumberWatcher)
            editTextJudgeNumberWatcher = EditTextJudgeNumberWatcher(transferEt, it.decimalPrec)
            transferEt.addTextChangedListener(editTextJudgeNumberWatcher)
        }
    }

    /**
     * 划转接口
     * 划转成功后，重新请求资产接口，获取最新的资产
     */
    fun transferAssets(userId: String,
                       token: String,
                       from: String,
                       to: String,
                       amount: Double,
                       remark: String?,
                       tokenUserId: String,
                       tokenUserKey: String) {
        NRetrofit.instance
                .assetService()
                .transferAssets(userId, token, from, to, amount, remark, tokenUserId, tokenUserKey)
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        dialog.show(toType)
                        getAsset()
                    } else {
                        if (it.isCauseNotEmpty()) {
                            AuthorityDialog.getInstance(this)
                                    .show("资金划转需要完成以下设置，请检查",
                                            it.cause
                                    ) {
                                        this@TransferActivity.finish()
                                    }
                        } else {
                            showToast(it.message)
                        }
                    }
                }, onError)
    }

    fun getAsset() {
        Flowable.zip(
                UserAssetBean.getUserAssets(userBean?.userId!!, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey),
                UserAssetBean.assetInquiry(userBean?.userId!!, tokenUserId = userBean!!.tokenReqVo.tokenUserId, tokenUserKey = userBean!!.tokenReqVo.tokenUserKey),
                object : BiFunction<Result<MutableList<UserAssetBean>>, Result<MutableList<UserAssetBean>>, Boolean> {
                    override fun apply(t1: Result<MutableList<UserAssetBean>>, t2: Result<MutableList<UserAssetBean>>): Boolean {
                        if (t1.success) {
                            otcList.clear()
                            otcList.addAll(t1.result)
                        }
                        if (t2.success) {
                            bibiList.clear()
                            bibiList.addAll(t2.result)
                        }
                        return true
                    }
                }).compose(netTfWithDialog())
                .subscribe({

                }, onError)
    }

}
