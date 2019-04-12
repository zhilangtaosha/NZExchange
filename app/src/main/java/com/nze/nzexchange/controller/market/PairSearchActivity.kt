package com.nze.nzexchange.controller.market

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.login.LoginActivity
import com.nze.nzexchange.database.dao.impl.PairDaoImpl
import com.nze.nzexchange.widget.clearedit.ClearableEditText
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_pair_search.*

class PairSearchActivity : NBaseActivity() {
    val searchEt: ClearableEditText by lazy { cet_search_aps }
    val cancelTv: TextView by lazy { tv_cancel_aps }
    val listView: ListView by lazy { lv_search_aps }

    val searchAdapter: PairSearchAdapter by lazy {
        PairSearchAdapter(this).apply {
            setSelfClick { item, position ->
                if (userBean == null) {
                    skipActivity(LoginActivity::class.java)
                    return@setSelfClick
                }

            }
        }
    }
    var userBean = UserBean.loadFromApp()
    val pairDao = PairDaoImpl()

    override fun getRootView(): Int = R.layout.activity_pair_search

    override fun initView() {

        listView.adapter = searchAdapter

        cancelTv.setOnClickListener {
            onBackPressed()
        }

        RxTextView.textChanges(searchEt)
                .map {
                    val list: MutableList<TransactionPairsBean> = mutableListOf()
                    list.addAll(pairDao.findByKey(it.toString()))
                    list.forEach {
                        NLog.i("id:${it.id} transactionPair:${it.transactionPair}")
                    }
                    list
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.isNotEmpty()) {
                        searchAdapter.group = it.toMutableList()
                    } else {

                    }
                }


    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_LOGIN_SUCCUSS || eventCenter.eventCode == EventCode.CODE_LOGOUT_SUCCESS) {
            userBean = UserBean.loadFromApp()
        }
    }

    fun refresh() {
        Flowable.create<List<TransactionPairsBean>>({

        }, BackpressureStrategy.DROP)
                .compose(netTfWithDialog())
                .subscribe {

                }
    }

    override fun getOverridePendingTransitionMode(): BaseActivity.TransitionMode = BaseActivity.TransitionMode.DEFAULT

    override fun isBindEventBusHere(): Boolean = true

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
    }

    override fun onNetworkDisConnected() {
    }

    override fun getContainerTargetView(): View? = null


}
