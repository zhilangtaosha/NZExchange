package com.nze.nzexchange.controller.my.setting

import android.content.SharedPreferences
import android.view.View
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean2.MoneyTypeBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.repository.sp.SettingSp
import kotlinx.android.synthetic.main.activity_money_type_set.*

class MoneyTypeSetActivity : NBaseActivity() {
    val sp: SettingSp by lazy { SettingSp.getInstance(this) }
    val listView: ListView by lazy {
        listView_amts.apply {
            setOnItemClickListener { parent, view, position, id ->
                val bean = moneyTypeList.get(position)
                sp.put(SettingSp.KEY_MONEY_TYPE,bean.moneySign)
                moneyAdapter.notifyDataSetChanged()
            }
        }
    }
    val moneyAdapter: MoneyTypeAdapter by lazy { MoneyTypeAdapter(this) }
    val moneyTypeList by lazy {
        mutableListOf<MoneyTypeBean>().apply {
            add(MoneyTypeBean("人民币", "CNY"))
            add(MoneyTypeBean("美元", "USD"))
            add(MoneyTypeBean("韩元", "KRW"))
            add(MoneyTypeBean("日元", "JPY"))
            add(MoneyTypeBean("欧元", "EUR"))
            add(MoneyTypeBean("卢布", "RUB"))
            add(MoneyTypeBean("英镑", "GBP"))
        }
    }

    override fun getRootView(): Int = R.layout.activity_money_type_set

    override fun initView() {
        listView.adapter = moneyAdapter
        moneyAdapter.group = moneyTypeList
//        val string = sp.getString(SettingSp.KEY_MONEY_TYPE,"CNY")

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
