package com.nze.nzexchange.controller.market

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.OtcBean
import com.nze.nzexchange.bean.SoketRankBean
import com.nze.nzexchange.bean.TransactionPairBean
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.extend.formatForCurrency
import com.nze.nzexchange.extend.formatForLegal
import com.nze.nzexchange.extend.mul
import kotlinx.android.synthetic.main.lv_market.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/11/20
 */
class MarkeOptionaltLvAdapter(mContext: Context) : NBaseAda<SoketRankBean, MarkeOptionaltLvAdapter.ViewHolder>(mContext) {


    override fun setLayout(): Int = R.layout.lv_market

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: SoketRankBean, position: Int) {
        vh.transactionTv.text = item.getCurrency()
        vh.mainCurrencyTv.text = "/${item.getMainCurrency()}"
        vh.exchangeTv.text = item.last.formatForCurrency()
        if (item.change > 0) {
            vh.changeTv.setBackgroundResource(R.drawable.shape_radius_up_bg)
            vh.changeTv.text = "+${item.change}%"
        } else if (item.change == 0.0) {
            vh.changeTv.setBackgroundResource(R.drawable.shape_radius_up_bg)
            vh.changeTv.text = "${item.change}%"
        } else {
            vh.changeTv.setBackgroundResource(R.drawable.shape_radius_down_bg)
            vh.changeTv.text = "${item.change}%"
        }
        vh.total24Tv.text = "24h量 ${item.volume}"
        vh.costTv.text = "¥${item.cny.formatForLegal()}"


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