package com.nze.nzexchange.controller.login

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.LoginBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.controller.base.NBaseActivity
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
            loginNet("13043489996", "1qaz1QAZ")
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
                        NzeApp.instance.userBean = it.result.cloneToUserBean()
                        EventBus.getDefault().post(EventCenter<Boolean>(EventCode.CODE_LOGIN_SUCCUSS, true))
                        this@QuickLoginActivity.finish()
                    }

                }, {
                    showToast("登录失败")
                })
    }
}
