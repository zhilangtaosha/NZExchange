package com.nze.nzexchange.controller.my.asset.legal

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.LegalRechargeHistoryBean
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.extend.add
import com.nze.nzexchange.extend.formatForLegal
import com.nze.nzexchange.tools.TimeTool
import kotlinx.android.synthetic.main.abc_alert_dialog_material.view.*
import kotlinx.android.synthetic.main.lv_legal_withdraw_history.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/24
 */
class LegalRechargeHistoryAdapter(mContext: Context) : NBaseAda<LegalRechargeHistoryBean, LegalRechargeHistoryAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_legal_withdraw_history

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: LegalRechargeHistoryBean, position: Int) {
        vh.nameTv.text = "充值记录"
        vh.amountKeyTv.text = "充值数量"
        vh.statusTv.text = item.getStatus()
        vh.timeValueTv.text = TimeTool.format(TimeTool.PATTERN2, item.checkpayCreateTime)
        vh.amountValueTv.text = "+${item.checkpayAmt.formatForLegal()}"
        vh.balanceValueTv.text = "${item.checkpayFronzenamount?.add(item.checkpayAbleamount).formatForLegal()}"
    }


    class ViewHolder(view: View) {
        val nameTv: TextView = view.tv_name_llwh
        val statusTv: TextView = view.tv_status_llwh
        val timeValueTv: TextView = view.tv_time_value_llwh
        val amountKeyTv: TextView = view.tv_amount_key_llwh
        val amountValueTv: TextView = view.tv_amount_value_llwh
        val balanceValueTv: TextView = view.tv_balance_value_llwh
    }
}