package com.nze.nzexchange.tools

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.nze.nzeframework.tool.NFileTool
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.tool.SystemTool
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.controller.main.MainActivity
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.PrintWriter


/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/6/3
 */
class CrashHandler private constructor() : Thread.UncaughtExceptionHandler {
    private val DEFAULT_LOG_DIR = "log"
    // log文件的后缀名
    private val FILE_NAME_SUFFIX = ".log"
    private val mDefaultCrashHandler: Thread.UncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
    private val application: Application = NzeApp.instance
    private lateinit var mContext: Context

    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    fun init(context: Context) {
        mContext = context
    }

    companion object {
        private var instance: CrashHandler? = null
//        @Synchronized
//        fun create(context: Context): CrashHandler {
//            if (instance == null) {
//                instance = CrashHandler(context)
//            }
//            return instance!!
//        }

        @Synchronized
        fun create(): CrashHandler {
            if (instance == null) {
                instance = CrashHandler()
            }
            return instance!!
        }
    }

    @SuppressLint("WrongConstant")
    override fun uncaughtException(t: Thread?, e: Throwable?) {
        NLog.i("出错了。。。")
        try {
//            saveToFile(e!!)
        } catch (e: Exception) {
        } finally {
            e?.printStackTrace()
//            ActivityManager.instance.AppExit()
            val intent = Intent(application, MainActivity::class.java)
            val restartIntent = PendingIntent.getActivity(mContext, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK)
            val mgr = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, restartIntent)
            android.os.Process.killProcess(android.os.Process.myPid())


        }
    }

    @Throws(Exception::class)
    private fun saveToFile(ex: Throwable) {
        try {
            val file = NFileTool.getSaveFile(application, "${DEFAULT_LOG_DIR}", "${FileTool.getTempName(FILE_NAME_SUFFIX)}")
            NLog.i(file.absolutePath)
            val pw = PrintWriter(BufferedWriter(FileWriter(file)))
            // 导出发生异常的时间
            pw.println(SystemTool.getDataTime("yyyy-MM-dd-HH-mm-ss"))
            // 导出手机信息
            dumpPhoneInfo(pw)

            pw.println()
            // 导出异常的调用栈信息
            ex.printStackTrace(pw)
            pw.close()
        } catch (e: Exception) {
            throw(Exception())
        }
    }

    @Throws(PackageManager.NameNotFoundException::class)
    private fun dumpPhoneInfo(pw: PrintWriter) {
        // 应用的版本名称和版本号
        val pm = application.packageManager
        val pi = pm.getPackageInfo(application.packageName, PackageManager.GET_ACTIVITIES)
        pw.print("App Version: ")
        pw.print(pi.versionName)
        pw.print('_')
        pw.println(pi.versionCode)
        pw.println()

        // android版本号
        pw.print("OS Version: ")
        pw.print(Build.VERSION.RELEASE)
        pw.print("_")
        pw.println(Build.VERSION.SDK_INT)
        pw.println()

        // 手机制造商
        pw.print("Vendor: ")
        pw.println(Build.MANUFACTURER)
        pw.println()

        // 手机型号
        pw.print("Model: ")
        pw.println(Build.MODEL)
        pw.println()

        // cpu架构
        pw.print("CPU ABI: ")
        pw.println(Build.CPU_ABI)
        pw.println()
    }

}