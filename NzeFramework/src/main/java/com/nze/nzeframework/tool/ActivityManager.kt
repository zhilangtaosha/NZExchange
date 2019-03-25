package com.nze.nzeframework.tool

import android.app.Activity
import android.content.Context
import com.nze.nzeframework.ui.BaseActivity
import java.util.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/3/25
 */
class ActivityManager {
    companion object {
        val instance: ActivityManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { ActivityManager() }
    }
    private val activityStack: Stack<BaseActivity> by lazy { Stack<BaseActivity>() }
    private var mCurrentAct: Activity? = null
    /**
     * 获取当前Activity栈中元素个数
     */
    fun getCount(): Int {
        return activityStack.size
    }

    /**
     * 添加Activity到栈
     */
    fun addActivity(activity: BaseActivity) {
        activityStack.add(activity)
    }

    /**
     * 获取当前Activity（栈顶Activity）
     */
    fun topActivity(): Activity? {
        if (activityStack == null) {
            throw NullPointerException(
                    "Activity stack is Null,your Activity must extend BaseActivity")
        }
        if (activityStack.isEmpty()) {
            return null
        }
        val activity = activityStack.lastElement()
        return activity as Activity
    }

    /**
     * 获取当前Activity（栈顶Activity） 没有找到则返回null
     */
    fun findActivity(cls: Class<*>): Activity? {
        var activity: BaseActivity? = null
        for (aty in activityStack) {
            if (aty.javaClass.equals(cls)) {
                activity = aty
                break
            }
        }
        return activity
    }

    /**
     * 结束当前Activity（栈顶Activity）
     */
    fun finishActivity() {
        val activity = activityStack.lastElement()
        finishActivity(activity as Activity)
    }

    /**
     * 结束指定的Activity(重载)
     */
    fun finishActivity(activity: Activity?) {
        var activity = activity
        if (activity != null) {
            activityStack.remove(activity)
            activity.finish()
            activity = null
        }
    }

    /**
     * 结束指定的Activity(重载)
     */
    fun finishActivity(cls: Class<*>) {
        for (activity in activityStack) {
            if (activity.javaClass.equals(cls)) {
                finishActivity(activity as Activity)
            }
        }
    }

    /**
     * 关闭除了指定activity以外的全部activity 如果cls不存在于栈中，则栈全部清空
     *
     * @param cls
     */
    fun finishOthersActivity(cls: Class<*>) {
        for (activity in activityStack) {
            if (!activity.javaClass.equals(cls)) {
                finishActivity(activity as Activity)
            }
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        var i = 0
        val size = activityStack.size
        while (i < size) {
            if (null != activityStack[i]) {
                (activityStack[i] as Activity).finish()
            }
            i++
        }
        activityStack.clear()
    }


    /**
     * 应用程序退出
     */
    fun AppExit(context: Context) {
        try {
            finishAllActivity()
            Runtime.getRuntime().exit(0)
        } catch (e: Exception) {
            Runtime.getRuntime().exit(-1)
        }

    }

    /**
     * @Description: 弹出到指定Activity 移除栈中此Activity上面的所有activity
     * @param cls
     */
    fun Pop2Activity(cls: Class<*>) {
        if (null == findActivity(cls)) {
            return
        }
        mCurrentAct = activityStack.peek() as Activity
//        while (null != mCurrentAct &&!mCurrentAct.javaClass.equals(cls)) {
//            // mCurrentAct.finish();
//            finishActivity(mCurrentAct)
//            mCurrentAct = activityStack.peek() as Activity
//        }
    }

}