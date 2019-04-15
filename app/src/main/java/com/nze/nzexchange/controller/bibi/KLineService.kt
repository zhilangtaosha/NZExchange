package com.nze.nzexchange.controller.bibi

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

/**
 * https://blog.csdn.net/sun20209527/article/details/78653286
 * https://www.cnblogs.com/wicrecend/p/5288527.html
 */
class KLineService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    class KBinder : Binder() {


    }
}
