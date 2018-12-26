package com.nze.nzexchange.controller.my.asset.recharge

import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean2.RechargeHistoryBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.tools.getNColor
import com.nze.nzexchange.widget.recyclerview.Divider
import kotlinx.android.synthetic.main.activity_recharge_history.*

class RechargeHistoryActivity : NBaseActivity() {
    val rcv: RecyclerView by lazy { rcv_arh }
    val layoutManager: LinearLayoutManager by lazy { LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) }
    val rcvAdapter: RechargeHistoryAdapter by lazy {
        RechargeHistoryAdapter(this).apply {
            setDetailClick {
                skipActivity(RechargeDetailActivity::class.java)
            }
        }
    }
    override fun getRootView(): Int = R.layout.activity_recharge_history

    override fun initView() {
        rcv.layoutManager = layoutManager
        rcv.adapter = rcvAdapter
        rcvAdapter.setData(RechargeHistoryBean.getList())

        val divider = Divider(divider = ColorDrawable(getNColor(R.color.color_line)))
        divider.setHeight(2)
        rcv.addItemDecoration(divider)

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
