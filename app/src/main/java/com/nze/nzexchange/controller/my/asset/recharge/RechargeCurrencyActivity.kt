package com.nze.nzexchange.controller.my.asset.recharge

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.UserAssetBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.my.asset.SelectCurrencyActivity
import com.nze.nzexchange.widget.CommonTopBar
import com.uuzuche.lib_zxing.activity.CodeUtils
import kotlinx.android.synthetic.main.activity_recharge_coin.*

/**
 * 充值页面
 */
class RechargeCurrencyActivity : NBaseActivity(), View.OnClickListener {

    val topBar: CommonTopBar by lazy {
        ctb_arc.apply {

            setTitleClick {
                startActivityForResult(
                        Intent(this@RechargeCurrencyActivity, SelectCurrencyActivity::class.java),
                        1
                )
            }
            setRightClick {
                //充值历史列表
                skipActivity(RechargeHistoryActivity::class.java)
            }
        }
    }
    val coinTitleTv: TextView by lazy { tv_title_address_arc }
    val coinAddressIv: ImageView by lazy { iv_coin_address_arc }
    val coinAddressTv: TextView by lazy { tv_coin_address_arc }
    val copyAddressTv: TextView by lazy { tv_copy_address_arc }
    val tipTv: TextView by lazy { tv_tip_arc }
    var codeBitmap: Bitmap? = null
    var userAssetBean: UserAssetBean? = null

    override fun getRootView(): Int = R.layout.activity_recharge_coin

    override fun initView() {
        topBar.setTitleRightIcon(R.mipmap.open_icon)
        intent?.let {
            userAssetBean = it.getParcelableExtra(IntentConstant.PARAM_ASSET)
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

    override fun getContainerTargetView(): View? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClick(v: View?) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            userAssetBean = data?.extras?.getParcelable<UserAssetBean>(IntentConstant.PARAM_ASSET)
            refreshLayout()
        }
    }

    fun refreshLayout() {
        userAssetBean?.let {
            topBar.setTitle("${it.currency}充值")
            coinTitleTv.text = "${it.currency}快速充值"
            codeBitmap = CodeUtils.createImage(it.address, 400, 400, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
            coinAddressIv.setImageBitmap(codeBitmap)
            coinAddressTv.text = it.address
        }
    }

}
