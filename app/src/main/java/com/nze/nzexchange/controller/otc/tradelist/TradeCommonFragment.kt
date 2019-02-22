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
import com.nze.nzexchange.bean.Accmoney
import com.nze.nzexchange.bean.OtcOrder
import com.nze.nzexchange.bean.SubOrderInfoBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.config.RrefreshType
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.otc.SaleConfirmActivity
import com.nze.nzexchange.tools.TimeTool
import com.nze.nzexchange.tools.getNColor
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.common_ptrlv.view.*
import java.util.concurrent.TimeUnit


/**
 * 交易单已取消、已完成
 *
 */
class TradeCommonFragment : NBaseFragment(), AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener<ListView> {

    lateinit var ptrLv: PullToRefreshListView
    var type: Int = 0
    var requestStatus = SubOrderInfoBean.REQUEST_NO_COMPLETE

    val commonAdapter: TradeCommonAdapter by lazy {
        TradeCommonAdapter(activity!!, type, this)
    }
    val noCompleteAdapter: TradeNoCompleteAdapter by lazy {
        TradeNoCompleteAdapter(activity!!, this)
    }
    val list: MutableList<SubOrderInfoBean> by lazy {
        mutableListOf<SubOrderInfoBean>()
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
        if (type == TYPE_NO_COMPLETE && eventCenter.eventCode == EventCode.CODE_CONFIRM_PAY)
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

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (type) {
            TYPE_NO_COMPLETE -> {
                val item = noCompleteAdapter.getItem(position)
                startActivity(Intent(activity, SaleConfirmActivity::class.java)
                        .putExtra(IntentConstant.PARAM_SUBORDERID, item?.suborderId))
            }
            TYPE_COMPLETED, TYPE_CANCEL -> {
                val item = commonAdapter.getItem(position)
                startActivity(Intent(activity, TradeCommonDetailActivity::class.java)
                        .putExtra(PARAM_TYPE, type)
                        .putExtra(IntentConstant.PARAM_SUBORDERID, item?.suborderId))

            }

        }
    }

    fun refresh(requstStatus: Int) {
        this.requestStatus = requstStatus
        ptrLv.doPullRefreshing(true, 200)
    }


    override fun onPullDownToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        refreshType = RrefreshType.PULL_DOWN
        page = 1
        getDataFromNet()
    }

    override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        refreshType = RrefreshType.PULL_UP
        page++
        getDataFromNet()
    }

    override fun getDataFromNet() {
        SubOrderInfoBean.findSubOrderPoolNet(UserBean.loadFromApp()?.userId!!, page, PAGE_SIZE, requestStatus)
                .compose(netTf())
                .subscribe({
                    var rList = it.result.map {
                        it.phoneTime = System.currentTimeMillis()
//                        it.suborderOverTime = 60 * 60
                        it
                    }.toMutableList()
                    when (refreshType) {
                        RrefreshType.INIT -> {
                            when (type) {
                                TYPE_NO_COMPLETE -> {
                                    noCompleteAdapter.group = rList
                                }
                                TYPE_COMPLETED, TYPE_CANCEL -> {
                                    commonAdapter.group = rList
                                }
                            }
                        }
                        RrefreshType.PULL_DOWN -> {
                            ptrLv.setLastUpdatedLabel(TimeTool.getLastUpdateTime())
                            ptrLv.onPullDownRefreshComplete()
                            when (type) {
                                TYPE_NO_COMPLETE -> {
                                    noCompleteAdapter.group = rList
                                    countdown()
                                }
                                TYPE_COMPLETED, TYPE_CANCEL -> {
                                    commonAdapter.group = rList
                                }
                            }
                        }
                        RrefreshType.PULL_UP -> {
                            ptrLv.onPullUpRefreshComplete()
                            when (type) {
                                TYPE_NO_COMPLETE -> {
                                    noCompleteAdapter.addItems(rList)
                                }
                                TYPE_COMPLETED, TYPE_CANCEL -> {
                                    commonAdapter.addItems(rList)
                                }
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


    fun countdown() {
        Flowable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe {
                    noCompleteAdapter.notifyDataSetChanged()
                }
    }
}
