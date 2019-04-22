package com.nze.nzexchange.controller.market

import android.icu.lang.UCharacter.GraphemeClusterBreak.V
import android.view.View
import android.widget.ListView
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.http.NRetrofit
import kotlinx.android.synthetic.main.activity_optional_edit.*
import org.greenrobot.eventbus.EventBus

class OptionalEditActivity : NBaseActivity(), View.OnClickListener {


    val addTv: TextView by lazy { tv_add_aoe }
    val finishTv: TextView by lazy { tv_finish_aoe }
    val listView: ListView by lazy { lv_aoe }
    val editAdapter by lazy {
        OptionalEditAdapter(this).apply {
            onRemoveListener = { item, position ->
                delete(item)
            }
        }
    }
    var userBean = UserBean.loadFromApp()

    override fun getRootView(): Int = R.layout.activity_optional_edit

    override fun initView() {
        listView.adapter = editAdapter

        addTv.setOnClickListener(this)
        finishTv.setOnClickListener(this)
        getDataFromNet()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_add_aoe -> {
                skipActivity(PairSearchActivity::class.java)
            }
            R.id.tv_finish_aoe -> {
                finish()
            }
        }
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


    fun getDataFromNet() {
        TransactionPairsBean.getOptionalTransactionPair(userBean?.userId!!)
                .map {
                    it.apply {
                        result.map {
                            it.optional = 1
                        }
                    }
                }
                .compose(netTf())
                .subscribe({
                    if (it.success) {
                        editAdapter.group = it.result
                    }
                }, onError)
    }

    fun delete(item: TransactionPairsBean) {
        NRetrofit.instance
                .bibiService()
                .deleteOptional(item.id, userBean?.userId!!)
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        EventBus.getDefault().post(EventCenter<TransactionPairsBean>(EventCode.CODE_SELF_SELECT, item))
                        getDataFromNet()
                    }
                }, onError)
    }
}
