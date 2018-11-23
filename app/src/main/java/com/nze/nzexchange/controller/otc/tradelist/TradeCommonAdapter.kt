package com.nze.nzexchange.controller.otc.tradelist

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.OtcOrder
import com.nze.nzexchange.bean.SubOrderInfoBean
import com.nze.nzexchange.controller.base.NBaseAda
import kotlinx.android.synthetic.main.lv_trade_common.view.*

class TradeCommonAdapter(mContext: Context, var type: Int) : NBaseAda<SubOrderInfoBean, TradeCommonAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_trade_common

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: SubOrderInfoBean) {
//        vh.typeTv.text = item.type
//        vh.timeTv.text = item.time
//        vh.statusTv.text = item.status
//        vh.priceTv.text = item.price
//        vh.numTv.text = "数量: ${item.num}"
//        vh.moneyTv.text = "总额: ${item.total}"



    }

    class ViewHolder(view: View) {
        val typeTv: TextView = view.tv_type_ltc
        val timeTv: TextView = view.tv_time_ltc
        val statusTv: TextView = view.tv_status_ltc
        val priceTv: TextView = view.tv_price_ltc
        val numTv: TextView = view.tv_num_ltc
        val moneyTv: TextView = view.tv_money_ltc

    }
}