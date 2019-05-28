package com.nze.nzexchange.controller.market

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean2.ShenDubean
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.extend.formatForCurrency
import com.nze.nzexchange.extend.formatForPrice
import com.nze.nzexchange.widget.depth.DepthDataBean
import kotlinx.android.synthetic.main.lv_buy_kline.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/1/24
 */
class KLineBuyAdapter(mContext: Context) : NBaseAda<DepthDataBean, KLineBuyAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_buy_kline

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: DepthDataBean, position: Int) {
        vh.amountTv.text = item.volume.formatForCurrency()
        vh.priceTv.text = item.price.formatForPrice()
    }


    class ViewHolder(view: View) {
        val amountTv: TextView by lazy { view.tv_amount_lbk }
        val priceTv: TextView by lazy { view.tv_price_lbk }
    }
}