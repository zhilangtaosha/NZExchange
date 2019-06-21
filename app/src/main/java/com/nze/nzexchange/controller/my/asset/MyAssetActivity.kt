package com.nze.nzexchange.controller.my.asset

import android.net.Uri
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.ListView
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import com.jakewharton.rxbinding2.widget.RxTextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.RealNameAuthenticationBean
import com.nze.nzexchange.bean.UserAssetBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.AccountType
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.LegalConfig
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.common.AuthorityDialog
import com.nze.nzexchange.controller.common.presenter.CommonBibiP
import com.nze.nzexchange.controller.my.asset.legal.LegalAssetDetailActivity
import com.nze.nzexchange.controller.my.asset.presenter.AssetP
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.widget.LinearLayoutAsListView
import com.nze.nzexchange.widget.clearedit.ClearableEditText
import com.zhuang.zbannerlibrary.ZBanner
import com.zhuang.zbannerlibrary.ZBannerAdapter
import kotlinx.android.synthetic.main.activity_my_asset.*
import org.greenrobot.eventbus.EventBus
import java.util.Locale.filter

/**
 * 我的资产
 */
class MyAssetActivity : NBaseActivity(), NBaseFragment.OnFragmentInteractionListener, AdapterView.OnItemClickListener {
    val assetP by lazy { AssetP(this) }
    val zbanner: ZBanner by lazy { carousel_ama }
    val accoutTypeList: List<Int> = listOf<Int>(
            AssetBannerFragment.ACCOUT_TYPE_BIBI,
            AssetBannerFragment.ACCOUT_TYPE_OTC,
            AssetBannerFragment.ACCOUT_TYPE_BIBI,
            AssetBannerFragment.ACCOUT_TYPE_OTC
    )
    val fragmentList: List<AssetBannerFragment> = listOf(
            AssetBannerFragment.newInstance(AssetBannerFragment.ACCOUT_TYPE_BIBI),
            AssetBannerFragment.newInstance(AssetBannerFragment.ACCOUT_TYPE_OTC)
            , AssetBannerFragment.newInstance(AssetBannerFragment.ACCOUT_TYPE_BIBI),
            AssetBannerFragment.newInstance(AssetBannerFragment.ACCOUT_TYPE_OTC)
    )
    val bannerAdapter: AssetBannerAdapter by lazy { AssetBannerAdapter(supportFragmentManager, accoutTypeList) }

    val searchEt: ClearableEditText by lazy { et_search_ama }
    val showCb: CheckBox by lazy { cb_show_small_ama }
    val listView: LinearLayoutAsListView by lazy { lv_ama }

    val mHandler: Handler = Handler()
    var isRefresh = true
    val assetAdapter: MyAssetLvAdapter by lazy {
        MyAssetLvAdapter(this).apply {
            onAssetItemClick = { position, item ->
                if (item!!.currency != LegalConfig.NAME) {
                    CurrencyAssetDetailActivity.skip(this@MyAssetActivity, type, assetAdapter.getItem(position)!!, otcList, bibiList)
                } else {
                    LegalAssetDetailActivity.skip(this@MyAssetActivity, realNameAuthenticationBean)
                }
//                CurrencyAssetDetailActivity.skip(this@MyAssetActivity, type, assetAdapter.getItem(position)!!, otcList, bibiList)
            }
        }
    }
    val userBean: UserBean? = UserBean.loadFromApp()
    var type = AccountType.BIBI
    val otcList: ArrayList<UserAssetBean> by lazy { ArrayList<UserAssetBean>() }
    val bibiList: ArrayList<UserAssetBean> by lazy { ArrayList<UserAssetBean>() }
    var isFirst = true
    var bannerIndex = 0

    val dialog = AuthorityDialog.getInstance(this)
    var realNameAuthenticationBean: RealNameAuthenticationBean? = null

    override fun getRootView(): Int = R.layout.activity_my_asset

    override fun initView() {
        zbanner.setAdapter(bannerAdapter)
        listView.adapter = assetAdapter
//        listView.onItemClickListener = this
        zbanner.setOnPageChangeLister {
            assetAdapter.clearGroup(true)
            bannerIndex = it
            (bannerAdapter.getItem(it) as AssetBannerFragment).refresh()
            if (isRefresh) {
                when (it) {
                    0 -> {
                        getBibiAsset()
                        type = AccountType.BIBI
                    }
                    1 -> {
                        getOtcAsset()
                        type = AccountType.OTC
                    }
                    2 -> {
                        getBibiAsset()
                        type = AccountType.BIBI
                    }
                    3 -> {
                        getOtcAsset()
                        type = AccountType.OTC
                    }
                }
            } else {
                isRefresh = true
            }
            searchEt.setText("")
            showCb.isChecked = false
        }
        RxTextView.textChanges(searchEt)
                .subscribe {
                    filter()
                }
        RxCompoundButton.checkedChanges(showCb)
                .subscribe {
                    filter()
                }

        getOtcAsset()
        assetP.getReanNameAuthentication(userBean!!, {
            if (it.success)
                realNameAuthenticationBean = it.result
        }, onError)
    }

    fun filter() {
        val currency = searchEt.getContent()
        val checked = showCb.isChecked
        val list = ArrayList<UserAssetBean>()
        if (type == AccountType.BIBI) {
            val l = bibiList.filter {
                val b = if (checked) {
                    it.available > 0
                } else {
                    true
                }
                (it.currency.contains(currency.toLowerCase()) || it.currency.contains(currency.toUpperCase())) && b
            }
            list.addAll(l)
        } else {
            val l = otcList.filter {
                val b = if (checked) {
                    it.available > 0
                } else {
                    true
                }
                (it.currency.contains(currency.toLowerCase()) || it.currency.contains(currency.toUpperCase())) && b
            }
            list.addAll(l)
        }
        assetAdapter.group = list
        listView.adapter = assetAdapter
    }

    override fun onResume() {
        super.onResume()
        if (type == AccountType.BIBI) {
            getBibiAsset()
        } else {
            getOtcAsset()
        }
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
    }

    override fun getOverridePendingTransitionMode(): BaseActivity.TransitionMode = BaseActivity.TransitionMode.DEFAULT

    override fun isBindEventBusHere(): Boolean = false

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkDisConnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContainerTargetView(): View? = listView

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = assetAdapter.getItem(position)
        if (item!!.currency != LegalConfig.NAME) {
            CurrencyAssetDetailActivity.skip(this, type, assetAdapter.getItem(position)!!, otcList, bibiList)
        } else {
            LegalAssetDetailActivity.skip(this, realNameAuthenticationBean)
        }
    }


    override fun onFragmentInteraction(uri: Uri) {
    }

    inner class AssetBannerAdapter(fm: FragmentManager?, var typeList: List<Int>) : ZBannerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
//            val fragment = AssetBannerFragment.newInstance(typeList[position])
//            return fragment
            return fragmentList[position]
        }

        override fun getCount(): Int = typeList.size

    }


    fun getOtcAsset() {
        UserAssetBean.getUserAssets(userBean?.userId!!, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                .compose(netTfWithDialog())
                .subscribe({
                    stopAllView()
                    if (it.success) {
                        otcList.clear()
                        otcList.addAll(it.result)
                        if (type == AccountType.OTC) {
                            assetAdapter.group = otcList
                            listView.adapter = assetAdapter
                        }
                    } else {
                        if (!dialog.isShow() && it.isCauseNotEmpty()) {
                            AuthorityDialog.getInstance(this)
                                    .show("查询资产需要完成以下设置，请检查"
                                            , it.cause) {
                                        finish()
                                    }
                        }
                    }
                }, {
                    showNODataView("未获取到OTC资产数据")
                })
    }

    fun getBibiAsset() {
        UserAssetBean.assetInquiry(userBean?.userId!!, tokenUserId = userBean!!.tokenReqVo.tokenUserId, tokenUserKey = userBean!!.tokenReqVo.tokenUserKey)
                .compose(netTfWithDialog())
                .subscribe({
                    stopAllView()
                    if (it.success) {
                        bibiList.clear()
                        bibiList.addAll(it.result)
                        if (type == AccountType.BIBI) {
                            assetAdapter.group = bibiList
                            listView.adapter = assetAdapter
                        }
                    } else {
                        if (!dialog.isShow() && it.isCauseNotEmpty()) {
                            AuthorityDialog.getInstance(this)
                                    .show("查询资产需要完成以下设置，请检查"
                                            , it.cause) {
                                        finish()
                                    }
                        }
                    }
                }, {
                    showNODataView("未获取到币币资产数据")
                })
    }
}
