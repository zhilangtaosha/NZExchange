package com.nze.nzexchange.controller.otc


import android.graphics.drawable.ColorDrawable
import android.support.v4.app.Fragment
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.utils.EventCenter
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.OtcBean
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.tools.getNColor
import kotlinx.android.synthetic.main.fragment_otc_ad.view.*


/**
 * A simple [Fragment] subclass.
 *
 */
class OtcAdFragment : NBaseFragment() {
    val adAdapter: OtcAdAdapter by lazy {
        OtcAdAdapter(activity!!)
    }
    lateinit var ptrLv: PullToRefreshListView

    companion object {
        @JvmStatic
        fun newInstance() = OtcAdFragment()
    }

    override fun getRootView(): Int = R.layout.fragment_otc_ad

    override fun initView(rootView: View) {
        ptrLv = rootView.ptrlv_ad
        val listView = ptrLv.refreshableView
        listView.adapter = adAdapter
        listView.divider = ColorDrawable(getNColor(R.color.color_line))
        listView.dividerHeight = 1

        adAdapter.group = OtcBean.getList()

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
