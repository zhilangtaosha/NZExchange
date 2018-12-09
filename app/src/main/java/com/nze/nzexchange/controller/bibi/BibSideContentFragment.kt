package com.nze.nzexchange.controller.bibi


import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzeframework.widget.pulltorefresh.internal.PullToRefreshBase

import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransactionPairBean
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.tools.getNColor
import kotlinx.android.synthetic.main.fragment_otc_content.view.*


/**
 * A simple [Fragment] subclass.
 *
 */
class BibSideContentFragment : NBaseFragment(), PullToRefreshBase.OnRefreshListener<ListView> {

    lateinit var ptrLv: PullToRefreshListView
    var mainCurrency: String? = null
    val adapter: BibiSideContentAdapter by lazy { BibiSideContentAdapter(activity!!) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mainCurrency = it.getString(IntentConstant.PARAM_CURRENCY)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(tokenId: String) =
                BibSideContentFragment().apply {
                    arguments = Bundle().apply {
                        putString(IntentConstant.PARAM_CURRENCY, tokenId)
                    }
                }
    }

    override fun getRootView(): Int = R.layout.fragment_otc_content

    override fun initView(rootView: View) {
        ptrLv = rootView.plv_foc
        ptrLv.setPullLoadEnabled(false)
        ptrLv.setOnRefreshListener(this)
        val listView = ptrLv.refreshableView
        listView.divider = ColorDrawable(getNColor(R.color.color_line))
        listView.dividerHeight = 1
        listView.adapter = adapter
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
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


    override fun onPullDownToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        getDataFromNet()
    }

    override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>?) {
    }

    override fun onFirstRequest() {
        getDataFromNet()
    }

    override fun getDataFromNet() {
        NLog.i("mainCurrency>>>$mainCurrency")
        if (mainCurrency != null) {
            TransactionPairsBean.getTransactionPairs(mainCurrency!!)
                    .compose(netTf())
                    .subscribe({
                        if (it.success) {
                            adapter.group = it.result
                            ptrLv.onPullDownRefreshComplete()
                        }
                    }, {
                        NLog.i("")
                    })
        }
    }
}
