package com.nze.nzexchange.controller.my.authentication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.Camera
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.*
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NFileTool
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.R
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.tools.FileTool
import com.nze.nzexchange.tools.TimeTool
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_video_record.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class VideoRecordActivity : NBaseActivity(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks, SurfaceHolder.Callback, View.OnClickListener {


    val surfaceView: SurfaceView by lazy { sv_avr }
    val titleLeftTv: TextView by lazy { tv_left_title_avr }
    val timeTv: TextView by lazy { tv_time_avr }
    val tipLayout: LinearLayout by lazy { layout_tip_avr }
    val tipTv: TextView by lazy { tv_tip_avr }
    val recordBtn: Button by lazy { btn_record_avr }
    val playBtn: Button by lazy { btn_play_avr }
    val bottomLayout: RelativeLayout by lazy { layout_bottom_avr }
    val recordAgainBtn: Button by lazy { btn_record_again_avr }
    val nextBtn: Button by lazy { btn_next_avr }
    private var mSurfaceHolder: SurfaceHolder? = null
    private var mCamera: Camera? = null
    private var mRecorder: MediaRecorder? = null
    private val CAMERA_ID = 1
    private var mIsSufaceCreated = false
    private var mIsRecording = false//是否开始录制
    private var lastFileName: String? = null
    var mediaPlayer: MediaPlayer? = null

    companion object {
        const val RC_CAMERA_PERM = 110
    }

    override fun getRootView(): Int = R.layout.activity_video_record

    override fun initView() {

        intent?.let {
            lastFileName = it.getStringExtra(IntentConstant.PARAM_VIDEO_PATH)
        }

        titleLeftTv.setOnClickListener(this)
        recordBtn.setOnClickListener(this)
        playBtn.setOnClickListener(this)
        recordAgainBtn.setOnClickListener(this)
        nextBtn.setOnClickListener(this)

        mSurfaceHolder = surfaceView.getHolder()
//        mSurfaceHolder?.setKeepScreenOn(true)
        mSurfaceHolder?.addCallback(this)
        mSurfaceHolder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

        //计时
        mRunnable = Runnable {
            var currentTime = System.currentTimeMillis()
            timeTv.text = TimeTool.countTime(currentTime - startTime)
            NLog.i("${currentTime - startTime}")
            if (isCount)
                mHandler.postDelayed(mRunnable, 1000)
        }

    }

    override fun onResume() {
        super.onResume()
        initRecorder()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (mIsRecording)
            stopRecording()
        if (mediaPlayer != null)
            stopPlay()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_left_title_avr -> {
                onBackPressed()
            }
            R.id.btn_record_avr -> {
                if (!mIsRecording) {
                    initMediaRecorder()
                    startRecording()
                } else {
                    stopRecording()
                }
            }
            R.id.btn_play_avr -> {
                if (mediaPlayer != null) {
                    stopPlay()
                }
                play(0)
            }
            R.id.btn_record_again_avr -> {
                recordBtn.visibility = View.VISIBLE
                playBtn.visibility = View.GONE
                bottomLayout.visibility = View.GONE
                timeTv.visibility = View.VISIBLE
                stopPlay()
                startPreview()
            }
            R.id.btn_next_avr -> {
                Observable.create<String> {
                    it.onNext(screenshot(0))
                }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            val intent: Intent = Intent()
                            intent.putExtra(IntentConstant.PARAM_IMAGE_PATH, it)
                            intent.putExtra(IntentConstant.PARAM_VIDEO_PATH, lastFileName)
                            this.setResult(Activity.RESULT_OK, intent)
                            this.finish()
                        }
            }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_CAMERA_PERM)
    public fun initRecorder() {
        if (hasCameraPermission()) {
            if (lastFileName.isNullOrEmpty()) {
                startPreview()
            } else {
                recordBtn.visibility = View.GONE
                playBtn.visibility = View.VISIBLE
                bottomLayout.visibility = View.VISIBLE
                timeTv.visibility = View.GONE
                if (mediaPlayer != null) {
                    stopPlay()
                }
                play(0)
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

    //---------------计时---------------------------------
    private var startTime: Long = 0
    private var isCount: Boolean = false
    var mHandler: Handler = Handler()
    private var mRunnable: Runnable? = null

    //--------------视频截图----------------------------------------------
    val retriever: MediaMetadataRetriever by lazy { MediaMetadataRetriever() }
//    var screenFile: String? = null

    /**
     * 获取视频某一帧
     * @param timeMs 毫秒
     */
    fun screenshot(time: Long): String {
        retriever.setDataSource(lastFileName, HashMap<String, String>())
        val bitmap = retriever.getFrameAtTime(time * 1000, MediaMetadataRetriever.OPTION_CLOSEST)
        var screenFile = "${FileTool.getImageCachePath()}/${FileTool.getTempName("jpg")}"
        NFileTool.bitmapToFile(bitmap, screenFile!!)
        return screenFile
    }


    //-----------------前摄像头预览--------------------------------

    //启动预览
    private fun startPreview() {
        //保证只有一个Camera对象
        if (mCamera != null || !mIsSufaceCreated) {
            NLog.i("startPreview will return")
            return
        }

        mCamera = Camera.open(CAMERA_ID)

        val parameters = mCamera?.getParameters()
        val size = getBestPreviewSize(1080, 1920, parameters!!)
        if (size != null) {
            parameters.setPreviewSize(size!!.width, size!!.height)
        }

        parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
        parameters.previewFrameRate = 20

        //设置相机预览方向
        mCamera?.setDisplayOrientation(90)

        mCamera?.setParameters(parameters)

        try {
            mCamera?.setPreviewDisplay(mSurfaceHolder)
            //          mCamera.setPreviewCallback(mPreviewCallback);
        } catch (e: Exception) {
            NLog.i(e.message)
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

    //停止预览
    private fun stopPreview() {
        //释放Camera对象
        if (mCamera != null) {
            try {
                mCamera!!.setPreviewDisplay(null)
            } catch (e: Exception) {
            }

            mCamera!!.stopPreview()
            mCamera!!.release()
            mCamera = null
        }
    }

    //------------------录制视频------------------------------
    //录制初始化
    fun initMediaRecorder() {
        mRecorder = MediaRecorder()//实例化
        mCamera?.unlock()
        //给Recorder设置Camera对象，保证录像跟预览的方向保持一致
        mRecorder?.setCamera(mCamera)
        mRecorder?.setOrientationHint(90)  //改变保存后的视频文件播放时是否横屏(不加这句，视频文件播放的时候角度是反的)
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC) // 设置从麦克风采集声音
//        mRecorder?.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
        mRecorder?.setVideoSource(MediaRecorder.VideoSource.CAMERA)// 设置从摄像头采集图像
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)  // 设置视频的输出格式 为MP4
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT) // 设置音频的编码格式
        //mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264) // 设置视频的编码格式
        mRecorder?.setVideoEncodingBitRate(3 * 1024 * 1024)// 设置视频编码的比特率
        mRecorder?.setVideoSize(640, 480)  // 设置视频大小
        mRecorder?.setVideoFrameRate(20) // 设置帧率
        mRecorder?.setOrientationHint(270)// 设置选择角度，顺时针方向，默认是逆向90度。此处设置的是保存后的视频的角度
//        mRecorder.setMaxDuration(10000); //设置最大录像时间为10s
        mRecorder?.setPreviewDisplay(mSurfaceHolder?.getSurface())

        //设置视频存储路径
        lastFileName = "${FileTool.getMovieCachePath()}/${FileTool.getTempName("mp4")}"
        NLog.i(lastFileName)
        mRecorder?.setOutputFile(lastFileName)
    }

    //开始录制
    private fun startRecording() {
        if (mRecorder != null) {
            try {
                mRecorder?.prepare()
                mRecorder?.start()
            } catch (e: Exception) {
                mIsRecording = false
            }
        }
        isCount = true
        startTime = System.currentTimeMillis()
        mHandler.post(mRunnable)

        mIsRecording = true
    }

    //结束录制
    fun stopRecording() {
        if (mCamera != null) {
            mCamera?.lock()
        }
        if (mRecorder != null) {
            mRecorder?.stop()
            mRecorder?.release()
            mRecorder = null
        }
        isCount = false
        mIsRecording = false
        recordBtn.visibility = View.GONE
        playBtn.visibility = View.VISIBLE
        bottomLayout.visibility = View.VISIBLE
        timeTv.visibility = View.GONE
        stopPreview()

    }

    //---------------------播放视频--------------------------------------------
    /**
     * 开始播放
     */
    fun play(msec: Int) {
        // 获取视频文件地址
        val file = File(lastFileName)
        if (!file.exists()) {
            Toast.makeText(this, "视频文件路径错误", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()

                mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
                // 设置播放的视频源
                mediaPlayer?.setDataSource(lastFileName)
                // 设置显示视频的SurfaceHolder
                mediaPlayer?.setDisplay(mSurfaceHolder)
                mediaPlayer?.prepare();//同步装载
//                mediaPlayer?.prepareAsync()//异步装载
                mediaPlayer?.start()
            }

            // 按照初始位置播放
            mediaPlayer?.seekTo(msec)
            playBtn.visibility = View.GONE
            bottomLayout.visibility = View.GONE



            mediaPlayer?.setOnCompletionListener {
                // 在播放完毕被回调
                playBtn.visibility = View.VISIBLE
                bottomLayout.visibility = View.VISIBLE
            }

            mediaPlayer?.setOnErrorListener { mp, what, extra ->
                // 发生错误重新播放
                play(0)
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /*
     * 停止播放
     */
    protected fun stopPlay() {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    //-------------------------获取权限------------------------------------
    private fun hasCameraPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
    }

    override fun onRationaleAccepted(requestCode: Int) {
    }

    //---------------SurfaceHolder----------------------------------------
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        initRecorder()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mIsSufaceCreated = false
        mediaPlayer?.stop()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mIsSufaceCreated = true
    }
}
