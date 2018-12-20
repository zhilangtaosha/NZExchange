package com.nze.nzexchange.controller.my.asset.withdraw

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.widget.CommonButton
import com.nze.nzexchange.widget.clearedit.ClearableEditText
import com.uuzuche.lib_zxing.activity.CaptureActivity
import com.uuzuche.lib_zxing.activity.CodeUtils
import kotlinx.android.synthetic.main.activity_add_address.*
import pub.devrel.easypermissions.EasyPermissions


/**
 * 添加新的提币地址
 * 添加完成后在列表展示
 */
class AddAddressActivity : NBaseActivity(), View.OnClickListener, EasyPermissions.PermissionCallbacks {


    val addressEt: ClearableEditText by lazy { et_address_aaa }
    val scanIv: ImageView by lazy { iv_scan_aaa }
    val remarkEt: ClearableEditText by lazy { et_remark_aaa }
    val confirmBtn: CommonButton by lazy { btn_confirm_aaa }

    val REQUEST_CODE = 0x001

    override fun getRootView(): Int = R.layout.activity_add_address

    override fun initView() {

        scanIv.setOnClickListener(this)
        confirmBtn.setOnCommonClick(this)


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
            R.id.iv_scan_aaa -> {
                cameraTask()
            }
            R.id.btn_confirm_aaa -> {
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            if (null != data) {
                var bundle: Bundle? = data.getExtras() ?: return;
                if (bundle?.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    var result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                } else if (bundle?.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(this@AddAddressActivity, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * 请求CAMERA权限码
     */
    val REQUEST_CAMERA_PERM = 101

    fun openScan() {
        startActivityForResult(Intent(this@AddAddressActivity, CaptureActivity::class.java), REQUEST_CODE)
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
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
            showToast("拒绝权限，不再弹出询问框，请前往APP应用设置中打开此权限")
        }else{
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


}
