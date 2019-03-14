package com.nze.nzexchange.controller.my.authentication

import android.Manifest
import android.hardware.Camera
import android.media.MediaRecorder
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.tools.FileTool
import kotlinx.android.synthetic.main.activity_video_authentication.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class VideoAuthenticationActivity : NBaseActivity(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks, SurfaceHolder.Callback {

    companion object {
        const val RC_CAMERA_PERM = 110
    }

    private val recordVideoTv: TextView by lazy { tv_record_video_ava }
    private val surfaceView: SurfaceView by lazy { sfv_ava }
    private val tipTv: TextView by lazy { tv_tip_ava }
    private var surfaceHolder: SurfaceHolder? = null
    private var mediaRecorder: MediaRecorder? = null
    private var mCamera: Camera? = null
    private val CAMERA_ID = 0

    override fun getRootView(): Int = R.layout.activity_video_authentication

    override fun initView() {
        initRecorder()

        recordVideoTv.setOnClickListener {

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

    @AfterPermissionGranted(RC_CAMERA_PERM)
    public fun initRecorder() {
        if (hasCameraPermission()) {
            surfaceHolder = surfaceView.holder
            surfaceHolder?.addCallback(this@VideoAuthenticationActivity)
            surfaceHolder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)


            mediaRecorder = MediaRecorder()
            // 设置录制视频源为Camera(相机)
            mediaRecorder?.setVideoSource(MediaRecorder.VideoSource.CAMERA)
            // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            // 设置录制的视频编码h263 h264
            mediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
            mediaRecorder?.setVideoSize(176, 144)
            // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
            mediaRecorder?.setVideoFrameRate(20);
            mediaRecorder?.setPreviewDisplay(surfaceHolder?.surface)
            mediaRecorder?.setOutputFile("${FileTool.getFileCachePath()}/45646.3gp")

            mediaRecorder?.setOnInfoListener { mr, what, extra ->

            }


        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "请授权拍摄视频",
                    RC_CAMERA_PERM,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

    }

    private fun record() {
        recordVideoTv.visibility = View.GONE
        surfaceView.visibility = View.VISIBLE
        try {// 准备录制
            mediaRecorder?.prepare()
            // 开始录制
            mediaRecorder?.start()
        } catch (e: Exception) {
            NLog.i("...")
        }
    }

    private fun hasCameraPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                    .setTitle("授权相机设置")
                    .setRationale("去设置相机")
                    .build()
                    .show()
        }
        this.finish()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onRationaleDenied(requestCode: Int) {
        this.finish()
    }

    override fun onRationaleAccepted(requestCode: Int) {
    }

    override fun onResume() {
        super.onResume()
//        startPreview()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
//        startPreview()
        surfaceHolder = holder
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {

    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        surfaceHolder = holder
    }

    //启动预览
    private fun startPreview() {
        //保证只有一个Camera对象
        if (mCamera != null) {
            Log.i("zwy", "startPreview will return")
            return
        }

        mCamera = Camera.open(CAMERA_ID)

        val parameters = mCamera?.getParameters()
        val size = getBestPreviewSize(1080, 1920, parameters!!)
        if (size != null) {
            parameters.setPreviewSize(size!!.width, size!!.height)
        }

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)
        parameters.setPreviewFrameRate(20)

        //设置相机预览方向
        mCamera?.setDisplayOrientation(90)

        mCamera?.setParameters(parameters)

        try {
            mCamera?.setPreviewDisplay(surfaceHolder)
            //          mCamera.setPreviewCallback(mPreviewCallback);
        } catch (e: Exception) {
            Log.d("zwy", e.message)
        }

        mCamera?.startPreview()
    }

    private fun getBestPreviewSize(width: Int, height: Int, parameters: Camera.Parameters): Camera.Size? {
        var result: Camera.Size? = null

        for (size in parameters.supportedPreviewSizes) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size
                } else {
                    val resultArea = result.width * result.height
                    val newArea = size.width * size.height

                    if (newArea > resultArea) {
                        result = size
                    }
                }
            }
        }

        return result
    }
}
