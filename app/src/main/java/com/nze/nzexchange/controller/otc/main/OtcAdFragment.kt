package com.nze.nzexchange.controller.otc.main


import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.support.v4.app.Fragment
import android.view.View
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzeframework.widget.pulltorefresh.internal.PullToRefreshBase
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.*
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.config.RrefreshType
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.common.CheckPermission
import com.nze.nzexchange.controller.otc.PublishActivity
import com.nze.nzexchange.extend.setTxtColor
import com.nze.nzexchange.http.NRetrofit
import com.nze.nzexchange.tools.TimeTool
import com.nze.nzexchange.tools.getNColor
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.fragment_otc_ad.view.*
import retrofit2.http.Field


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
    var mMainCurrencyBean: MainCurrencyBean? = null
    var userBean: UserBean? = null
        get() {
            return UserBean.loadFromApp()
        }

    companion object {
        @JvmStatic
        fun newInstance() = OtcAdFragment()
    }

    override fun getRootView(): Int = R.layout.fragment_otc_ad

    override fun initView(rootView: View) {
        ptrLv = rootView.ptrlv_ad
        ptrLv.isPullLoadEnabled = true
        ptrLv.setOnRefreshListener(this)

        val listView = ptrLv.refreshableView
        listView.divider = ColorDrawable(getNColor(R.color.color_line))
        listView.dividerHeight = 1
        listView.adapter = adAdapter

//        adAdapter.onClick = { poolId, userId, transactionType ->
//            if (transactionType == FindSellBean.TRANSACTIONTYPE_BUY) {
//                cancelBuyOrder(poolId, userId, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
//                        .compose(netTfWithDialog())
//                        .subscribe({
//                            showToast(it.message)
//                            if (it.success)
//                                ptrLv.doPullRefreshing(true, 200)
//                        }, onError)
//            } else {
//                cancelSaleOrder(poolId, userId, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
//                        .compose(netTfWithDialog())
//                        .subscribe({
//                            showToast(it.message)
//                            if (it.success)
//                                ptrLv.doPullRefreshing(true, 200)
//                        }, onError)
//            }
//
//        }

        rootView.iv_add_ad.setOnClickListener {
            CheckPermission.getInstance()
                    .commonCheck(activity as NBaseActivity, CheckPermission.OTC_SEND_ADVERT, "发布广告需要完成以下设置，请检查", onPass = {
                        startActivity(Intent(activity, PublishActivity::class.java)
                                .putExtra(IntentConstant.PARAM_TOKENID, mMainCurrencyBean?.tokenId)
                                .putExtra(IntentConstant.PARAM_CURRENCY, mMainCurrencyBean?.tokenSymbol))
                    })
        }

    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_PULISH) {
//            ptrLv.doPullRefreshing(true, 200)
            page = 1
            refreshType = RrefreshType.PULL_DOWN
            getDataFromNet()
        }
        if (eventCenter.eventCode == EventCode.CODE_LOGIN_SUCCUSS) {
            page = 1
            refreshType = RrefreshType.PULL_DOWN
            getDataFromNet()
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

    override fun getContainerTargetView(): View? = ptrLv
    override fun refresh(tokenId: String?) {
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
        FindSellBean.getFromNet(UserBean.loadFromApp()?.userId!!, page, PAGE_SIZE)
                .compose(netTf())
                .subscribe({
//                    stopAllView()
                    val rList = it.result
                    when (refreshType) {
                        RrefreshType.INIT -> {
                            if (rList != null && rList.size > 0) {
                                findSellList.clear()
                                findSellList.addAll(rList)
                                adAdapter.group = rList
                            } else {
                                showNODataView("没有广告")
                            }
                        }
                        RrefreshType.PULL_DOWN -> {
                            if (rList != null && rList.size > 0) {
                                findSellList.clear()
                                findSellList.addAll(rList)
                                adAdapter.group = rList
                                ptrLv.onPullDownRefreshComplete()

                            } else {
                                showNODataView("没有广告")
                            }
                        }
                        RrefreshType.PULL_UP -> {
                            findSellList.addAll(rList)

                            adAdapter.addItems(rList)
                            ptrLv.onPullUpRefreshComplete()
                        }
                        else -> {
                            ptrLv.onPullDownRefreshComplete()
                            ptrLv.onPullUpRefreshComplete()
                        }
                    }
                    if (adAdapter.count >= it.pageSize) {
                        ptrLv.setHasMoreData(false)
                    } else {
                        ptrLv.setHasMoreData(true)
                    }
                }, {
                    ptrLv.onPullDownRefreshComplete()
                    ptrLv.onPullUpRefreshComplete()
                })
    }


    fun cancelSaleOrder(poolId: String,
                        userId: String,
                        tokenUserId: String,
                        tokenUserKey: String
    ): Flowable<Result<Boolean>> {
        return Flowable.defer {
            NRetrofit.instance
                    .buyService()
                    .cancelSaleOrder(poolId, userId, tokenUserId, tokenUserKey)
        }
    }

    fun cancelBuyOrder(
            poolId: String,
            userId: String,
            tokenUserId: String,
            tokenUserKey: String
    ): Flowable<Result<Boolean>> {
        return Flowable.defer {
            NRetrofit.instance
                    .buyService()
                    .cancelBuyOrder(poolId, userId, tokenUserId, tokenUserKey)
        }
    }

}
