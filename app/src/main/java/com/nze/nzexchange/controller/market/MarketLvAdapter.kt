package com.nze.nzexchange.controller.market

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.OtcBean
import com.nze.nzexchange.bean.TransactionPairBean
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.extend.formatForCurrency
import kotlinx.android.synthetic.main.lv_market.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/11/20
 */
class MarketLvAdapter(mContext: Context) : NBaseAda<TransactionPairsBean, MarketLvAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_market

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: TransactionPairsBean, position: Int) {
        vh.transactionTv.text = item.currency
        vh.mainCurrencyTv.text = item.mainCurrency
        vh.exchangeTv.text = item.exchangeRate.formatForCurrency()
        if (item.gain > 0) {
            vh.changeTv.setBackgroundResource(R.drawable.shape_radius_up_bg)
            vh.changeTv.text = "+${item.gain}%"
        } else {
            vh.changeTv.setBackgroundResource(R.drawable.shape_radius_down_bg)
            vh.changeTv.text = "${item.gain}%"
        }
//        vh.total24Tv.text = item.total24.toString()
//        vh.costTv.text = item.cost.toString()

    }

    class ViewHolder(view: View) {
        val transactionTv: TextView = view.tv_transaction
        val mainCurrencyTv: TextView = view.tv_main_currecy
        val exchangeTv: TextView = view.tv_exchange
        val total24Tv: TextView = view.tv_total24
        val costTv: TextView = view.tv_cost
        val changeTv: TextView = view.tv_change
    }
}