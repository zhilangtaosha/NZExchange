package com.nze.nzexchange.controller.my.authentication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import kotlinx.android.synthetic.main.activity_video_authentication.*
import java.io.File

class VideoAuthenticationActivity : NBaseActivity(), View.OnClickListener {

    private val recordVideoTv: TextView by lazy { tv_record_video_ava }
    private val videoIv: ImageView by lazy { iv_video_ava }
    private val videoLayout: RelativeLayout by lazy { layout_vedio_ava }
    private val tipTv: TextView by lazy { tv_tip_ava }
    val CODE_RECORD_REQUEST = 0
    var videoPath: String? = null

    override fun getRootView(): Int = R.layout.activity_video_authentication

    override fun initView() {

        videoIv.setOnClickListener(this)
        recordVideoTv.setOnClickListener(this)
        videoLayout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val intent = Intent(this@VideoAuthenticationActivity, VideoRecordActivity::class.java)
        intent.putExtra(IntentConstant.PARAM_VIDEO_PATH, videoPath)
        startActivityForResult(intent, CODE_RECORD_REQUEST)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            data?.extras?.let {
                recordVideoTv.visibility = View.GONE
                videoLayout.visibility = View.VISIBLE
                val imagePath = it.getString(IntentConstant.PARAM_IMAGE_PATH)
                videoPath = it.getString(IntentConstant.PARAM_VIDEO_PATH)
                videoIv.setImageURI(Uri.fromFile(File(imagePath)))
            }
        }
    }
}
