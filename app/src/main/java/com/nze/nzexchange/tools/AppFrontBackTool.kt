package com.nze.nzexchange.tools

import android.app.Activity
import android.os.Bundle
import android.app.Application



/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/3/25
 */
class AppFrontBackTool {

    private var mOnAppStatusListener: OnAppStatusListener? = null
    
    /**
     * 注册状态监听，仅在Application中使用
     *
     * @param application
     * @param listener
     */
    fun register(application: Application, listener: OnAppStatusListener) {
        mOnAppStatusListener = listener
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    fun unRegister(application: Application) {
        application.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    private val activityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        //打开的Activity数量统计
        private var activityStartCount = 0

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle) {

        }

        override fun onActivityStarted(activity: Activity) {
            activityStartCount++
            //数值从0变到1说明是从后台切到前台
            if (activityStartCount == 1) {
                //从后台切到前台
                if (mOnAppStatusListener != null) {
                    mOnAppStatusListener!!.onFront()
                }
            }
        }

        override fun onActivityResumed(activity: Activity) {

        }

        override fun onActivityPaused(activity: Activity) {

        }

        override fun onActivityStopped(activity: Activity) {
            activityStartCount--
            //数值从1到0说明是从前台切到后台
            if (activityStartCount == 0) {
                //从前台切到后台
                if (mOnAppStatusListener != null) {
                    mOnAppStatusListener!!.onBack()
                }
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

        }

        override fun onActivityDestroyed(activity: Activity) {

        }
    }

    interface OnAppStatusListener {
        fun onFront()

        fun onBack()
    }
}