package com.nze.nzexchange.controller.my.asset

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.BibiAssetBean
import com.nze.nzexchange.bean.UserAssetBean
import com.nze.nzexchange.bean.UserAssetBean.Companion.assetInquiry
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.AccountType
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.widget.clearedit.ClearableEditText
import com.zhuang.zbannerlibrary.ZBanner
import com.zhuang.zbannerlibrary.ZBannerAdapter
import kotlinx.android.synthetic.main.activity_my_asset.*

/**
 * 我的资产
 */
class MyAssetActivity : NBaseActivity(), NBaseFragment.OnFragmentInteractionListener, AdapterView.OnItemClickListener {

    val zbanner: ZBanner by lazy { carousel_ama }
    val accoutTypeList: List<Int> = listOf<Int>(AssetBannerFragment.ACCOUT_TYPE_BIBI, AssetBannerFragment.ACCOUT_TYPE_LEGAL, AssetBannerFragment.ACCOUT_TYPE_OTC)
    val bannerAdapter: AssetBannerAdapter by lazy { AssetBannerAdapter(supportFragmentManager, accoutTypeList) }

    val searchEt: ClearableEditText by lazy { et_search_ama }
    val showCb: CheckBox by lazy { cb_show_small_ama }
    val listView: ListView by lazy { lv_ama }
    val assetAdapter: MyAssetLvAdapter by lazy { MyAssetLvAdapter(this) }
    val userBean: UserBean? = UserBean.loadFromApp()
    var type = AccountType.BIBI
    val otcList: ArrayList<UserAssetBean> by lazy { ArrayList<UserAssetBean>() }
    val bibiList: ArrayList<UserAssetBean> by lazy { ArrayList<UserAssetBean>() }


    override fun getRootView(): Int = R.layout.activity_my_asset

    override fun initView() {
        zbanner.setAdapter(bannerAdapter)
        listView.adapter = assetAdapter
        listView.setOnItemClickListener(this)

        zbanner.setOnPageChangeLister {
            when (it) {
                0 -> {
                    getBibiAsset()
                    type = AccountType.BIBI
                }
                1 -> {
                }
                2 -> {
                    getOtcAsset()
                    type = AccountType.OTC
                }
            }
        }
        getOtcAsset()
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

    override fun getContainerTargetView(): View? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        ConcreteCurrencyAssetActivity.skip(this, type, assetAdapter.getItem(position)!!,otcList,bibiList)
    }


    override fun onFragmentInteraction(uri: Uri) {
    }

    class AssetBannerAdapter(fm: FragmentManager?, var typeList: List<Int>) : ZBannerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return AssetBannerFragment.newInstance(typeList[position])
        }

        override fun getCount(): Int = typeList.size

    }


    fun getOtcAsset() {
        UserAssetBean.getUserAssets(userBean?.userId!!)
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        otcList.clear()
                        otcList.addAll(it.result)
                        assetAdapter.group = otcList
                    }
                }, onError)
    }

    fun getBibiAsset() {
        UserAssetBean.assetInquiry(userBean?.userId!!)
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        bibiList.clear()
                        bibiList.addAll(it.result)
                        assetAdapter.group = bibiList
                    }
                }, onError)
    }
}
