package com.nze.nzexchange.controller.my.asset

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.view.View.Z
import android.widget.ImageView
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.widget.CommonTopBar
import com.uuzuche.lib_zxing.activity.CodeUtils
import kotlinx.android.synthetic.main.activity_recharge_coin.*
import org.w3c.dom.Text

class RechargeCurrencyActivity : NBaseActivity(), View.OnClickListener {

    val topBar: CommonTopBar by lazy {
        ctb_arc.apply {
            setTitleRightIcon(R.mipmap.open_icon)
            setTitleClick {
                startActivityForResult(
                        Intent(this@RechargeCurrencyActivity, SelectCurrencyActivity::class.java),
                        1
                )
            }
            setRightClick {
                //充值历史列表
            }
        }
    }
    val coinAddressIv: ImageView by lazy { iv_coin_address_arc }
    val coinAddressTv: TextView by lazy { tv_coin_address_arc }
    val copyAddressTv: TextView by lazy { tv_copy_address_arc }
    val tipTv: TextView by lazy { tv_tip_arc }
    var codeBitmap: Bitmap? = null

    override fun getRootView(): Int = R.layout.activity_recharge_coin

    override fun initView() {
        topBar.setTitle("USDT快速充值")
        codeBitmap = CodeUtils.createImage("4565s465df1s65df", 400, 400, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
        coinAddressIv.setImageBitmap(codeBitmap)
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

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            var currency = data?.extras?.getString(IntentConstant.PARAM_CURRENCY)
            topBar.setTitle("${currency}快速充值")
        }
    }
}
