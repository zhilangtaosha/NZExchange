package com.nze.nzexchange.controller.market

import android.view.View
import android.widget.*
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
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.http.NRetrofit
import com.nze.nzexchange.widget.FlowLayout
import com.nze.nzexchange.widget.clearedit.ClearableEditText
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_pair_search.*
import android.widget.LinearLayout


class PairSearchActivity : NBaseActivity() {
    val searchEt: ClearableEditText by lazy { cet_search_aps }
    val cancelTv: TextView by lazy { tv_cancel_aps }
    val listView: ListView by lazy { lv_search_aps }
    val historyLayout: RelativeLayout by lazy { layout_history_aps }
    val deleteIv: ImageView by lazy { iv_delete_aps }
    val flowLayout: LinearLayout by lazy { flayout_history_aps }

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
        switchLayout(true)
        val list = searchHistoryDao.findAll()
        flowLayout.removeAllViews()
        list.forEach {
            NLog.i("history:id->${it.id} transactionPair->${it.transactionPair}")
            val tv = TextView(this)
            val LP_WW = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            tv.layoutParams = LP_WW
            tv.text = it.transactionPair
            flowLayout.addView(tv)
        }
        flowLayout.invalidate()
    }

    override fun getOverridePendingTransitionMode(): TransitionMode = TransitionMode.DEFAULT

    override fun isBindEventBusHere(): Boolean = true

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
    }

    override fun onNetworkDisConnected() {
    }

    override fun getContainerTargetView(): View? = null


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
