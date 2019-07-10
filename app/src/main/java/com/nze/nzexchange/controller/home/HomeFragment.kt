package com.nze.nzexchange.controller.home

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog

import com.nze.nzexchange.R
import com.nze.nzexchange.bean.*
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.KLineParam
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.bibi.SoketService
import com.nze.nzexchange.controller.common.webview.WebActivity
import com.nze.nzexchange.controller.home.carousel.CarouselAdapter
import com.nze.nzexchange.controller.home.carousel.SimpleBulletinAdapter
import com.nze.nzexchange.controller.my.asset.MyAssetActivity
import com.nze.nzexchange.controller.my.asset.legal.LegalRechargeActivity
import com.nze.nzexchange.extend.formatForCurrency
import com.nze.nzexchange.extend.formatForLegal
import com.nze.nzexchange.extend.formatForPrice
import com.nze.nzexchange.extend.mul
import com.nze.nzexchange.http.NRetrofit
import com.nze.nzexchange.widget.bulletin.BulletinView
import com.nze.nzexchange.widget.recyclerview.Divider
import com.zhuang.zbannerlibrary.ZBanner
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.util.*


class HomeFragment : NBaseFragment(), View.OnClickListener {

    lateinit var rootView: View
    lateinit var mCarousel: ZBanner
    lateinit var bulletinView: BulletinView
    lateinit var mHotRView: RecyclerView
    val myWallet by lazy { rootView.layout_my_wallet_home }
    val legalTransactionLayout by lazy { rootView.layout_legal_transaction_home }
    val helpCenterLayout by lazy { rootView.layout_help_center_home }

    lateinit var mHotAdapter: HotTransactionPairAdapter
    val imageUrls: MutableList<String> = mutableListOf()
    val bannerList: MutableList<IndexImgs> = mutableListOf()
    val noticeList: MutableList<IndexNotices> = mutableListOf()
    val tips: MutableList<String> = mutableListOf("习近平举行仪式欢迎古巴国务委员会主席兼部长会议主席访华并同其举行会谈国家主席习近平8日下午在人民大会堂北大厅举行仪式",
            " 习近平上海考察，强调的这些事极具深意  专题 ",
            " 农业新“格局”  国外媒体高度评价  会场艺术珍品  专题 ")
    val hotDatas = mutableListOf<TransactionPairBean>()
    val carouselAdapter: CarouselAdapter by lazy { CarouselAdapter(fragmentManager!!, imageUrls) }

    //涨幅榜
    val mRandAdapter by lazy { RankListAdapter(activity!!) }
    var userBean = UserBean.loadFromApp()
    val mPopularList: MutableList<TransactionPairsBean> by lazy { mutableListOf<TransactionPairsBean>() }
    val mRankList: MutableList<SoketRankBean> by lazy { mutableListOf<SoketRankBean>() }

    init {
//        imageUrls.add("http://bpic.588ku.com/back_pic/17/03/16/14391cb76638d75a22f35625dff40eee.jpg")
//        imageUrls.add("http://bpic.588ku.com/back_pic/04/83/26/3458d0f1ff16fa3.jpg")
//        imageUrls.add("http://bpic.588ku.com/element_origin_min_pic/16/12/06/165846749a5fbf0.jpg")

        hotDatas.add(TransactionPairBean("BTC/USDT", 0.16, 6362.57, 43733.86, 213321))
        hotDatas.add(TransactionPairBean("BTC/USDT", -0.16, 6362.57, 43733.86, 213321))
        hotDatas.add(TransactionPairBean("BTC/USDT", -0.16, 6362.57, 43733.86, 213321))
        hotDatas.add(TransactionPairBean("BTC/USDT", 0.16, 6362.57, 43733.86, 213321))
        hotDatas.add(TransactionPairBean("BTC/USDT", 0.16, 6362.57, 43733.86, 213321))
        hotDatas.add(TransactionPairBean("BTC/USDT", 0.16, 6362.57, 43733.86, 213321))
        hotDatas.add(TransactionPairBean("BTC/USDT", 0.16, 6362.57, 43733.86, 213321))
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override fun getRootView(): Int = R.layout.fragment_home

    override fun initView(rootView: View) {
        this.rootView = rootView
        mCarousel = rootView.carousel_home


        bulletinView = rootView.bulletin_view


        mHotRView = rootView.rview_home
        mHotRView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val divider = Divider(LinearLayoutManager.HORIZONTAL)
        divider.setWidth(8)

        mHotRView.addItemDecoration(divider)


        myWallet.setOnClickListener(this)
        legalTransactionLayout.setOnClickListener(this)
        helpCenterLayout.setOnClickListener(this)

        initTopData()
        marketPopular(userBean?.userId)
        getRisingList(userBean?.userId)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.layout_my_wallet_home -> {
                if (UserBean.isLogin(activity!!))
                    skipActivity(MyAssetActivity::class.java)
            }
            R.id.layout_legal_transaction_home -> {
                if (UserBean.isLogin(activity!!))
                    skipActivity(LegalRechargeActivity::class.java)
            }
            R.id.layout_help_center_home -> {
                skipActivity(HelpCenterActivity::class.java)
            }
        }
    }

    fun initTopData() {
        IndexTopBean.indexAllData(true, true, false)
                .compose(netTfWithDialog())
                .subscribe({
                    val topBean = it.result
                    bannerList.addAll(topBean.imgs)
                    noticeList.addAll(topBean.notices)
                    imageUrls.clear()
                    bannerList.forEach {
                        imageUrls.add(it.imgUrl)
                    }
                    mCarousel.setAdapter(carouselAdapter)
                    mCarousel.star()

                    val bulletinAdapter = SimpleBulletinAdapter(activity!!, noticeList)
                    bulletinView.setAdapter(bulletinAdapter)
                    bulletinView.setOnBulletinItemClickListener {
                        WebActivity.skip(activity, noticeList[it].noticeBashmtlUrl, "公告")
                    }

                }, onError)
    }

    /**
     * 获取热门交易对数据
     */
    fun marketPopular(userId: String?) {
        TransactionPairsBean.marketPopular(userId)
                .compose(netTf())
                .subscribe({
                    if (it.success) {
                        mPopularList.clear()
                        mPopularList.addAll(it.result)
                        mHotAdapter = HotTransactionPairAdapter(activity!!, mPopularList)
                        mHotRView.adapter = mHotAdapter
                        refreshRank()
                    }
                }, onError)
    }

    /**
     * 涨幅榜
     */
    fun getRisingList(userId: String? = null) {
        NRetrofit.instance
                .bibiService()
                .getRisingList(userId)
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
//                        mRandAdapter.group = it.result
//                        rootView.lav_rank_home.adapter = mRandAdapter
                    }
                }, onError)
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {

    }


    override fun isBindEventBusHere(): Boolean = false

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
    }

    override fun onNetworkDisConnected() {
    }

    override fun getContainerTargetView(): View? = null

    override fun onResume() {
        super.onResume()
        activity!!.bindService(Intent(activity, SoketService::class.java), connection, Context.BIND_AUTO_CREATE)
    }


    override fun onDestroy() {
        mCarousel.stop()
        activity!!.unbindService(connection)
        super.onDestroy()
    }

    var binder: SoketService.SoketBinder? = null
    var isBinder = false

    val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i("zwy", "home onServiceDisconnected")
            isBinder = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i("zwy", "home onServiceConnected")
            binder = service as SoketService.SoketBinder
            isBinder = true

            binder?.addRankCallBak("home") { rankList ->
                NLog.i("home>>>rank")
                mRankList.clear()
                mRankList.addAll(rankList)
                mRandAdapter.group = mRankList.take(15).toMutableList()
                rootView.lav_rank_home.adapter = mRandAdapter
                refreshRank()
            }
            binder?.initSocket("home", KLineParam.getMarketMyself(), {
                NLog.i("home open")
                binder?.queryRank()
            }, {})
        }
    }

    fun refreshRank() {
        Observable.create<Int> {
            if (mPopularList.size > 0 && mRankList.size > 0) {
                mPopularList.removeAt(0)
                mPopularList.forEach { item ->
                    mRankList.forEach {
                        if (it.market == item.transactionPair) {
                            item.gain = it.change
                            item.cny = it.cny
                        }
                    }
                }
                it.onNext(1)
            }
            it.onComplete()
        }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mHotAdapter = HotTransactionPairAdapter(activity!!, mPopularList)
                    mHotRView.adapter = mHotAdapter
                }
    }


    override fun onVisibleRequest() {
        super.onVisibleRequest()
        NLog.i("HomeFragment....")
        binder?.queryRank()
    }
}
