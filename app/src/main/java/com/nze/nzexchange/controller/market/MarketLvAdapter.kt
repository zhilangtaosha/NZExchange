package com.nze.nzexchange.controller.market

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.OtcBean
import com.nze.nzexchange.bean.TransactionPairBean
import com.nze.nzexchange.controller.base.NBaseAda
import kotlinx.android.synthetic.main.lv_market.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/11/20
 */
class MarketLvAdapter(mContext: Context) : NBaseAda<TransactionPairBean, MarketLvAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_market

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: TransactionPairBean) {
        vh.transactionTv.text = item.name
        vh.exchangeTv.text = item.exchange.toString()
        vh.changeTv.text = item.change.toString()
        vh.total24Tv.text = item.total24.toString()
        vh.costTv.text = item.cost.toString()

    }

    class ViewHolder(view: View) {
        val transactionTv: TextView = view.tv_transaction
        val exchangeTv: TextView = view.tv_exchange
        val total24Tv: TextView = view.tv_total24
        val costTv: TextView = view.tv_cost
        val changeTv: TextView = view.tv_change
    }
}