package com.nze.nzexchange.controller.otc.main


import android.graphics.drawable.ColorDrawable
import android.support.v4.app.Fragment
import android.view.View
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzeframework.widget.pulltorefresh.internal.PullToRefreshBase
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.FindSellBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.RrefreshType
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.http.NRetrofit
import com.nze.nzexchange.bean.Result
import com.nze.nzexchange.tools.TimeTool
import com.nze.nzexchange.tools.getNColor
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.fragment_otc_ad.view.*


/**
 * A simple [Fragment] subclass.
 *
 */
class OtcAdFragment : NBaseFragment(), IOtcView, PullToRefreshBase.OnRefreshListener<ListView> {


    private val findSellList: MutableList<FindSellBean> by lazy {
        mutableListOf<FindSellBean>()
    }

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
        ptrLv.isPullLoadEnabled = false
        ptrLv.isScrollLoadEnabled = false
        ptrLv.setOnRefreshListener(this)

        val listView = ptrLv.refreshableView
        listView.adapter = adAdapter
        listView.divider = ColorDrawable(getNColor(R.color.color_line))
        listView.dividerHeight = 1

        adAdapter.onClick = { poolId, userId ->
            cancelNet(poolId, userId)
                    .compose(netTfWithDialog())
                    .subscribe({
                        showToast(it.message)
                        if (it.success)
                            ptrLv.doPullRefreshing(true, 200)
                    }, onError)
        }
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_PULISH)
            ptrLv.doPullRefreshing(true, 200)
    }

    override fun isBindEventBusHere(): Boolean = true

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkDisConnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContainerTargetView(): View? = null
    override fun refresh(tokenId: String) {
        ptrLv.doPullRefreshing(true, 200)
    }

    override fun onPullDownToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        page = 1
        refreshType = RrefreshType.PULL_DOWN
        getDataFromNet()
    }

    override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        page++
        refreshType = RrefreshType.PULL_UP
        getDataFromNet()
    }


    override fun getDataFromNet() {
        FindSellBean.getFromNet(NzeApp.instance.userId, page, PAGE_SIZE)
                .compose(netTf())
                .subscribe({
                    val rList = it.result
                    when (refreshType) {
                        RrefreshType.INIT -> {
                            adAdapter.group = rList
                        }
                        RrefreshType.PULL_DOWN -> {
                            adAdapter.group = rList
                            ptrLv.setLastUpdatedLabel(TimeTool.getLastUpdateTime())
                            ptrLv.onPullDownRefreshComplete()
                            if (adAdapter.count >= it.pageSize) {
                                ptrLv.setHasMoreData(false)
                            } else {
                                ptrLv.setHasMoreData(true)
                            }
                        }
                        RrefreshType.PULL_UP -> {
                            adAdapter.addItems(rList)
                            ptrLv.onPullUpRefreshComplete()
                            if (adAdapter.count >= it.pageSize) {
                                ptrLv.setHasMoreData(false)
                            } else {
                                ptrLv.setHasMoreData(true)
                            }
                        }
                        else -> {
                        }
                    }
                }, {
                    ptrLv.onPullDownRefreshComplete()
                    ptrLv.onPullUpRefreshComplete()
                })
    }


    fun cancelNet(poolId: String, userId: String): Flowable<Result<Boolean>> {
        return Flowable.defer {
            NRetrofit.instance
                    .buyService()
                    .cancelOrder(poolId, userId)
        }
    }
}
