 package com.nze.nzexchange.controller.otc

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.utils.EventCenter
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.OtcBean
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.tools.getNColor
import kotlinx.android.synthetic.main.fragment_otc_content.view.*




class OtcContentFragment : NBaseFragment() {
    lateinit var ptrLv: PullToRefreshListView
    private var type: Int = 0
    private var buyData = OtcBean.getList()
    private val buyAdapter: OtcBuyAdapter by lazy {
        OtcBuyAdapter(activity!!,type)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getInt(PARAM_TYPE)
        }
    }

    companion object {
        const val PARAM_TYPE = "type"
        const val TYPE_BUY = 0
        const val TYPE_SALE = 1
        @JvmStatic
        fun newInstance(type: Int) =
                OtcContentFragment().apply {
                    arguments = Bundle().apply {
                        putInt(PARAM_TYPE, type)
                    }
                }
    }

    override fun getRootView(): Int = R.layout.fragment_otc_content

    override fun initView(rootView: View) {
        ptrLv = rootView.plv_foc
        ptrLv.setPullLoadEnabled(true)
        val listView = ptrLv.refreshableView
        listView.divider = ColorDrawable(getNColor(R.color.color_line))
        listView.dividerHeight = 1


        listView.adapter = buyAdapter
        buyAdapter.group=buyData

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
}