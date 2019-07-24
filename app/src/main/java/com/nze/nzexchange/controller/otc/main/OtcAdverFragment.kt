package com.nze.nzexchange.controller.otc.main


import android.view.View
import android.widget.AbsListView
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzeframework.widget.pulltorefresh.internal.PullToRefreshBase
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.FindSellBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.RrefreshType
import com.nze.nzexchange.controller.base.NBaseFragment
import kotlinx.android.synthetic.main.fragment_otc_ad.view.*


class OtcAdverFragment : NBaseFragment(), PullToRefreshBase.OnRefreshListener<ListView>, AbsListView.OnScrollListener {
    override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
//        if (userBean != null && mTotal > totalItemCount && firstVisibleItem + visibleItemCount >= totalItemCount - 3) {
//            RrefreshType.PULL_UP
//            page++
//            getData()
//        }
    }

    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
    }

    var mTotal = 0
    lateinit var ptrLv: PullToRefreshListView
    lateinit var listView: ListView
    var userBean: UserBean? = null
        get() {
            return UserBean.loadFromApp()
        }
    val adAdapter: OtcAdAdapter by lazy {
        OtcAdAdapter(activity!!)
    }
    val list: MutableList<FindSellBean> by lazy { mutableListOf<FindSellBean>() }

    companion object {
        @JvmStatic
        fun newInstance() = OtcAdverFragment()
    }

    override fun getRootView(): Int = R.layout.fragment_otc_adver

    override fun initView(rootView: View) {
        ptrLv = rootView.ptrlv_ad
        ptrLv.isPullLoadEnabled = true
        ptrLv.setOnRefreshListener(this)
//        ptrLv.isScrollLoadEnabled = true
        listView = ptrLv.refreshableView
        listView.adapter = adAdapter
        adAdapter.group = list

//        ptrLv.setOnScrollListener(this)
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_LOGIN_SUCCUSS) {
            page = 1
            refreshType = RrefreshType.PULL_DOWN
            getData()
        }
    }

    override fun isBindEventBusHere(): Boolean = true

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


    override fun onPullDownToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        page = 1
        refreshType = RrefreshType.PULL_DOWN
        ptrLv.setHasMoreData(true)
        getData()
    }

    override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        page++
        refreshType = RrefreshType.PULL_UP
        getData()
    }

    fun getData() {
        FindSellBean.getFromNet(UserBean.loadFromApp()?.userId!!, page, PAGE_SIZE)
                .compose(netTf())
                .subscribe({
                    val rList = it.result
                    mTotal = it.totalSize
                    when (refreshType) {
                        RrefreshType.INIT -> {
//                            adAdapter.group = rList
                            list.clear()
                            list.addAll(rList)
                        }
                        RrefreshType.PULL_DOWN -> {
                            list.clear()
//                            adAdapter.group = rList
                            list.addAll(rList)
                            ptrLv.onPullDownRefreshComplete()

                        }
                        RrefreshType.PULL_UP -> {
//                            adAdapter.addItems(rList)
                            list.addAll(rList)
                            ptrLv.onPullUpRefreshComplete()
//                            listView.scrollBy(0,-100)
                        }
                        else -> {
                        }
                    }
                    if (adAdapter.count >= it.totalSize) {
                        ptrLv.setHasMoreData(false)
                    }
//                    else {
//                        ptrLv.setHasMoreData(true)
//                    }
                }, {
                    ptrLv.onPullDownRefreshComplete()
                    ptrLv.onPullUpRefreshComplete()
                })
    }
}
