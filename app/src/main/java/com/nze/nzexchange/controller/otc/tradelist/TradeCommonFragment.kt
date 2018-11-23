package com.nze.nzexchange.controller.otc.tradelist


import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzeframework.widget.pulltorefresh.internal.PullToRefreshBase
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.OtcOrder
import com.nze.nzexchange.bean.SubOrderInfoBean
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.tools.getNColor
import kotlinx.android.synthetic.main.common_ptrlv.view.*


/**
 * 交易单已取消、已完成
 *
 */
class TradeCommonFragment : NBaseFragment(), AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener<ListView> {

    lateinit var ptrLv: PullToRefreshListView
    var type: Int = 0
    var requestStatus = SubOrderInfoBean.REQUEST_NO_COMPLETE

    val commonAdapter: TradeCommonAdapter by lazy {
        TradeCommonAdapter(activity!!, type)
    }
    val noCompleteAdapter: TradeNoCompleteAdapter by lazy {
        TradeNoCompleteAdapter(activity!!)
    }

    companion object {
        const val PARAM_TYPE: String = "type"
        const val TYPE_COMPLETED: Int = 0
        const val TYPE_CANCEL: Int = 1
        const val TYPE_NO_COMPLETE: Int = 2

        @JvmStatic
        fun newInstance(type: Int) = TradeCommonFragment().apply {
            arguments = Bundle().apply {
                putInt(PARAM_TYPE, type)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getInt(PARAM_TYPE)
        }
    }

    override fun getRootView(): Int = R.layout.common_ptrlv


    override fun initView(rootView: View) {
        ptrLv = rootView.ptrlv_common
        ptrLv.setPullLoadEnabled(true)
        ptrLv.setOnRefreshListener(this)
        val listView = ptrLv.refreshableView
        listView.divider = ColorDrawable(getNColor(R.color.color_line))
        listView.dividerHeight = 1
        listView.onItemClickListener = this

        when (type) {
            TYPE_NO_COMPLETE -> {
                listView.adapter = noCompleteAdapter
            }
            TYPE_COMPLETED -> {
                listView.adapter = commonAdapter
            }
            TYPE_CANCEL -> {
                listView.adapter = commonAdapter
            }
        }


    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isBindEventBusHere(): Boolean = false

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkDisConnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContainerTargetView(): View? = null

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (type) {
            TYPE_NO_COMPLETE -> {

            }
            TYPE_COMPLETED, TYPE_CANCEL -> {
                startActivity(Intent(activity, TradeCommonDetailActivity::class.java).putExtra(PARAM_TYPE, type))
            }

        }
    }

    fun refresh(requstStatus: Int) {
        this.requestStatus = requestStatus
    }


    override fun onPullDownToRefresh(refreshView: PullToRefreshBase<ListView>?) {
    }

    override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>?) {
    }

    override fun getDataFromNet() {
        SubOrderInfoBean.findSubOrderPoolNet(NzeApp.instance.userId, page, PAGE_SIZE, requestStatus)
                .compose(netTf())
                .subscribe({

                }, onError)
    }
}
