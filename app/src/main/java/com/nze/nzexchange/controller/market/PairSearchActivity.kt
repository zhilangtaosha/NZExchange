package com.nze.nzexchange.controller.market

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.Result
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.login.LoginActivity
import com.nze.nzexchange.database.dao.impl.PairDaoImpl
import com.nze.nzexchange.database.dao.impl.SearchHistoryDaoImpl
import com.nze.nzexchange.http.NRetrofit
import com.nze.nzexchange.widget.clearedit.ClearableEditText
import com.nze.nzexchange.widget.flowlayout.FlowTagLayout
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_pair_search.*


class PairSearchActivity : NBaseActivity() {
    val searchEt: ClearableEditText by lazy { cet_search_aps }
    val cancelTv: TextView by lazy { tv_cancel_aps }
    val listView: ListView by lazy { lv_search_aps }
    val historyLayout: RelativeLayout by lazy { layout_history_aps }
    val deleteIv: ImageView by lazy { iv_delete_aps }
    val flowLayout: FlowTagLayout by lazy { flayout_history_aps }
    val historyAdapter: HistoryFlowAdapter by lazy { HistoryFlowAdapter(this) }

    val searchAdapter: PairSearchAdapter by lazy {
        PairSearchAdapter(this).apply {
            setSelfClick { item, position ->
                if (userBean == null) {
                    skipActivity(LoginActivity::class.java)
                    return@setSelfClick
                }
                selftSelect(item, position)
            }
        }
    }
    var userBean = UserBean.loadFromApp()
    val pairDao = PairDaoImpl()
    val searchHistoryDao = SearchHistoryDaoImpl()

    override fun getRootView(): Int = R.layout.activity_pair_search

    override fun initView() {

        listView.adapter = searchAdapter

        cancelTv.setOnClickListener {
            onBackPressed()
        }

        RxTextView.textChanges(searchEt)
                .map {
                    searchByKey(it.toString())
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.isNotEmpty()) {
                        switchLayout(false)
                        searchAdapter.group = it.toMutableList()
                    } else {
                        switchLayout(true)
                    }
                }

        listView.setOnItemClickListener { parent, view, position, id ->
            val item = searchAdapter.getItem(position)
            KLineActivity.skip(this, item!!)
            searchHistoryDao.add(item)
            searchEt.setText("")
        }

        //搜索历史记录
        flowLayout.adapter = historyAdapter
        flowLayout.setOnTagClickListener { parent, view, position ->
            val pairsBean = historyAdapter.getItem(position)
            KLineActivity.skip(this, pairsBean!!)
        }

        //删除搜索记录
        deleteIv.setOnClickListener {
            searchHistoryDao.delete()
            historyAdapter.clearGroup(true)
            toggleShowEmpty(true, "暂无搜索记录", R.mipmap.no_data_search)
        }
    }

    fun searchByKey(key: String): MutableList<TransactionPairsBean> {
        val list: MutableList<TransactionPairsBean> = mutableListOf()
        if (key.isNotEmpty()) {
            list.addAll(pairDao.findByKey(key))
            list.forEach {
                NLog.i("id:${it.id} transactionPair:${it.transactionPair}")
            }
        } else {
            searchAdapter.clearGroup(true)
        }
        return list
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_LOGIN_SUCCUSS || eventCenter.eventCode == EventCode.CODE_LOGOUT_SUCCESS) {
            userBean = UserBean.loadFromApp()
            searchEt.setText("")
        }
    }

    override fun onResume() {
        super.onResume()
        showHistory()
    }

    fun showHistory() {
        switchLayout(true)
        val list = searchHistoryDao.findAll()
        if (list.size > 0) {
            stopAllView()
            historyAdapter.group = list.toMutableList()
        } else {
            toggleShowEmpty(true, "暂无搜索记录", R.mipmap.no_data_search)
        }
    }

    override fun getOverridePendingTransitionMode(): TransitionMode = TransitionMode.DEFAULT

    override fun isBindEventBusHere(): Boolean = true

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
    }

    override fun onNetworkDisConnected() {
    }

    override fun getContainerTargetView(): View? = flowLayout


    fun selftSelect(item: TransactionPairsBean, position: Int) {
        if (item.optional != 1) {
            addOptional(item.id, userBean?.userId!!)
                    .compose(netTfWithDialog())
                    .subscribe({
                        item.optional = 1
                        searchAdapter.notifyDataSetChanged()
                        showToast(it.message)
                        pairDao.update(item)
                    }, {
                        item.optional = 0
                        searchAdapter.notifyDataSetChanged()
                        pairDao.update(item)
                    })
        } else {
            deleteOptional(item.id, userBean?.userId!!)
                    .compose(netTfWithDialog())
                    .subscribe({
                        showToast(it.message)
                        item.optional = 0
                        searchAdapter.notifyDataSetChanged()
                        pairDao.update(item)
                    }, {
                        item.optional = 1
                        searchAdapter.notifyDataSetChanged()
                        pairDao.update(item)
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


    override fun onDestroy() {
        super.onDestroy()
        pairDao.close()
    }

    fun switchLayout(isHistoryShow: Boolean) {
        if (isHistoryShow) {
            listView.visibility = View.GONE
            historyLayout.visibility = View.VISIBLE
        } else {
            listView.visibility = View.VISIBLE
            historyLayout.visibility = View.GONE
        }
    }
}
