package com.nze.nzexchange.controller.my.authentication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.widget.takephoto.app.TakePhoto
import com.nze.nzeframework.widget.takephoto.app.TakePhotoImpl
import com.nze.nzeframework.widget.takephoto.model.InvokeParam
import com.nze.nzeframework.widget.takephoto.model.TContextWrap
import com.nze.nzeframework.widget.takephoto.model.TResult
import com.nze.nzeframework.widget.takephoto.permission.InvokeListener
import com.nze.nzeframework.widget.takephoto.permission.PermissionManager
import com.nze.nzeframework.widget.takephoto.permission.TakePhotoInvocationHandler
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.RealAuthenticationBean
import com.nze.nzexchange.bean.RealNameAuthenticationBean
import com.nze.nzexchange.bean.UploadBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.CommonListPopup
import com.nze.nzexchange.tools.FileTool
import com.nze.nzexchange.tools.TakePhotoTool
import com.nze.nzexchange.widget.CommonButton
import kotlinx.android.synthetic.main.activity_real_name_authentication.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class RealNameAuthenticationActivity : NBaseActivity(), View.OnClickListener, TakePhoto.TakeResultListener, InvokeListener {

    val frontTv: TextView by lazy { tv_front_arna }
    val frontIv: ImageView by lazy { iv_front_arna }
    val backTv: TextView by lazy { tv_back_arna }
    val backIv: ImageView by lazy { iv_back_arna }
    val holdTv: TextView by lazy { tv_hold_arna }
    val holdIv: ImageView by lazy { iv_hold_arna }

    val submitBtn: CommonButton by lazy { btn_submit_arna }

    private val itemLimit: MutableList<String> by lazy {
        mutableListOf<String>().apply {
            add("拍照")
            add("从相册选择")
        }
    }
    val selectPhonePopup: CommonListPopup by lazy {
        CommonListPopup(this).apply {
            addAllItem(itemLimit)
            setOnItemClick { position, item ->
                val file: File = File(FileTool.getImageCachePath(), FileTool.getTempName("jpg"))
                val imageUri: Uri = Uri.fromFile(file)
                if (position == 0) {
                    takePhoto.onPickFromCaptureWithCrop(imageUri, TakePhotoTool.getCropOptions())
                } else {
                    takePhoto.onPickFromGalleryWithCrop(imageUri, TakePhotoTool.getCropOptions())
                }
            }
        }
    }
    val takePhoto: TakePhoto by lazy {
        (TakePhotoInvocationHandler.of(this).bind(TakePhotoImpl(this, this)) as TakePhoto).apply {
            TakePhotoTool.configCompress(this)
            TakePhotoTool.commonSet(this)
        }
    }
    lateinit var invokeParam: InvokeParam
    val TYPE_FRONT = 0
    val TYPE_BACK = 1
    val TYPE_HOLD = 2
    var currentType = TYPE_FRONT

    lateinit var body: MultipartBody.Part
    lateinit var requestFile: RequestBody
    var userBean = UserBean.loadFromApp()
    var busId: String? = null
        get() {
            return "${System.currentTimeMillis()}"
        }
    var frontUrl: String? = null
    var backUrl: String? = null
    var holdUrl: String? = null


    override fun getRootView(): Int = R.layout.activity_real_name_authentication

    override fun initView() {
        frontTv.setOnClickListener(this)
        backTv.setOnClickListener(this)
        holdTv.setOnClickListener(this)
        submitBtn.setOnCommonClick(this)
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

    override fun getContainerTargetView(): View? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_front_arna -> {
                currentType = TYPE_FRONT
                selectPhonePopup.showPopupWindow()
            }
            R.id.tv_back_arna -> {
                currentType = TYPE_BACK
                selectPhonePopup.showPopupWindow()
            }
            R.id.tv_hold_arna -> {
                currentType = TYPE_HOLD
                selectPhonePopup.showPopupWindow()
            }
            R.id.btn_submit_arna -> {
                if (frontUrl.isNullOrEmpty()) {
                    showToast("请上传身份证正面照")
                    return
                }
                if (backUrl.isNullOrEmpty()) {
                    showToast("请上传身份证反面照")
                    return
                }
                if (holdUrl.isNullOrEmpty()) {
                    showToast("请上传手持身份证照")
                    return
                }
                val fileUrl = "${frontUrl};${backUrl};${holdUrl}"
                RealAuthenticationBean.saveOneEntity(userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey, fileUrl, null)
                        .compose(netTfWithDialog())
                        .subscribe({
                            if (it.success) {
                                finish()
                            }
                        }, onError)
            }
        }
    }

    override fun takeSuccess(result: TResult?) {
        val imageFile = File(if (result?.image?.compressPath != null) result.image?.compressPath else result?.image?.originalPath)
        uploadImage(imageFile)
        when (currentType) {
            TYPE_FRONT -> {
                frontTv.visibility = View.GONE
                frontIv.visibility = View.VISIBLE
                frontIv.setImageURI(Uri.fromFile(imageFile))
            }
            TYPE_BACK -> {
                backTv.visibility = View.GONE
                backIv.visibility = View.VISIBLE
                backIv.setImageURI(Uri.fromFile(imageFile))
            }
            TYPE_HOLD -> {
                holdTv.visibility = View.GONE
                holdIv.visibility = View.VISIBLE
                holdIv.setImageURI(Uri.fromFile(imageFile))
            }
        }
    }

    override fun takeFail(result: TResult?, msg: String?) {
    }

    override fun takeCancel() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        takePhoto.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        takePhoto.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        takePhoto.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this)
    }

    override fun invoke(invokeParam: InvokeParam?): PermissionManager.TPermissionType {
        val type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam!!.getMethod())
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam
        }
        return type
    }

    fun uploadImage(imageFile: File) {
        requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageFile)
        var fileName = "${System.currentTimeMillis()}.jpg"
        body = MultipartBody.Part.createFormData("file", fileName, requestFile)
        userBean?.run {
            UploadBean.uploadFileNet(tokenReqVo.tokenUserId, tokenReqVo.tokenUserKey, tokenReqVo.tokenSystreeId, "membMereally", busId!!, true, body)
                    .compose(netTfWithDialog())
                    .subscribe({
                        if (it.success) {
                            val httpUrl = it.result.fileds[0].httpUrl
                            when (currentType) {
                                TYPE_FRONT -> {
                                    frontUrl = httpUrl
                                }
                                TYPE_BACK -> {
                                    backUrl = httpUrl
                                }
                                TYPE_HOLD -> {
                                    holdUrl = httpUrl
                                }
                            }
                        }
                    }, {
                        when (currentType) {
                            TYPE_FRONT -> {
                                frontTv.visibility = View.VISIBLE
                                frontIv.visibility = View.GONE
                            }
                            TYPE_BACK -> {
                                backTv.visibility = View.VISIBLE
                                backIv.visibility = View.GONE
                            }
                            TYPE_HOLD -> {
                                holdTv.visibility = View.VISIBLE
                                holdIv.visibility = View.GONE
                            }
                        }
                    })
        }
    }
}
