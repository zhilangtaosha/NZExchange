package com.nze.nzexchange.controller.login

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.LoginBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.Preferences
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.bibi.SoketService
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.tools.MD5Tool
import kotlinx.android.synthetic.main.activity_quick_login.*
import org.greenrobot.eventbus.EventBus

class QuickLoginActivity : NBaseActivity() {
    override fun getRootView(): Int = R.layout.activity_quick_login

    override fun initView() {
        btn1_aql.setOnClickListener {
            loginNet("16681805@qq.com", "nz123456")
        }
        btn2_aql.setOnClickListener {
            loginNet("18666666666", "123456")
        }
        btn3_aql.setOnClickListener {
            loginNet("8646494@qq.com", "123456")
        }
        btn4_aql.setOnClickListener {
            loginNet("zhouweiyong55@163.com", "nz123456")
        }
        btn5_aql.setOnClickListener {
            loginNet("13603041983", "nz123456")
        }
        btn6_aql.setOnClickListener {
            loginNet("18718527348", "aA123456")
        }
        btn7_aql.setOnClickListener {
            loginNet("zhangwengege@126.com", "123456aA")
        }
        btn8_aql.setOnClickListener {
            loginNet("13043489996", "wqn123456")
        }
        btn9_aql.setOnClickListener {
            loginNet("460717470@qq.com", "wqn123456")
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

    override fun getContainerTargetView(): View? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    fun loginNet(name: String, pwd: String) {
        val pwdStr = MD5Tool.getMd5_32(pwd)
        LoginBean.login(name, pwdStr)
                .compose(netTfWithDialog())
                .subscribe({
                    showToast(it.message)
                    if (it.result.token != null) {
                        binder?.auth(it.result.token.tokenReqVo.tokenUserKey)
                        NzeApp.instance.userBean = it.result.cloneToUserBean()
                        this@QuickLoginActivity.finish()
                    }

                }, {
                    showToast("登录失败")
                })
    }

    override fun onResume() {
        super.onResume()
        bindService(Intent(this, SoketService::class.java), connection, Context.BIND_AUTO_CREATE)
    }


    override fun onDestroy() {
        binder?.removeCallBack("quick")
        unbindService(connection)
        super.onDestroy()
    }

    var binder: SoketService.SoketBinder? = null
    var isBinder = false

    val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i("zwy", "quick onServiceDisconnected")
            isBinder = false
            binder = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i("zwy", "quick onServiceConnected")
            binder = service as SoketService.SoketBinder
            isBinder = true

            binder?.addAuthCallBack("quick") {
                if (!it) {
                    showToast("身份认证失败")
                } else {
                    showToast("身份认证成功")
                    EventBus.getDefault().post(EventCenter<Boolean>(EventCode.CODE_LOGIN_SUCCUSS, true))
                }
            }

        }
    }
}
