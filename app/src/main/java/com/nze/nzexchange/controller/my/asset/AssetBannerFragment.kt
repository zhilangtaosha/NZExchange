package com.nze.nzexchange.controller.my.asset


import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.UserAssetBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.AccountType
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.IntentConstant.Companion.PARAM_CURRENCY
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.common.AuthorityDialog
import com.nze.nzexchange.controller.common.presenter.CommonBibiP
import com.nze.nzexchange.extend.add
import com.nze.nzexchange.extend.formatForCurrency
import com.nze.nzexchange.extend.formatForLegal
import kotlinx.android.synthetic.main.fragment_asset_banner.view.*


/**
 * A simple [Fragment] subclass.
 *
 */
class AssetBannerFragment : NBaseFragment() {
    var accoutType: Int = 0
    val PARAM_accout_type = "accout"

    lateinit var rootView: View
    val rootLayout: LinearLayout by lazy { rootView.rootLayout_fab }
    val accoutTypeTv: TextView by lazy { rootView.tv_accout_type_fab }
    val assetKeyTv: TextView by lazy { rootView.tv_asset_key_fab }
    val assetValueTv: TextView by lazy { rootView.tv_asset_value_fab }
    val moneyTv: TextView by lazy { rootView.tv_money_fab }
    var userBean = UserBean.loadFromApp()

    companion object {
        const val ACCOUT_TYPE_BIBI = 0//币币
        const val ACCOUT_TYPE_LEGAL = 1//法币
        const val ACCOUT_TYPE_OTC = 2//otc

        @JvmStatic
        fun newInstance(accoutType: Int) =
                AssetBannerFragment().apply {
                    arguments = Bundle().apply {
                        putInt(PARAM_CURRENCY, accoutType)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            accoutType = it.getInt(PARAM_CURRENCY)
        }
    }

    override fun getRootView(): Int = R.layout.fragment_asset_banner

    override fun initView(rootView: View) {
        this.rootView = rootView

        when (accoutType) {
            ACCOUT_TYPE_BIBI -> {
                rootLayout.setBackgroundResource(R.mipmap.asset_bibi_bg)
                accoutTypeTv.text = "币币账户"
            }
            ACCOUT_TYPE_LEGAL -> {
                rootLayout.setBackgroundResource(R.mipmap.asset_legal_bg)
                accoutTypeTv.text = "法币账户"
            }
            ACCOUT_TYPE_OTC -> {
                rootLayout.setBackgroundResource(R.mipmap.asset_otc_bg)
                accoutTypeTv.text = "OTC账户"
                getOtcAsset()
            }
        }

    }

    fun refresh() {
        if (accoutType == ACCOUT_TYPE_OTC) {
            getOtcAsset()
        } else {
            getBibiAsset()
        }
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {

    }

    override fun isBindEventBusHere(): Boolean = false

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkDisConnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContainerTargetView(): View? = null

    fun getOtcAsset() {
        UserAssetBean.getUserAssets(userBean?.userId!!, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        val list = it.result
                        val len = list.size
                        var i = 0
                        var t = 0.0
                        list.forEach { asset ->
                            CommonBibiP.getInstance(activity as NBaseActivity)
                                    .currencyToLegal(asset.currency, asset.available + asset.freeze, {
                                        i++
                                        if (it.success) {
                                            t = t.add(it.result.formatForLegal().toDouble())
                                        }
                                        if (i == len) {
                                            assetValueTv.text = t.formatForLegal()
                                        }
                                    }, {
                                        i++
                                        if (i == len) {
                                            assetValueTv.text = t.formatForLegal()
                                        }
                                    })
                        }

                    }
                }, onError)
    }

    fun getBibiAsset() {
        UserAssetBean.assetInquiry(userBean?.userId!!, tokenUserId = userBean!!.tokenReqVo.tokenUserId, tokenUserKey = userBean!!.tokenReqVo.tokenUserKey)
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        val list = it.result
                        val len = list.size
                        var i = 0
                        var t = 0.0
                        list.forEach { asset ->
                            CommonBibiP.getInstance(activity as NBaseActivity)
                                    .currencyToLegal(asset.currency, asset.available + asset.freeze, {
                                        i++
                                        if (it.success) {
                                            t = t.add(it.result.formatForLegal().toDouble())
                                        }
                                        if (i == len) {
                                            assetValueTv.text = t.formatForLegal()
                                        }
                                    }, {
                                        i++
                                        if (i == len) {
                                            assetValueTv.text = t.formatForLegal()
                                        }
                                    })
                        }
                    }
                }, onError)
    }
}
