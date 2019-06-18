package com.nze.nzexchange.controller.my.asset.withdraw

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.jakewharton.rxbinding2.widget.RxTextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.CurrencyWithdrawInfoBean
import com.nze.nzexchange.bean.UserAssetBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.bean.VerifyBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.FundPasswordPopup
import com.nze.nzexchange.controller.my.asset.SelectCurrencyActivity
import com.nze.nzexchange.extend.formatForCurrency
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.extend.mul
import com.nze.nzexchange.extend.sub
import com.nze.nzexchange.http.NRetrofit
import com.nze.nzexchange.tools.DoubleMath.Companion.sub
import com.nze.nzexchange.tools.editjudge.EditCurrencyWatcher
import com.nze.nzexchange.widget.CommonButton
import com.nze.nzexchange.widget.CommonTopBar
import com.nze.nzexchange.widget.VerifyButton
import com.nze.nzexchange.widget.clearedit.ClearableEditText
import com.uuzuche.lib_zxing.activity.CaptureActivity
import com.uuzuche.lib_zxing.activity.CodeUtils
import kotlinx.android.synthetic.main.activity_coin_withdraw.*
import pub.devrel.easypermissions.EasyPermissions

/**
 * 数字货币提现
 */
class WithdrawCurrencyActivity : NBaseActivity(), View.OnClickListener, EasyPermissions.PermissionCallbacks {


    val topBar: CommonTopBar by lazy { ctb_acw }
    val currencyTv: TextView by lazy { tv_currency_acw }
    val selectCurrencyIv: ImageView by lazy { iv_select_currency_acw }
    val addressEt: ClearableEditText by lazy { et_address_acw }
    val qcodeIv: ImageView by lazy { iv_qcode_acw }
    val addressIv: ImageView by lazy { iv_address_acw }
    val amountEt: ClearableEditText by lazy { et_amount_acw }
    val availableTv: TextView by lazy { tv_available_acw }
    val allTv: TextView by lazy { tv_all_acw }
    val verifyKeyTv: TextView by lazy { tv_verify_key_acw }
    val verifyValueEt: ClearableEditText by lazy { et_verify_value_acw }
    val verifyBtn: VerifyButton by lazy { btn_verify_acw }
    val serviceChargeTv: TextView by lazy { tv_service_charge_acw }
    val actualAmountTv: TextView by lazy { tv_actual_amount_acw }
    val withdrawBtn: CommonButton by lazy { btn_withdraw_acw }
    val labelLayout: RelativeLayout by lazy { layout_label_acw }
    val labelEt: ClearableEditText by lazy { et_label_acw }
    val tipTv: TextView by lazy { tv_tip_acw }

    val REQUEST_CODE_QCODE = 1
    val REQUEST_CODE_CURRENCY = 2

    /**
     * 请求CAMERA权限码
     */
    private val REQUEST_CAMERA_PERM: Int = 101
    var userAssetBean: UserAssetBean? = null
    var userBean: UserBean? = UserBean.loadFromApp()
    val VERIFY_PHONE = 0
    val VERIFY_EMAIL = 1
    var verifyType = VERIFY_PHONE
    var verifyAccount: String? = null//发送验证码的账号
    var checkcodeId: String? = null

    val fundPopup: FundPasswordPopup by lazy {
        FundPasswordPopup(this).apply {
            onPasswordClick = {
                sendTransaction(it)
            }
        }
    }
    var feeRate: Double = 0.0//提币手续费
    var feeLimitlowGet: Double = 0.0//最小提现数量

    override fun getRootView(): Int = R.layout.activity_coin_withdraw

    override fun initView() {
        intent?.let {
            userAssetBean = it.getParcelableExtra(IntentConstant.PARAM_ASSET)
        }
        topBar.setRightClick {
            startActivity(Intent(this@WithdrawCurrencyActivity, WithdrawHistoryActivity::class.java)
                    .putExtra(IntentConstant.PARAM_ASSET, userAssetBean))
        }

        userBean?.let {
            if (!it.userPhone.isNullOrEmpty()) {
                verifyKeyTv.text = "短信验证"
                verifyValueEt.hint = "请输入手机短信验证码"
                verifyAccount = it.userPhone
            } else {
                verifyKeyTv.text = "邮箱验证"
                verifyValueEt.hint = "请输入邮箱证码"
                verifyAccount = it.userEmail
            }
        }
        amountEt.addTextChangedListener(EditCurrencyWatcher(amountEt))
        selectCurrencyIv.setOnClickListener(this)
        qcodeIv.setOnClickListener(this)
        addressIv.setOnClickListener(this)
        allTv.setOnClickListener(this)
        verifyBtn.setOnClickListener(this)
        withdrawBtn.setOnClickListener(this)


        RxTextView.textChanges(amountEt)
                .subscribe {
                    if (!it.isNullOrEmpty()) {
                        val amount = it.toString().toDouble()
                        actualAmountTv.text = "${amount.sub(feeRate)} ${userAssetBean?.currency}"

                    } else {
                        actualAmountTv.text = "0 ${userAssetBean?.currency}"
                    }
                }

        refreshLayout()
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_select_currency_acw -> {
                startActivityForResult(
                        Intent(this, SelectCurrencyActivity::class.java),
                        REQUEST_CODE_CURRENCY
                )
            }
            R.id.iv_qcode_acw -> {
                cameraTask()
            }
            R.id.iv_address_acw -> {
                SelectCurrencyAddressListActivity.skip(this, userAssetBean!!.currency)
            }
            R.id.tv_all_acw -> {
                amountEt.setText(userAssetBean?.available?.formatForCurrency())
            }
            R.id.btn_verify_acw -> {
                VerifyBean.getVerifyCodeNet(verifyAccount!!, VerifyBean.TYPE_COMMON)
                        .compose(netTfWithDialog())
                        .subscribe({
                            if (it.success) {
                                verifyBtn.startVerify()
                                checkcodeId = it.result.checkcodeId
                                showToast("验证码已经发送到$verifyAccount")
                            }
                        }, onError)
            }
            R.id.btn_withdraw_acw -> {
                val amount = amountEt.getContent()
                if (amount.isNotEmpty() && amount.toDouble() > userAssetBean?.available!!) {
                    showToast("最多可提现${userAssetBean?.available!!}${userAssetBean?.currency}")
                    return
                }
                fundPopup.showPopupWindow()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var bundle: Bundle? = data?.getExtras() ?: return;
        when (requestCode) {
            REQUEST_CODE_QCODE -> {
                if (null != data) {
                    if (bundle?.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                        var result = bundle.getString(CodeUtils.RESULT_STRING);
                        addressEt.setText(result)
//                        Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                    } else if (bundle?.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                        Toast.makeText(this@WithdrawCurrencyActivity, "解析二维码失败", Toast.LENGTH_LONG).show();
                    }
                }
            }
            REQUEST_CODE_CURRENCY -> {
                userAssetBean = bundle?.getParcelable(IntentConstant.PARAM_ASSET)
                refreshLayout()
            }
        }
    }


    fun openScan() {
        startActivityForResult(Intent(this@WithdrawCurrencyActivity, CaptureActivity::class.java), REQUEST_CODE_QCODE)
    }


    fun cameraTask() {
        val perms = arrayOf<String>(Manifest.permission.CAMERA)
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            // Have permission, do the thing!
            NLog.i("cameraTask:this camera permission is granted")
            openScan()
        } else {
            // Ask for one permission
            NLog.i("cameraTask: this camera premission is denied , " + "ready to request this permission")
            EasyPermissions.requestPermissions(this, "需要请求camera权限",
                    REQUEST_CAMERA_PERM, Manifest.permission.CAMERA)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            showToast("拒绝权限，不再弹出询问框，请前往APP应用设置中打开此权限")
        } else {
            showToast("拒绝权限，等待下次询问哦")
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        openScan()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    fun refreshLayout() {
        userAssetBean?.let {
            topBar.setTitle("${it.currency}提现")
            currencyTv.text = it.currency
            availableTv.text = "可提数量：${it.available} ${it.currency}"
            amountEt.hint = "最小提现数量为200 ${it.currency}"
            serviceChargeTv.text = "--${it.currency}"
            actualAmountTv.text = "0 ${it.currency}"


            CurrencyWithdrawInfoBean.getCurrencyWithdrawInfo(it.currency)
                    .compose(netTfWithDialog())
                    .subscribe({ rs ->
                        if (rs.success) {
                            val withdrawInfoBean = rs.result
                            feeLimitlowGet = withdrawInfoBean.feeLimitlowGet
                            feeRate = withdrawInfoBean.feeRate
                            serviceChargeTv.text = "${feeRate.formatForCurrency()}${it.currency}"
                            tipTv.text = withdrawInfoBean.feeGetbiText
                        }
                    }, onError)

            switchLayout(it)
        }

    }

    fun switchLayout(bean: UserAssetBean) {
        if (bean.currency != "EOS") {
            labelLayout.visibility = View.GONE
        } else {
            labelLayout.visibility = View.VISIBLE
        }
    }


    fun sendTransaction(pwd: String) {
        val address = addressEt.getContent()
        val amount = amountEt.getContent()
        if (address.isNullOrEmpty()) {
            showToast("请输入提现地址")
            addressEt.requestFocus()
            return
        }
        if (amount.isNullOrEmpty()) {
            showToast("请输入提现数量")
            amountEt.requestFocus()
            return
        }
        if (amount.toDouble() < feeLimitlowGet) {
            showToast("提现最小数量为${feeLimitlowGet}")
            return
        }
        if (checkcodeId.isNullOrEmpty()) {
            showToast("请获取验证码")
            verifyValueEt.setText("")
            return
        }
        NRetrofit.instance
                .bibiService()
                .sendTransaction(userBean?.userId!!, userAssetBean?.currency!!, address, amount.toDouble().sub(feeRate), "123456", null, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey, pwd, checkcodeId!!, verifyValueEt.getContent())
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        showToast("提币成功")
                        finish()
                    } else {
                        showToast(it.message)
                    }
                }, onError)
    }
}
