package com.nze.nzexchange.controller.home

import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog

import com.nze.nzexchange.R
import com.nze.nzexchange.bean.IndexImgs
import com.nze.nzexchange.bean.IndexNotices
import com.nze.nzexchange.bean.IndexTopBean
import com.nze.nzexchange.bean.TransactionPairBean
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.home.carousel.CarouselAdapter
import com.nze.nzexchange.controller.home.carousel.SimpleBulletinAdapter
import com.nze.nzexchange.widget.bulletin.BulletinView
import com.nze.nzexchange.widget.recyclerview.Divider
import com.zhuang.zbannerlibrary.ZBanner
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : NBaseFragment() {
    lateinit var mCarousel: ZBanner
    lateinit var bulletinView:BulletinView
    lateinit var mHotRView: RecyclerView
    lateinit var mHotAdapter: HotTransactionPairAdapter
    val imageUrls: MutableList<String> = mutableListOf()
    val bannerList: MutableList<IndexImgs> = mutableListOf()
    val noticeList: MutableList<IndexNotices> = mutableListOf()
    val tips: MutableList<String> = mutableListOf("习近平举行仪式欢迎古巴国务委员会主席兼部长会议主席访华并同其举行会谈国家主席习近平8日下午在人民大会堂北大厅举行仪式",
            " 习近平上海考察，强调的这些事极具深意  专题 ",
            " 农业新“格局”  国外媒体高度评价  会场艺术珍品  专题 ")
    val hotDatas = mutableListOf<TransactionPairBean>()
    val carouselAdapter:CarouselAdapter by lazy { CarouselAdapter(fragmentManager!!, imageUrls) }

    init {
        imageUrls.add("http://bpic.588ku.com/back_pic/17/03/16/14391cb76638d75a22f35625dff40eee.jpg")
        imageUrls.add("http://bpic.588ku.com/back_pic/04/83/26/3458d0f1ff16fa3.jpg")
        imageUrls.add("http://bpic.588ku.com/element_origin_min_pic/16/12/06/165846749a5fbf0.jpg")

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
        mCarousel = rootView.carousel_home
//        val carouselAdapter = CarouselAdapter(fragmentManager!!, imageUrls)


        bulletinView = rootView.bulletin_view


        mHotRView = rootView.rview_home
        mHotRView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val divider = Divider(LinearLayoutManager.HORIZONTAL)
        divider.setWidth(8)

        mHotRView.addItemDecoration(divider)
        mHotAdapter = HotTransactionPairAdapter(activity!!, hotDatas)
        mHotRView.adapter = mHotAdapter

        val mRandAdapter = RankListAdapter(activity!!)


        Handler().postDelayed({
            mRandAdapter.group = hotDatas
            rootView.lav_rank_home.adapter = mRandAdapter
        }, 3000)

        initTopData()
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

                    val bulletinAdapter = SimpleBulletinAdapter(activity!!, noticeList)
                    bulletinView.setAdapter(bulletinAdapter)
                    
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
        mCarousel.star()
    }

    override fun onDestroy() {
        mCarousel.stop()
        super.onDestroy()
    }
}
