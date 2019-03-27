package com.nze.nzexchange.controller.market

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean2.ShenDubean
import com.nze.nzexchange.controller.base.NBaseAda
import kotlinx.android.synthetic.main.lv_buy_kline.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/1/24
 */
class KLineSellAdapter(mContext: Context) : NBaseAda<ShenDubean, KLineSellAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_sell_kline

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: ShenDubean, position: Int) {
        vh.amountTv.text = item.amount
        vh.priceTv.text = item.price
    }


    class ViewHolder(view: View) {
        val amountTv: TextView by lazy { view.tv_amount_lbk }
        val priceTv: TextView by lazy { view.tv_price_lbk }
    }
}