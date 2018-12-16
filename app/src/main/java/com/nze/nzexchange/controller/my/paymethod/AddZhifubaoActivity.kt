package com.nze.nzexchange.controller.my.paymethod

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.widget.takephoto.app.TakePhoto
import com.nze.nzeframework.widget.takephoto.app.TakePhotoImpl
import com.nze.nzeframework.widget.takephoto.model.InvokeParam
import com.nze.nzeframework.widget.takephoto.model.TContextWrap
import com.nze.nzeframework.widget.takephoto.model.TResult
import com.nze.nzeframework.widget.takephoto.permission.InvokeListener
import com.nze.nzeframework.widget.takephoto.permission.PermissionManager
import com.nze.nzeframework.widget.takephoto.permission.TakePhotoInvocationHandler
import com.nze.nzexchange.R
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.CommonListPopup
import com.nze.nzexchange.tools.FileTool
import com.nze.nzexchange.tools.TakePhotoTool
import kotlinx.android.synthetic.main.activity_add_zhifubao.*
import java.io.File

class AddZhifubaoActivity : NBaseActivity(), TakePhoto.TakeResultListener, InvokeListener, View.OnClickListener {


    var type: Int = IntentConstant.TYPE_ZHIFUBAO
    val cardNoValueEt:EditText by lazy { et_cardno_value_aaz }
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
                val file: File = File(FileTool.getImageCachePath(), FileTool.getTempImageName())
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

    override fun getRootView(): Int = R.layout.activity_add_zhifubao

    override fun initView() {
        intent?.let {
            type = it.getIntExtra(IntentConstant.PARAM_PAY, IntentConstant.TYPE_ZHIFUBAO)
        }

        if (type == IntentConstant.TYPE_ZHIFUBAO) {
            ctb_aaz.setTitle("添加支付宝")
            et_cardno_value_aaz.hint = "请输入支付宝账号"
        } else {
            ctb_aaz.setTitle("添加微信")
            et_cardno_value_aaz.hint = "请输入微信账号"
        }
        layout_add_aaz.setOnClickListener(this)

        iv_close_aaz.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.layout_add_aaz -> selectPhonePopup.showPopupWindow()
            R.id.iv_close_aaz -> {
                iv_add_aaz.setImageResource(R.mipmap.add_image)
                iv_close_aaz.visibility = View.GONE
                layout_add_aaz.isClickable = true
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

    override fun takeSuccess(result: TResult?) {
        NLog.i("takeSuccess:" + result?.image?.compressPath)
        iv_add_aaz.setImageURI(Uri.fromFile(File(if (result?.image?.compressPath != null) result.image?.compressPath else result?.image?.originalPath)))
        iv_close_aaz.visibility = View.VISIBLE
        layout_add_aaz.isClickable = false
    }

    override fun takeFail(result: TResult?, msg: String?) {
        NLog.i("takeFail:$msg")
    }

    override fun takeCancel() {
        NLog.i("")
    }

    override fun invoke(invokeParam: InvokeParam?): PermissionManager.TPermissionType {
        val type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam!!.getMethod())
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam
        }
        return type
    }


}


