package com.nze.nzexchange.controller.market

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean2.ShenDubean
import com.nze.nzexchange.controller.base.NBaseAda
import kotlinx.android.synthetic.main.lv_new_deal_kline.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/2/20
 */
class KLineNewDealAdapter(mContext: Context) : NBaseAda<ShenDubean, KLineNewDealAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_new_deal_kline

    override fun createViewHold(convertView: View): ViewHolder=ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: ShenDubean, position: Int) {
    }

    class ViewHolder(val view: View) {
        val timeTv: TextView by lazy { view.tv_time_lndk }
        val priceTv: TextView by lazy { view.tv_price_lndk }
        val amountTv: TextView by lazy { view.tv_amount_lndk }
    }
}