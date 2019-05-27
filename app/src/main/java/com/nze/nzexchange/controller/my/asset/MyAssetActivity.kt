package com.nze.nzexchange.controller.my.asset

import android.net.Uri
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
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.UserAssetBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.AccountType
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.common.AuthorityDialog
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.widget.clearedit.ClearableEditText
import com.zhuang.zbannerlibrary.ZBanner
import com.zhuang.zbannerlibrary.ZBannerAdapter
import kotlinx.android.synthetic.main.activity_my_asset.*

/**
 * 我的资产
 */
class MyAssetActivity : NBaseActivity(), NBaseFragment.OnFragmentInteractionListener, AdapterView.OnItemClickListener {

    val zbanner: ZBanner by lazy { carousel_ama }
    val accoutTypeList: List<Int> = listOf<Int>(AssetBannerFragment.ACCOUT_TYPE_BIBI, AssetBannerFragment.ACCOUT_TYPE_OTC, AssetBannerFragment.ACCOUT_TYPE_BIBI, AssetBannerFragment.ACCOUT_TYPE_OTC)
    val bannerAdapter: AssetBannerAdapter by lazy { AssetBannerAdapter(supportFragmentManager, accoutTypeList) }

    val searchEt: ClearableEditText by lazy { et_search_ama }
    val showCb: CheckBox by lazy { cb_show_small_ama }
    val listView: ListView by lazy { lv_ama }
    val assetAdapter: MyAssetLvAdapter by lazy { MyAssetLvAdapter(this) }
    val userBean: UserBean? = UserBean.loadFromApp()
    var type = AccountType.BIBI
    val otcList: ArrayList<UserAssetBean> by lazy { ArrayList<UserAssetBean>() }
    val bibiList: ArrayList<UserAssetBean> by lazy { ArrayList<UserAssetBean>() }
    var isFirst = true

    val dialog = AuthorityDialog.getInstance(this)

    override fun getRootView(): Int = R.layout.activity_my_asset

    override fun initView() {
        zbanner.setAdapter(bannerAdapter)
        listView.adapter = assetAdapter
        listView.setOnItemClickListener(this)

        zbanner.setOnPageChangeLister {
            assetAdapter.clearGroup(true)
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
    }

    override fun onResume() {
        super.onResume()
        getOtcAsset()
        if (!isFirst) {
            getBibiAsset()
        } else {
            isFirst = false
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

    override fun getContainerTargetView(): View? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        CurrencyAssetDetailActivity.skip(this, type, assetAdapter.getItem(position)!!, otcList, bibiList)
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
        UserAssetBean.getUserAssets(userBean?.userId!!, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        otcList.clear()
                        otcList.addAll(it.result)
                        if (type == AccountType.OTC) {
                            assetAdapter.group = otcList
                        }
                    }else{
                        if (!dialog.isShow()&&it.isCauseNotEmpty()) {
                            AuthorityDialog.getInstance(this)
                                    .show("查询资产需要完成以下设置，请检查"
                                            , it.cause) {
                                        finish()
                                    }
                        }
                    }
                }, onError)
    }

    fun getBibiAsset() {
        UserAssetBean.assetInquiry(userBean?.userId!!, tokenUserId = userBean!!.tokenReqVo.tokenUserId, tokenUserKey = userBean!!.tokenReqVo.tokenUserKey)
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        bibiList.clear()
                        bibiList.addAll(it.result)
                        if (type == AccountType.BIBI)
                            assetAdapter.group = bibiList
                    }else{
                        if (!dialog.isShow()&&it.isCauseNotEmpty()) {
                            AuthorityDialog.getInstance(this)
                                    .show("查询资产需要完成以下设置，请检查"
                                            , it.cause) {
                                        finish()
                                    }
                        }
                    }
                }, onError)
    }
}
