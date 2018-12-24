package com.nze.nzexchange.controller.my.asset.withdraw

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.BuildConfig
import com.nze.nzexchange.R
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

    val builder by lazy {
        OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(RetryIntercepter(3))
                .apply {
                    val interceptor = HttpLoggingInterceptor()
                    interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                    addInterceptor(interceptor)
                }
    }
    override fun getRootView(): Int = R.layout.activity_withdraw_history

    override fun initView() {

        val service = Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("http://192.168.1.130:2000")
                .build()
                .create(AssetService::class.java)

        val obj: JSONObject = JSONObject()
        obj.put("method", "query_translist")
        obj.put("jsonrpc", "2.0")
        val arr = JSONArray()
        arr.put("0x1f2143c74b17d70f0a98706f826c25531e88820d")
        arr.put("*")
        arr.put("*")
        arr.put("OCC")
        obj.put("params", arr)

        val body:RequestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),obj.toString())

        btn1_awh.setOnClickListener {
            service.zhangNet(body)
                    .compose(netTfWithDialog())
                    .subscribe({
                        NLog.i(it.toString())
                    },{
                        NLog.i("")
                    })

        }
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
