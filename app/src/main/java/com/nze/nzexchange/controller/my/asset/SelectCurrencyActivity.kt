package com.nze.nzexchange.controller.my.asset

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.ListView
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.UserAssetBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.AccountType
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.AuthorityDialog
import kotlinx.android.synthetic.main.activity_select_currency.*

/**
 * 选择币种
 * 每个账户的币种地址是不同的
 * 需要请求不同接口获取币种
 */
class SelectCurrencyActivity : NBaseActivity() {
    val listView: ListView by lazy { lv_currency_asc }
    val currencyAdapter: SelectCurrencyAdapter by lazy { SelectCurrencyAdapter(this) }
    val cancelTv: TextView by lazy { tv_cancel_asc }

    val currencyList: MutableList<String>
            by lazy { mutableListOf<String>() }

    var userBean: UserBean? = UserBean.loadFromApp()
    val userAssetList: MutableList<UserAssetBean> by lazy { mutableListOf<UserAssetBean>() }
    var type: Int = AccountType.BIBI
    val dialog = AuthorityDialog.getInstance(this)

    companion object {
        fun skipForResult(activity: FragmentActivity, type: Int) {
            val intent = Intent(activity, SelectCurrencyActivity::class.java)
            intent.putExtra(IntentConstant.PARAM_TYPE, type)
            activity.startActivityForResult(intent, 1)
        }
    }

    override fun getRootView(): Int = R.layout.activity_select_currency

    override fun initView() {

        intent?.let {
            type = it.getIntExtra(IntentConstant.PARAM_TYPE, AccountType.BIBI)
        }

        listView.adapter = currencyAdapter
        listView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent()
            intent.putExtra(IntentConstant.PARAM_ASSET, userAssetList.get(position))
            this@SelectCurrencyActivity.setResult(Activity.RESULT_OK, intent)
            this@SelectCurrencyActivity.finish()

        }

        if (type == AccountType.OTC) {
            getOtcAsset()
        } else {
            getBibiAsset()
        }

        cancelTv.setOnClickListener {
            finish()
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

    fun getOtcAsset() {
        UserAssetBean.getUserAssets(userBean?.userId!!, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        userAssetList.addAll(it.result)
                        userAssetList.forEach {
                            currencyList.add(it.currency)
                            currencyAdapter.group = currencyList
                        }
                    } else {
                        if (!dialog.isShow() && it.isCauseNotEmpty()) {
                            AuthorityDialog.getInstance(this)
                                    .show("查询资产需要完成以下设置，请检查"
                                            , it.cause) {
                                        finish()
                                    }
                        }
                    }
                }, onError)
    }

    fun getBibiAsset() {
        UserAssetBean.assetInquiry(userBean?.userId!!, tokenUserId = userBean!!.tokenReqVo.tokenUserId, tokenUserKey = userBean!!.tokenReqVo.tokenUserKey)
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        userAssetList.addAll(it.result)
                        userAssetList.forEach {
                            currencyList.add(it.currency)
                            currencyAdapter.group = currencyList
                        }
                    } else {
                        if (!dialog.isShow() && it.isCauseNotEmpty()) {
                            AuthorityDialog.getInstance(this)
                                    .show("查询资产需要完成以下设置，请检查"
                                            , it.cause) {
                                        finish()
                                    }
                        }
                    }
                }, onError)
    }
}
