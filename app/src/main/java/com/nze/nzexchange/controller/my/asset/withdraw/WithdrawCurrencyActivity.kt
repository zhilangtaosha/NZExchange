package com.nze.nzexchange.controller.my.asset.withdraw

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.UserAssetBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.my.asset.SelectCurrencyActivity
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.http.NRetrofit
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
    val phoneEt: ClearableEditText by lazy { et_phone_acw }
    val verifyBtn: VerifyButton by lazy { btn_verify_acw }
    val serviceChargeTv: TextView by lazy { tv_service_charge_acw }
    val actualAmountTv: TextView by lazy { tv_actual_amount_acw }
    val withdrawBtn: CommonButton by lazy { btn_withdraw_acw }
    val attentionTv: TextView by lazy { tv_attention_acw }

    val REQUEST_CODE_QCODE = 1
    val REQUEST_CODE_CURRENCY = 2

    /**
     * 请求CAMERA权限码
     */
    private val REQUEST_CAMERA_PERM: Int = 101
    var userAssetBean: UserAssetBean? = null
    var userBean:UserBean?= UserBean.loadFromApp()

    override fun getRootView(): Int = R.layout.activity_coin_withdraw

    override fun initView() {
        intent?.let {
            userAssetBean = it.getParcelableExtra(IntentConstant.PARAM_ASSET)
        }
        topBar.setRightClick {
            skipActivity(WithdrawHistoryActivity::class.java)
        }
        selectCurrencyIv.setOnClickListener(this)
        qcodeIv.setOnClickListener(this)
        addressIv.setOnClickListener(this)
        allTv.setOnClickListener(this)
        verifyBtn.setOnClickListener(this)
        withdrawBtn.setOnClickListener(this)

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
                openScan()
            }
            R.id.iv_address_acw -> {
                skipActivity(SelectCurrencyAddressListActivity::class.java)
            }
            R.id.tv_all_acw -> {
            }
            R.id.btn_verify_acw -> {
                verifyBtn.startVerify()
            }
            R.id.btn_withdraw_acw -> {
                sendTransaction()
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
                        Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
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
            availableTv.text = it.available.toString()
        }

    }


    fun sendTransaction() {
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
        NRetrofit.instance
                .bibiService()
                .sendTransaction(userBean?.userId!!,userAssetBean?.currency!!,address,amount,"123456","")
                .compose(netTfWithDialog())
                .subscribe({
                    showToast(it.message)
                },onError)
    }
}
