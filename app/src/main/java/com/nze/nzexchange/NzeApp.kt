package com.nze.nzexchange

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.multidex.MultiDex
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.ui.BaseApplication
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.tools.download.CustomSqliteActor
import com.nze.nzexchange.tools.selectlanguage.MultiLanguageUtil
import com.uc.crashsdk.export.CrashApi
import com.uuzuche.lib_zxing.activity.ZXingLibrary
import org.greenrobot.eventbus.EventBus
import zlc.season.rxdownload3.core.DownloadConfig
import zlc.season.rxdownload3.extension.ApkInstallExtension
import zlc.season.rxdownload3.extension.ApkOpenExtension

class NzeApp : BaseApplication() {
    var userId = "007"
    var userBean: UserBean? = null
    var activityNumber: Int = 0
    var isFirst: Boolean = true
    var mCrashApi: CrashApi? = null

    companion object {
        lateinit var instance: NzeApp;

    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        ZXingLibrary.initDisplayOpinion(this)
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)


        val builder = DownloadConfig.Builder.create(this)
                .setDebug(true)
                .enableDb(false)
                .setDbActor(CustomSqliteActor(this))
                .enableNotification(true)
                .addExtension(ApkInstallExtension::class.java)
                .addExtension(ApkOpenExtension::class.java)

        DownloadConfig.init(builder)
        MultiLanguageUtil.init(this)
        val isDebug: Boolean = true
        mCrashApi = CrashApi.createInstanceEx(applicationContext, "adx1", isDebug)
    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    val activityLifecycleCallbacks: ActivityLifecycleCallbacks = object : ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity?) {

        }

        override fun onActivityResumed(activity: Activity?) {
        }

        override fun onActivityStarted(activity: Activity?) {
            if (activityNumber == 0) {
                NLog.i("app回到前台$activityNumber");
                if (!isFirst) {
                    NLog.i("app回到前台>>>不是第一次")
                    EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_APP_TO_FRONT))
                } else {
                    isFirst = false
                    NLog.i("app回到前台>>>第一次打开应用")
                }
            }
            activityNumber++;
        }

        override fun onActivityDestroyed(activity: Activity?) {
        }

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        }

        override fun onActivityStopped(activity: Activity?) {
            activityNumber--;
            if (activityNumber == 0) {
                // app回到后台
                NLog.i("app回到后台");
            }
        }

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        }

    }
}