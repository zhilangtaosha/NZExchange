package com.nze.nzexchange.controller.my.asset


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.config.IntentConstant.Companion.PARAM_CURRENCY
import com.nze.nzexchange.controller.base.NBaseFragment
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

    companion object {
        const val ACCOUT_TYPE_BIBI = 1//币币
        const val ACCOUT_TYPE_LEGAL = 2//法币
        const val ACCOUT_TYPE_OTC = 3//otc

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
            }
        }

    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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


}
