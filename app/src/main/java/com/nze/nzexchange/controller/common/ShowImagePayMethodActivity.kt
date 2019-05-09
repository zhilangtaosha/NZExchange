package com.nze.nzexchange.controller.common

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NGlide
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.R
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.tools.FileTool
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_show_image_paymethod.*
import zlc.season.rxdownload3.RxDownload
import zlc.season.rxdownload3.core.*
import zlc.season.rxdownload3.extension.ApkInstallExtension

/**
 * 显示微信和支付等 图片支付方式
 */
class ShowImagePayMethodActivity : NBaseActivity(), View.OnClickListener {


    val payIv: ImageView by lazy { iv_pay }
    val closeIv: ImageView by lazy { iv_close_pay }
    val downIv: ImageView by lazy { iv_down_pay }
    private var currentStatus = Status()
    private var disposable: Disposable? = null
    lateinit var url: String
    lateinit var mission: Mission


    override fun getRootView(): Int = R.layout.activity_show_image_paymethod


    override fun initView() {
        intent?.let {
            url = it.getStringExtra(IntentConstant.PARAM_URL)
        }
        closeIv.setOnClickListener(this)
        downIv.setOnClickListener(this)
        NGlide.load(payIv, url)
        val tempName = FileTool.getTempName("jpg")
        val savePath = FileTool.getPotoPath()
        NLog.i(savePath)
        mission = Mission(url, tempName, savePath)
        disposable = RxDownload.create(mission, autoStart = false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { status ->
                    currentStatus = status
                    when (status) {
                        is Normal -> {
                            "开始"
                        }
                        is Suspend -> "已暂停"
                        is Waiting -> {
                            "等待中"
                        }
                        is Downloading -> {

                            "暂停"
                        }
                        is Failed -> {
                            dismissLoad()
                            "失败"
                        }
                        is Succeed -> {
                            dismissLoad()
                            showToast("下载成功")
                            "安装"
                        }
                        is ApkInstallExtension.Installing -> "安装中"
                        is ApkInstallExtension.Installed -> "打开"
                        else -> ""
                    }
                }

        RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET)
                .subscribe {
                    if (!it)
                        finish()
                }
        setWindowStatusBarColor(R.color.transparent)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_close_pay -> {
                disposable?.dispose()
                onBackPressed()
            }
            R.id.iv_down_pay -> {
                showLoad()
                RxDownload.start(mission).subscribe()

            }
        }
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
    }

    override fun getOverridePendingTransitionMode(): TransitionMode = TransitionMode.DEFAULT

    override fun isBindEventBusHere(): Boolean = false

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkDisConnected() {
    }

    override fun getContainerTargetView(): View? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        fun skip(context: Context, url: String) {
            context.startActivity(Intent(context, ShowImagePayMethodActivity::class.java)
                    .putExtra(IntentConstant.PARAM_URL, url))
        }
    }

}
