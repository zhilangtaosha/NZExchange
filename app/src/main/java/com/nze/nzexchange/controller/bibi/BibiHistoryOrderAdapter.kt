package com.nze.nzexchange.controller.bibi

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.SoketOrderBean
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.extend.*
import com.nze.nzexchange.tools.TimeTool
import com.nze.nzexchange.tools.getNColor
import kotlinx.android.synthetic.main.lv_bibi_history_order.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明: 当前委托列表
 * @创建时间：2018/12/4
 */
class BibiHistoryOrderAdapter(mContext: Context) : NBaseAda<SoketOrderBean, BibiHistoryOrderAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_bibi_history_order

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: SoketOrderBean, position: Int) {
        if (item.side == 1) {//；卖出
            vh.statusTv.setTxtColor(R.color.color_FFFF4A5F)
            vh.statusTv.text = "卖出"
        } else {//买入
            vh.statusTv.setTxtColor(R.color.color_FF019D81)
            vh.statusTv.text = "买入"
        }
        vh.pairTv.text = "${item.currency}/${item.mainCurrency}"

        vh.entrustPriceKey.text = "委托价(${item.mainCurrency})"

        vh.transactionTotalKey.text = "成交总额(${item.mainCurrency})"
        vh.transactionPriceKey.text = "成交均价(${item.mainCurrency})"


        vh.timeValue.text = TimeTool.format(TimeTool.PATTERN2, (item.ctime * 1000).toLong())

        if (item.type == 1) {//限价
            //委托价格
            vh.entrustPriceValue.text = item.price.formatForCurrency()
            //成交均价
            if (item.deal_stock != 0.0) {
                vh.transactionPriceValue.text = item.deal_money.divByFloor(item.deal_stock, 8).formatForCurrency()
            } else {
                vh.transactionPriceValue.text = "0"
            }
            vh.entrustAmountKey.text = "委托量(${item.currency})"
            vh.transactionAmountKey.text = "成交量(${item.currency})"
        } else {//市价
            vh.entrustPriceValue.text = "市价"
            vh.transactionPriceValue.text = "市价"
            if (item.side == 2) {
                vh.entrustAmountKey.text = "委托量(${item.mainCurrency})"
                vh.transactionAmountKey.text = "成交量(${item.mainCurrency})"
            } else {
                vh.entrustAmountKey.text = "委托量(${item.currency})"
                vh.transactionAmountKey.text = "成交量(${item.currency})"
            }
        }


        vh.entrustAmountValue.text = item.amount.formatForCurrency()
        vh.transactionTotalValue.text = item.deal_money.formatForCurrency()

        vh.transactionAmountValue.text = item.deal_stock.formatForCurrency()


        if (item.deal_stock==item.amount){
            vh.cancelTv.text = "已完成"
            vh.cancelTv.setTextColor(getNColor(R.color.color_common))
            vh.cancelTv.setBackgroundColor(getNColor(R.color.transparent))
            vh.cancelTv.isClickable = false
        }else{
            vh.cancelTv.text = "已撤销"
            vh.cancelTv.setTextColor(getNColor(R.color.color_common))
            vh.cancelTv.setBackgroundColor(getNColor(R.color.transparent))
            vh.cancelTv.isClickable = false
        }

    }

    class ViewHolder(val view: View) {
        val statusTv: TextView = view.tv_status_lbco
        val pairTv: TextView = view.tv_pair_lbco
        val cancelTv: TextView = view.tv_cancel_lbco
        val entrustPriceKey: TextView = view.tv_entrust_price_key_lbco//委托价
        val entrustAmountKey: TextView = view.tv_entrust_amount_key_lbco//委托数量
        val timeValue: TextView = view.tv_time_value_lbco
        val entrustPriceValue: TextView = view.tv_entrust_price_value_lbco
        val entrustAmountValue: TextView = view.tv_entrust_amount_value_lbco
        val transactionTotalKey: TextView = view.tv_transaction_total_key_lbco//成交总额
        val transactionPriceKey: TextView = view.tv_transaction_price_key_lbco//成交均价
        val transactionAmountKey: TextView = view.tv_transaction_amount_key_lbco//成交量
        val transactionTotalValue: TextView = view.tv_transaction_total_value_lbco
        val transactionPriceValue: TextView = view.tv_transaction_price_value_lbco
        val transactionAmountValue: TextView = view.tv_transaction_amount_value_lbco
    }
}