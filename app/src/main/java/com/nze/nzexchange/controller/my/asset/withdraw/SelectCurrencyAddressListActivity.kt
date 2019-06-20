package com.nze.nzexchange.controller.my.asset.withdraw

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.CurrenyWithdrawAddressBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.bean2.CoinAddressBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.my.asset.withdraw.presenter.CurrencyWithdrawP
import com.nze.nzexchange.widget.CommonTopBar
import kotlinx.android.synthetic.main.activity_selet_address_list.*

/***
 * 地址列表
 */
class SelectCurrencyAddressListActivity : NBaseActivity() {
    val topBar: CommonTopBar by lazy { ctb_asal }
    val currencyWithdrawP: CurrencyWithdrawP by lazy { CurrencyWithdrawP(this) }
    val selectLv: ListView by lazy { lv_select_asal }
    val selectAdapter: SelectCurrencyAddressAdapter by lazy {
        SelectCurrencyAddressAdapter(this).apply {
            onSelectListener = { position ->
                val bean = addressList[position]
                addressList[position].isSelect = !bean.isSelect
                this.notifyDataSetChanged()
            }
        }
    }
    val addBtn: Button by lazy { btn_add_asal }
    var userBean = UserBean.loadFromApp()
    val addressList: MutableList<CurrenyWithdrawAddressBean> by lazy { mutableListOf<CurrenyWithdrawAddressBean>() }
    lateinit var currency: String

//    companion object {
//        fun skip(context: Context, currency: String) {
//            context.startActivity(Intent(context, SelectCurrencyAddressListActivity::class.java)
//                    .putExtra(IntentConstant.PARAM_CURRENCY, currency))
//        }
//    }

    override fun getRootView(): Int = R.layout.activity_selet_address_list

    override fun initView() {
        intent?.let {
            currency = it.getStringExtra(IntentConstant.PARAM_CURRENCY)
        }
        selectLv.adapter = selectAdapter

        addBtn.setOnClickListener {
            AddAddressActivity.skip(this, currency)
        }

        topBar.setRightClick {
            addressList.forEach {
                if (it.isSelect)
                    currencyWithdrawP.deleteCurrencyWithdrawAddress(userBean!!, it, addressList, {
                        if (it.success) {
                            selectAdapter.group = addressList
                        }
                    }, onError)
            }
        }

        selectLv.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent()
            intent.putExtra(IntentConstant.PARAM_ADDRESS, addressList[position])
            this.setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        currencyWithdrawP.getCurrencyWithdrawAddress(userBean!!, currency, {
            if (it.success) {
                addressList.clear()
                addressList.addAll(it.result)
                selectAdapter.group = addressList
            }
        }, onError)
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

}
