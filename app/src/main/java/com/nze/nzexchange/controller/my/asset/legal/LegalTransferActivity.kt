package com.nze.nzexchange.controller.my.asset.legal

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
import com.nze.nzexchange.bean.LegalAccountBean
import com.nze.nzexchange.bean.Result
import com.nze.nzexchange.bean.UserAssetBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.AccountType
import com.nze.nzexchange.config.AccountType.Companion.LEGAL
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.config.LegalConfig
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.AuthorityDialog
import com.nze.nzexchange.controller.my.asset.SelectCurrencyActivity
import com.nze.nzexchange.controller.my.asset.legal.presenter.LegalP
import com.nze.nzexchange.controller.my.asset.transfer.TransferHistoryActivity
import com.nze.nzexchange.controller.my.asset.transfer.TransferSuccessDialog
import com.nze.nzexchange.extend.formatForCurrency
import com.nze.nzexchange.extend.formatForLegal
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.http.CRetrofit
import com.nze.nzexchange.http.NRetrofit
import com.nze.nzexchange.validation.EmptyValidation
import com.nze.nzexchange.widget.CommonButton
import com.nze.nzexchange.widget.CommonTopBar
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.activity_transfer.*

class LegalTransferActivity : NBaseActivity(), View.OnClickListener {
    val legalP: LegalP by lazy { LegalP(this) }
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
    var type: Int = AccountType.LEGAL
    var userBean = UserBean.loadFromApp()

    val TRANSFER_LEGALCURRENCY = "legalCurrency "
    val TRANSFER_BIBI = "coin"


    val bibiList: MutableList<UserAssetBean> by lazy { mutableListOf<UserAssetBean>() }
    val dialog: TransferSuccessDialog  by lazy { TransferSuccessDialog(this) }

    var accountBean: LegalAccountBean? = null

    companion object {
        fun skip(context: Context, accountBean: LegalAccountBean) {
            val intent = Intent(context, LegalTransferActivity::class.java)
            intent.putExtra(IntentConstant.PARAM_LEGAL_ACCOUNT, accountBean)
            context.startActivity(intent)
        }
    }

    override fun getRootView(): Int = R.layout.activity_transfer

    override fun initView() {
        topBar.setRightClick {
            skipActivity(TransferHistoryActivity::class.java)
        }
        intent?.let {
            accountBean = it.getParcelableExtra(IntentConstant.PARAM_LEGAL_ACCOUNT)
        }
        selectCurrency.visibility = View.GONE
        currencyTv.text = LegalConfig.NAME
        unitTv.text = LegalConfig.NAME
        refreshLayout(false)

        transferBtn.initValidator()
                .add(transferEt, EmptyValidation())
                .executeValidator()

        selectCurrency.setOnClickListener(this)
        swapIv.setOnClickListener(this)
        allTv.setOnClickListener(this)
        transferBtn.setOnCommonClick(this)
        swapAccount(type)

        getAsset()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_select_currency_at -> {
            }
            R.id.iv_swap_at -> {
                if (type == AccountType.BIBI) {
                    type = AccountType.LEGAL
                } else {
                    type = AccountType.BIBI
                }
                swapAccount(type)
            }
            R.id.tv_all_at -> {
                transferEt.setText(userAssetBean!!.available.toString())
            }
            R.id.btn_transfer_at -> {
                if (transferBtn.validate()) {
                    val amount = transferEt.getContent()
                    var from = TRANSFER_BIBI
                    var to = TRANSFER_LEGALCURRENCY
                    if (type == AccountType.LEGAL) {
                        from = TRANSFER_LEGALCURRENCY
                        to = TRANSFER_BIBI
                    }
                    transferAssets(userBean!!.userId, LegalConfig.NAME, from, to, amount.toDouble(), null, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                }
            }
        }
    }

    fun swapAccount(type: Int) {
        when (type) {
            AccountType.BIBI -> {
                fromAccountTv.text = "币币账户"
                toAccountTv.text = "法币账户"
            }
            AccountType.OTC -> {
                fromAccountTv.text = "OTC账户"
                toAccountTv.text = "币币账户"
            }
            AccountType.LEGAL -> {
                fromAccountTv.text = "法币账户"
                toAccountTv.text = "币币账户"
            }
        }

        refreshLayout(true)
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


    fun refreshLayout(isRefreshData: Boolean) {
        if (isRefreshData && type == AccountType.BIBI) {
            for (b in bibiList) {
                if (LegalConfig.NAME == b.currency) {
                    userAssetBean = b
                    break
                }
            }
            userAssetBean?.let {
                availableTv.text = "可用${it.available.formatForCurrency()}${LegalConfig.NAME}"
                unitTv.text = it.currency
            }
        } else {
            accountBean?.let {
                availableTv.text = "可用${it.accAbleAmount.formatForCurrency()}${LegalConfig.NAME}"
            }
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
                        dialog.show(type)
                        getAsset()
                    } else {
                        if (it.isCauseNotEmpty()) {
                            AuthorityDialog.getInstance(this)
                                    .show("资金划转需要完成以下设置，请检查",
                                            it.cause
                                    ) {
                                        this@LegalTransferActivity.finish()
                                    }
                        } else {
                            showToast("划转失败")
                        }
                    }
                }, onError)
    }

    fun getAsset() {
        legalP.getLegalAccountInfo(userBean!!, {
            if (it.success) {
                accountBean = it.result
            }
        }, onError)
        UserAssetBean.assetInquiry(userBean?.userId!!, tokenUserId = userBean!!.tokenReqVo.tokenUserId, tokenUserKey = userBean!!.tokenReqVo.tokenUserKey)
                .compose(netTf())
                .subscribe({
                    if (it.success) {
                        bibiList.clear()
                        bibiList.addAll(it.result)
                    }
                }, onError)

    }

}
