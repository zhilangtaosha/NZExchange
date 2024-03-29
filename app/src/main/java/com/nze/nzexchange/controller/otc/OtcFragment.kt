package com.nze.nzexchange.controller.otc


import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.extend.setTextFromHtml

import com.nze.nzexchange.R
import com.nze.nzexchange.bean.MainCurrencyBean
import com.nze.nzexchange.bean.OtcAssetBean
import com.nze.nzexchange.bean.UserAssetBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.AccountType
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.common.AuthorityDialog
import com.nze.nzexchange.controller.login.LoginActivity
import com.nze.nzexchange.controller.my.asset.presenter.AssetP
import com.nze.nzexchange.controller.otc.main.*
import com.nze.nzexchange.controller.otc.tradelist.TradeListActivity
import com.nze.nzexchange.controller.transfer.CapitalTransferActivity
import com.nze.nzexchange.tools.dp2px
import com.nze.nzexchange.tools.getNColor
import com.nze.nzexchange.widget.indicator.indicator.IndicatorViewPager
import com.nze.nzexchange.widget.indicator.indicator.ScrollIndicatorView
import com.nze.nzexchange.widget.indicator.indicator.slidebar.ColorBar
import com.nze.nzexchange.widget.indicator.indicator.transition.OnTransitionTextListener
import kotlinx.android.synthetic.main.fragment_otc.view.*


class OtcFragment : NBaseFragment(), View.OnClickListener, AdapterView.OnItemClickListener {

    val mAssetP: AssetP by lazy { AssetP(mBaseActivity as NBaseActivity) }
    lateinit var drawerLayout: DrawerLayout
    lateinit var moreTv: TextView
    lateinit var leftLayout: LinearLayout
    lateinit var availableTv: TextView
    lateinit var frozenTv: TextView
    lateinit var availableLayout: RelativeLayout

    lateinit var indicatorViewPager: IndicatorViewPager
    lateinit var viewPager: ViewPager
    lateinit var scrollIndicatorView: ScrollIndicatorView
    private var otcAdFragment: OtcAdFragment = OtcAdFragment.newInstance()
    //    private val tabs = listOf<String>("${getString(R.string.otc_buy)}", "${getString(R.string.otc_sell)}", "${getString(R.string.otc_adver)}")
    private val tabs = mutableListOf<String>()
    private val pages = listOf<NBaseFragment>(OtcContentFragment.newInstance(OtcContentFragment.TYPE_BUY),
            OtcContentFragment.newInstance(OtcContentFragment.TYPE_SALE),
            otcAdFragment)

    var currentItem: Int = 0
    private var mCurrentAsset: OtcAssetBean? = null
    private var mMainCurrencyBean: MainCurrencyBean? = null
        set(value) {
            otcAdFragment.mMainCurrencyBean = value
            field = value
        }

    val sideAdapter: OtcSideAdapter by lazy {
        OtcSideAdapter(activity!!)
    }

    private val sideData = mutableListOf<MainCurrencyBean>()
    private var mCurrentTokenId: String? = null
    val otcList: ArrayList<UserAssetBean> by lazy { ArrayList<UserAssetBean>() }
    var userBean = UserBean.loadFromApp()
    var userAssetBean: UserAssetBean? = null

    companion object {
        @JvmStatic
        fun newInstance() = OtcFragment()
    }

    override fun getRootView(): Int = R.layout.fragment_otc

    override fun initView(rootView: View) {
        tabs.add(getString(R.string.otc_buy))
        tabs.add(getString(R.string.otc_sell))
        tabs.add(getString(R.string.otc_adver))
        //title
        moreTv = rootView.more_market
        moreTv.setOnClickListener(this)
        rootView.iv_trade_list_market.setOnClickListener(this)
        //侧边栏
        drawerLayout = rootView.layout_drawer_market
        leftLayout = rootView.layout_left_market
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        rootView.lv_side_otc.setOnItemClickListener(this)

        rootView.lv_side_otc.adapter = sideAdapter
        sideAdapter.group = sideData
        //主页
        // rootView.available_home.text = Html.fromHtml("可用<font color=\"#E05760\">0.1983</font>UCC")
        availableLayout = rootView.layout_available_otc
        availableTv = rootView.tv_available_otc
        frozenTv = rootView.tv_frozen_otc
        availableLayout.setOnClickListener(this)

        scrollIndicatorView = rootView.siv_otc
        viewPager = rootView.vp_otc
        scrollIndicatorView.onTransitionListener = OnTransitionTextListener().setColor(getNColor(R.color.color_FF6D87A8), getNColor(R.color.color_FFA3AAB9))

        val colorBar = ColorBar(activity!!, getNColor(R.color.color_FF6D87A8), 3)
        colorBar.setWidth(dp2px(41F))
        scrollIndicatorView.setScrollBar(colorBar)

        viewPager.offscreenPageLimit = 3
        indicatorViewPager = IndicatorViewPager(scrollIndicatorView, viewPager)
        val indicatorAdapter = OtcIndicatorAdapter(fragmentManager!!, activity!!, tabs, pages)
        indicatorViewPager.adapter = indicatorAdapter

        indicatorViewPager.setOnIndicatorPageChangeListener { preItem: Int, currentItem: Int ->
            if (currentItem == 2 && !UserBean.isLogin()) {
                skipActivity(LoginActivity::class.java)
                return@setOnIndicatorPageChangeListener
            }
            changeAva(currentItem)
        }

        getCurrencyNet()

    }


    fun getCurrencyNet() {
        MainCurrencyBean.getCurrency()
                .compose(netTfWithDialog())
                .subscribe({
                    val list = it.result
                    sideData.addAll(list)
                    sideAdapter.group = list
                    selectCurrency()
                }, onError)
    }

    fun getAssetFromNet() {
        OtcAssetBean.getAssetsNet(UserBean.loadFromApp()?.userId!!)
                .compose(netTf())
                .subscribe({
                    val list = it.result
//                    sideData.addAll(list)
//                    sideAdapter.group = list
//                    mCurrentAsset = list.get(0)
//                    moreTv.setText(mCurrentAsset!!.currency)
//                    changeAva(currentItem)
                }, onError)
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_NO_LOGIN) {
            indicatorViewPager.setCurrentItem(currentItem, false)
            userBean = UserBean.loadFromApp()
            getOtcAsset()
        }
        if (eventCenter.eventCode == EventCode.CODE_CHANGE_OTC_CURRENCY) {
            mCurrentTokenId = eventCenter.data as String
            sideData.forEach {
                if (mCurrentTokenId == it.tokenId) {
                    mMainCurrencyBean = it
                    return@forEach
                }
            }
            moreTv.setText(mMainCurrencyBean!!.tokenSymbol)
            changeAva(currentItem)
        }
        if (eventCenter.eventCode == EventCode.CODE_LOGOUT_SUCCESS) {
            indicatorViewPager.setCurrentItem(0, false)
        }
    }


    fun selectCurrency() {
        if (mCurrentTokenId != null) {//目前mCurrentTokenId获取有问题
            sideData.forEach {
                if (mCurrentTokenId == it.tokenId) {
                    mMainCurrencyBean = it
                    return@forEach
                }
            }
        } else {
            mMainCurrencyBean = sideData[0]
        }
        moreTv.setText(mMainCurrencyBean!!.tokenSymbol)
        changeAva(currentItem)
    }

    override fun isBindEventBusHere(): Boolean = true

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
    }

    override fun onNetworkDisConnected() {
    }

    override fun getContainerTargetView(): View? = null

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.more_market -> {
                if (drawerLayout.isDrawerOpen(leftLayout)) {
                    drawerLayout.openDrawer(leftLayout)
                } else {
                    drawerLayout.openDrawer(leftLayout)
                }
            }
            R.id.layout_available_otc -> {
            }
            R.id.iv_trade_list_market -> {
                if (UserBean.isLogin(activity!!))
                    skipActivity(TradeListActivity::class.java)
            }
            else -> {
            }
        }
    }

    fun changeAva(current: Int) {
        this.currentItem = current
        val fragment = pages.get(current)

        if (fragment is IOtcView)
            fragment.refresh(mMainCurrencyBean?.tokenId)


    }

    fun refreshLayout() {
        moreTv.text = mCurrentAsset?.currency
        availableTv.setTextFromHtml("可用<font color=\"#E05760\">${mCurrentAsset?.available}</font>${mCurrentAsset?.currency}")
        frozenTv.setTextFromHtml("<font color=\"#6D87A8\">冻结${mCurrentAsset?.freeze}${mCurrentAsset?.currency}</font>")
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        drawerLayout.closeDrawer(leftLayout)
        mMainCurrencyBean = sideData.get(position)
        moreTv.setText(mMainCurrencyBean!!.tokenSymbol)
        changeAva(currentItem)
    }

    fun getOtcAsset() {
        UserAssetBean.getUserAssets(userBean?.userId!!, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                .compose(netTfWithDialog())
                .subscribe({
                    stopAllView()
                    if (it.success) {
                        otcList.clear()
                        otcList.addAll(it.result)
                        otcList.forEach {
                            if (it.currency == mMainCurrencyBean?.tokenSymbol) {
                                userAssetBean = it
                                return@forEach
                            }
                        }
                        pages.forEach {
                            (it as IOtcView).setOtcAsset(userAssetBean!!)
                        }
                    }
                }, {
                    NLog.i("未获取到OTC资产数据")
                })
    }
}

