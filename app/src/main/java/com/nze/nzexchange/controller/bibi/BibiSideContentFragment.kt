package com.nze.nzexchange.controller.bibi


import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzeframework.widget.pulltorefresh.internal.PullToRefreshBase
import com.nze.nzexchange.NzeApp

import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.http.NRetrofit
import com.nze.nzexchange.bean.Result
import com.nze.nzexchange.bean.SoketRankBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.controller.login.LoginActivity
import com.nze.nzexchange.tools.getNColor
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.fragment_otc_content.view.*
import org.greenrobot.eventbus.EventBus


/**
 * A simple [Fragment] subclass.
 *
 */
class BibiSideContentFragment : NBaseFragment(), PullToRefreshBase.OnRefreshListener<ListView>, BibiSideContentAdapter.OnItemClickListener {

    lateinit var ptrLv: PullToRefreshListView
    var mainCurrency: String? = null
    val adapter: BibiSideContentAdapter by lazy { BibiSideContentAdapter(activity!!) }

    var userBean: UserBean? = UserBean.loadFromApp()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mainCurrency = it.getString(IntentConstant.PARAM_CURRENCY)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(currency: String) =
                BibiSideContentFragment().apply {
                    arguments = Bundle().apply {
                        putString(IntentConstant.PARAM_CURRENCY, currency)
                    }
                }
    }

    override fun getRootView(): Int = R.layout.fragment_otc_content

    override fun initView(rootView: View) {
        ptrLv = rootView.plv_foc
        ptrLv.isPullRefreshEnabled = false
        ptrLv.isPullLoadEnabled = false
        ptrLv.setOnRefreshListener(this)
        val listView = ptrLv.refreshableView
        listView.divider = ColorDrawable(getNColor(R.color.color_line))
        listView.dividerHeight = 1
        listView.setSelector(R.drawable.selector_common_lv)
        listView.adapter = adapter

        adapter.onItemClick = this
        listView.setOnItemClickListener { parent, view, position, id ->
            val item = adapter.getItem(position)
            val pairsBean = TransactionPairsBean()
            pairsBean.setValueFromRankBean(item!!)
            EventBus.getDefault().post(EventCenter<TransactionPairsBean>(EventCode.CODE_SELECT_TRANSACTIONPAIR, pairsBean))
        }

    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_SELF_SELECT) {
//            val pairsBean = eventCenter.data as TransactionPairsBean
//            if (mainCurrency == "自选") {
//                if (pairsBean.optional == 1) {
//                    adapter.addItem(pairsBean)
//                } else {
//                    val list = adapter.group
//                    adapter.group = list.filter {
//                        it.id != pairsBean.id
//                    }.toMutableList()
//                }
//            } else {
//                val list = adapter.group
//                val index = list.indexOfFirst {
//                    it.id == pairsBean.id
//                }
//                if (index >= 0) {
//                    list.get(index).optional = pairsBean.optional
//                    adapter.notifyDataSetChanged()
//                }
//            }
        }
        if (eventCenter.eventCode == EventCode.CODE_LOGIN_SUCCUSS) {
            userBean = UserBean.loadFromApp()
//            refreshData()
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

    override fun getContainerTargetView(): View? = null


    fun refreshData() {
        ptrLv.doPullRefreshing(true, 200)
    }

    override fun onPullDownToRefresh(refreshView: PullToRefreshBase<ListView>?) {
    }

    override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>?) {
    }

    override fun onFirstRequest() {
    }

    fun refreshData(marketList: MutableList<SoketRankBean>) {
        adapter.group = marketList
    }

    fun getOptionalFromNet() {
        TransactionPairsBean.getOptionalTransactionPair(userBean?.userId!!)
                .compose(netTfWithDialog())
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

                    }
                }, onError)
    }


    override fun selftSelect(item: SoketRankBean, position: Int) {
        if (item.optional != 1) {
            addOptional(item.market, userBean?.userId!!)
                    .compose(netTfWithDialog())
                    .subscribe({
                        //                        item.optional = 1
                        if (it.success)
                            EventBus.getDefault().post(EventCenter<TransactionPairsBean>(EventCode.CODE_SELF_SELECT, TransactionPairsBean()))
//                        adapter.notifyDataSetChanged()
                        showToast(it.message)
                    }, {
                        //                        item.optional = 0
//                        adapter.notifyDataSetChanged()
                    })
        } else {
            deleteOptional(item.market, userBean?.userId!!)
                    .compose(netTfWithDialog())
                    .subscribe({
                        showToast(it.message)
//                        item.optional = 0
                        if (it.success)
                            EventBus.getDefault().post(EventCenter<TransactionPairsBean>(EventCode.CODE_SELF_SELECT, TransactionPairsBean()))
//                        adapter.notifyDataSetChanged()
                    }, {
                        //                        item.optional = 1
//                        adapter.notifyDataSetChanged()
                    })
        }
    }

    //添加自选
    fun addOptional(currencyId: String, userId: String): Flowable<Result<String>> {
        return Flowable.defer {
            NRetrofit.instance
                    .bibiService()
                    .addOptional(currencyId, userId)
        }
    }

    //删除自选
    fun deleteOptional(currencyId: String, userId: String): Flowable<Result<String>> {
        return Flowable.defer {
            NRetrofit.instance
                    .bibiService()
                    .deleteOptional(currencyId, userId)
        }
    }
}
