package com.nze.nzexchange.controller.my.asset.withdraw

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.BuildConfig
import com.nze.nzexchange.R
import com.nze.nzexchange.bean2.WithdrawHistoryBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.http.AssetService
import com.nze.nzexchange.http.RetryIntercepter
import com.nze.nzexchange.http.UserService
import kotlinx.android.synthetic.main.activity_withdraw_history.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class WithdrawHistoryActivity : NBaseActivity() {
    val listView: ListView by lazy {
        listView_awh.apply {
            setOnItemClickListener { parent, view, position, id ->
                skipActivity(WithdrawDetailActivity::class.java)
            }
        }
    }
    val historyAdapter: WithdrawHistoryAdapter by lazy { WithdrawHistoryAdapter(this) }

    override fun getRootView(): Int = R.layout.activity_withdraw_history

    override fun initView() {
        listView.adapter = historyAdapter
        historyAdapter.group = WithdrawHistoryBean.getList()
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOverridePendingTransitionMode(): TransitionMode = TransitionMode.DEFAULT

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
