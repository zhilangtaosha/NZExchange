package com.nze.nzexchange.controller.my.asset

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.UserAssetBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import kotlinx.android.synthetic.main.activity_select_currency.*

/**
 * 选择币种
 * 每个账户的币种地址是不同的
 * 需要请求不同接口获取币种
 */
class SelectCurrencyActivity : NBaseActivity() {
    val listView: ListView by lazy { lv_currency_asc }
    val currencyAdapter: SelectCurrencyAdapter by lazy { SelectCurrencyAdapter(this) }

    val currencyList: MutableList<String>
            by lazy { mutableListOf<String>() }

    var userBean: UserBean? = UserBean.loadFromApp()
    val userAssetList: MutableList<UserAssetBean> by lazy { mutableListOf<UserAssetBean>() }

    companion object {
        const val TYPE_BIBI = 0
        const val TYPE_LEGAL = 1
        const val TYPE_OTC = 2
    }

    override fun getRootView(): Int = R.layout.activity_select_currency

    override fun initView() {
        listView.adapter = currencyAdapter
        listView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent()
            intent.putExtra(IntentConstant.PARAM_ASSET, userAssetList.get(position))
            this@SelectCurrencyActivity.setResult(Activity.RESULT_OK, intent)
            this@SelectCurrencyActivity.finish()

        }

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

    fun getBibiAsset() {
        UserAssetBean.getUserAssets(userBean?.userId!!)
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        userAssetList.addAll(it.result)
                        userAssetList.forEach {
                            currencyList.add(it.currency)
                            currencyAdapter.group = currencyList
                        }
                    }
                }, onError)
    }
}
