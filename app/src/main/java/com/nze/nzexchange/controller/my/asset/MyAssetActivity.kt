package com.nze.nzexchange.controller.my.asset

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
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.BibiAssetBean
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

    override fun getRootView(): Int = R.layout.activity_my_asset

    override fun initView() {
        zbanner.setAdapter(bannerAdapter)
        listView.adapter = assetAdapter
        listView.setOnItemClickListener(this)

        assetAdapter.group = BibiAssetBean.getList()
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
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

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        skipActivity(ConcreteCurrencyAssetActivity::class.java)
    }


    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class AssetBannerAdapter(fm: FragmentManager?, var typeList: List<Int>) : ZBannerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return AssetBannerFragment.newInstance(typeList[position])
        }

        override fun getCount(): Int = typeList.size

    }

}
