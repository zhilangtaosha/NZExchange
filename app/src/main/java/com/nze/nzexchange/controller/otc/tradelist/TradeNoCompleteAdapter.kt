package com.nze.nzexchange.controller.otc.tradelist

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.OtcOrder
import com.nze.nzexchange.controller.base.NBaseAda
import kotlinx.android.synthetic.main.lv_trade_common.view.*
import kotlinx.android.synthetic.main.lv_trade_no_complete.view.*

class TradeNoCompleteAdapter(mContext: Context) : NBaseAda<OtcOrder, TradeNoCompleteAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_trade_no_complete

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: OtcOrder) {
        vh.typeTv.text = item.type
        vh.timeTv.text = item.time
        vh.statusTv.text = item.status
        vh.priceTv.text = item.price
        vh.numTv.text = "数量: ${item.num}"
        vh.moneyTv.text = "总额: ${item.total}"
        vh.countdownTv.text = "${item.countDown / 60}分${item.countDown % 60}秒"
    }

    class ViewHolder(val view: View) {
        val typeTv: TextView = view.tv_type_ltnc
        val timeTv: TextView = view.tv_time_ltnc
        val statusTv: TextView = view.tv_status_ltnc
        val priceTv: TextView = view.tv_price_ltnc
        val numTv: TextView = view.tv_num_ltnc
        val moneyTv: TextView = view.tv_money_ltnc
        val countdownTv: TextView = view.tv_countdown_ltnc
    }
}