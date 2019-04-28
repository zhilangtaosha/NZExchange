package com.nze.nzexchange.controller.common

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.nze.nzexchange.config.AccountType.Companion.OTC

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:项目的权限判断
 * @创建时间：2019/4/28
 */
class VerifyService : Service() {
    companion object {
        val VERFY_TYPE = "type"
        val OTC_AD = 0//otc发布广告
        val OTC_BUY = 1//otc购买
        val OTC_SALE = 2//otc出售
        val BIBI_RECHARGE = 3//充币
        val BIBI_WITHDRAW = 4//提币
    }

    var startId = 0

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        this.startId = startId
        val bundle = intent?.extras
        val type = bundle?.getInt(VERFY_TYPE)
        when (type) {
            OTC_AD -> {
            }
            OTC_BUY -> {
            }
            OTC_BUY -> {
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}