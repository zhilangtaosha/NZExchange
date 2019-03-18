package com.nze.nzexchange.controller.my.authentication

import android.Manifest
import android.content.Intent
import android.hardware.Camera
import android.media.MediaRecorder
import android.view.SurfaceHolder
import android.view.View
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import kotlinx.android.synthetic.main.activity_video_authentication.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class VideoAuthenticationActivity : NBaseActivity() {


    private val recordVideoTv: TextView by lazy { tv_record_video_ava }
    private val tipTv: TextView by lazy { tv_tip_ava }
    private var surfaceHolder: SurfaceHolder? = null
    private var mediaRecorder: MediaRecorder? = null
    private var mCamera: Camera? = null
    private val CAMERA_ID = 0
    val CODE_RECORD_REQUEST = 0

    override fun getRootView(): Int = R.layout.activity_video_authentication

    override fun initView() {

        recordVideoTv.setOnClickListener {
            startActivityForResult(Intent(this@VideoAuthenticationActivity, VideoRecordActivity::class.java), CODE_RECORD_REQUEST)
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


    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)

    }

}
