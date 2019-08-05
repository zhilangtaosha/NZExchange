package com.nze.nzexchange.controller.my.paymethod

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NGlide
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.widget.takephoto.app.TakePhoto
import com.nze.nzeframework.widget.takephoto.app.TakePhotoImpl
import com.nze.nzeframework.widget.takephoto.model.InvokeParam
import com.nze.nzeframework.widget.takephoto.model.TContextWrap
import com.nze.nzeframework.widget.takephoto.model.TResult
import com.nze.nzeframework.widget.takephoto.permission.InvokeListener
import com.nze.nzeframework.widget.takephoto.permission.PermissionManager
import com.nze.nzeframework.widget.takephoto.permission.TakePhotoInvocationHandler
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.RealNameAuthenticationBean
import com.nze.nzexchange.bean.SetPayMethodBean
import com.nze.nzexchange.bean.UploadBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.CommonListPopup
import com.nze.nzexchange.controller.common.FundPasswordPopup
import com.nze.nzexchange.controller.my.paymethod.presenter.PayMethodPresenter
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.extend.getValue
import com.nze.nzexchange.tools.FileTool
import com.nze.nzexchange.tools.TakePhotoTool
import com.nze.nzexchange.validation.EmptyValidation
import com.nze.nzexchange.widget.CommonButton
import kotlinx.android.synthetic.main.abc_activity_chooser_view.view.*
import kotlinx.android.synthetic.main.activity_add_zhifubao.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class AddZhifubaoActivity : NBaseActivity(), TakePhoto.TakeResultListener, InvokeListener, View.OnClickListener {

    val pmp by lazy { PayMethodPresenter(this) }
    var type: Int = IntentConstant.TYPE_ZHIFUBAO
    val cardNoValueEt: EditText by lazy { et_cardno_value_aaz }
    val saveBtn: CommonButton by lazy {
        btn_save_aaz.apply {
            initValidator()
                    .add(cardNoValueEt, EmptyValidation())
                    .executeValidator()
        }
    }
    val nameValueTv: TextView by lazy { tv_name_value_aaz }
    val addLayout: RelativeLayout by lazy { layout_add_aaz }
    val addIv: ImageView by lazy { iv_add_aaz }
    val closeIv: ImageView by lazy { iv_close_aaz }

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
//                    takePhoto.onPickFromCaptureWithCrop(imageUri, TakePhotoTool.getCropOptions())
                    takePhoto.onPickFromCapture(imageUri)
                } else {
//                    takePhoto.onPickFromGalleryWithCrop(imageUri, TakePhotoTool.getCropOptions())
                    takePhoto.onPickFromGallery()
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
    val userBean: UserBean? by lazy { NzeApp.instance.userBean }
    var path: String = "membAccmoney"
    var busId: String? = null
        get() {
            return System.currentTimeMillis().toString()
        }
    lateinit var body: MultipartBody.Part
    lateinit var requestFile: RequestBody

    var accmoneyWeixinurl: String? = null
    var accmoneyWeixinacc: String? = null
    var accmoneyZfburl: String? = null
    var accmoneyZfbacc: String? = null

    var imageUrl = ""
    var account = ""
    var realNameAuthenticationBean: RealNameAuthenticationBean? = null
    val fundPopup: FundPasswordPopup by lazy {
        FundPasswordPopup(this).apply {
            onPasswordClick = {
                setPayMethod(it)
            }
        }
    }


    override fun getRootView(): Int = R.layout.activity_add_zhifubao

    override fun initView() {
        intent?.let {
            type = it.getIntExtra(IntentConstant.PARAM_PAY, IntentConstant.TYPE_ZHIFUBAO)
            realNameAuthenticationBean = it.getParcelableExtra(IntentConstant.INTENT_REAL_NAME_BEAN)
        }
        realNameAuthenticationBean?.let {
            nameValueTv.text = it.membName
        }
        if (type == IntentConstant.TYPE_ZHIFUBAO) {
            ctb_aaz.setTitle("添加支付宝")
            et_cardno_value_aaz.hint = "请输入支付宝账号"
            imageUrl = userBean?.payMethod?.accmoneyZfburl?.getValue() ?: ""
            account = userBean?.payMethod?.accmoneyZfbacc?.getValue() ?: ""
            accmoneyZfbacc = account
            accmoneyZfburl = imageUrl
        } else {
            ctb_aaz.setTitle("添加微信")
            et_cardno_value_aaz.hint = "请输入微信账号"
            imageUrl = userBean?.payMethod?.accmoneyWeixinurl?.getValue() ?: ""
            account = userBean?.payMethod?.accmoneyWeixinacc?.getValue() ?: ""
            accmoneyWeixinacc = account
            accmoneyWeixinurl = imageUrl
        }

        addLayout.setOnClickListener(this)

        closeIv.setOnClickListener(this)
        saveBtn.setOnCommonClick(this)

        if (account.isNotEmpty()) {
            cardNoValueEt.setText(account)
            cardNoValueEt.setSelection(account.length)
        }
        if (imageUrl.isNotEmpty()) {
            NGlide.load(addIv, imageUrl)
            closeIv.visibility = View.VISIBLE
            addLayout.isClickable = false
        }

        pmp.getTransaction(userBean!!)
                .compose(netTfWithDialog())
                .subscribe({
                    if (!it.success) {
                        showToast(it.message)
                        saveBtn.visibility = View.GONE
                        cardNoValueEt.inputType = InputType.TYPE_NULL
                        addLayout.isClickable = false
                        closeIv.visibility = View.GONE
                    }
                }, onError)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.layout_add_aaz -> selectPhonePopup.showPopupWindow()
            R.id.iv_close_aaz -> {
                iv_add_aaz.setImageResource(R.mipmap.add_image)
                iv_close_aaz.visibility = View.GONE
                layout_add_aaz.isClickable = true
            }
            R.id.btn_save_aaz -> {

                if (iv_close_aaz.visibility == View.GONE) {
                    showToast("请上传付款码图片")
                    return
                }

                if (saveBtn.validate()) {
                    if (type == IntentConstant.TYPE_ZHIFUBAO) {
                        accmoneyZfbacc = cardNoValueEt.getContent()
                    } else {
                        accmoneyWeixinacc = cardNoValueEt.getContent()
                    }
                    fundPopup.showPopupWindow()
                }
            }
        }
    }


    fun setPayMethod(pwd: String) {
        userBean?.run {
            SetPayMethodBean.setPayMethodNet(tokenReqVo.tokenUserId,
                    tokenReqVo.tokenUserKey,
                    null,
                    null,
                    null,
                    accmoneyWeixinurl,
                    accmoneyWeixinacc,
                    accmoneyZfburl,
                    accmoneyZfbacc, pwd, null, null, null, null, null)
                    .compose(netTfWithDialog())
                    .subscribe({
                        if (it.success) {
                            val payMethodBean = it.result
                            if (type == IntentConstant.TYPE_ZHIFUBAO) {
                                userBean?.payMethod?.accmoneyZfbacc = payMethodBean.accmoneyZfbacc
                                userBean?.payMethod?.accmoneyZfburl = payMethodBean.accmoneyZfburl
                            } else {
                                userBean?.payMethod?.accmoneyWeixinacc = payMethodBean.accmoneyWeixinacc
                                userBean?.payMethod?.accmoneyWeixinurl = payMethodBean.accmoneyWeixinurl
                            }
                            NzeApp.instance.userBean = userBean
                            this@AddZhifubaoActivity.finish()
                        } else {
                            showToast(it.message)
                        }
                    }, onError)
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
        val imageFile = File(if (result?.image?.compressPath != null) result.image?.compressPath else result?.image?.originalPath)
        iv_add_aaz.setImageURI(Uri.fromFile(imageFile))
        iv_close_aaz.visibility = View.VISIBLE
        layout_add_aaz.isClickable = false
//        path = if (result?.image?.compressPath != null) result.image?.compressPath!! else result?.image?.originalPath!!
        requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageFile)
        var fileName = "${System.currentTimeMillis()}.jpg"
        body = MultipartBody.Part.createFormData("file", fileName, requestFile)
        userBean?.run {
            UploadBean.uploadFileNet(tokenReqVo.tokenUserId, tokenReqVo.tokenUserKey, tokenReqVo.tokenSystreeId, path, busId!!, true, body)
                    .compose(netTfWithDialog())
                    .subscribe({
                        if (it.success) {
                            val httpUrl = it.result.fileds[0].httpUrl
                            if (type == IntentConstant.TYPE_ZHIFUBAO) {
                                accmoneyZfburl = httpUrl
                            } else {
                                accmoneyWeixinurl = httpUrl
                            }
                        }
                    }, onError)
        }
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


