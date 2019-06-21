package com.nze.nzexchange.controller.my.asset.transfer

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransferRecordBean
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.extend.formatForCurrency
import com.nze.nzexchange.tools.TimeTool
import kotlinx.android.synthetic.main.lv_transfer_history.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/17
 */
class TransferHistoryAdapter(mContext: Context) : NBaseAda<TransferRecordBean, TransferHistoryAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_transfer_history

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: TransferRecordBean, position: Int) {
        vh.currencyTv.text = item.token
        vh.dateTv.text = TimeTool.format(TimeTool.PATTERN2, item.createTime)
        vh.amountValueTv.text = item.amount.formatForCurrency()
        var from = when (item.from) {
            "coin" -> {
                "币币账户"
            }
            "outside" -> {
                "OTC账户"
            }
            "legalCurrency" -> {
                "法币账户"
            }
            else -> {
                "币币账户"
            }
        }
        var to = when (item.to) {
            "coin" -> {
                "币币账户"
            }
            "outside" -> {
                "OTC账户"
            }
            "legalCurrency" -> {
                "法币账户"
            }
            else -> {
                "币币账户"
            }
        }
        vh.typeValueTv.text = "${from}到${to}"
    }


    class ViewHolder(view: View) {
        val currencyTv: TextView = view.tv_currency_lth
        val dateTv: TextView = view.tv_date_lth
        val amountValueTv: TextView = view.tv_amount_value_lth
        val typeValueTv: TextView = view.tv_type_value_lth
    }
}