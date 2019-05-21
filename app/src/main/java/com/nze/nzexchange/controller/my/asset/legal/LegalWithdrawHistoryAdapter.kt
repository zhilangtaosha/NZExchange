package com.nze.nzexchange.controller.my.asset.legal

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.LegalWithdrawHistoryBean
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.tools.TimeTool
import kotlinx.android.synthetic.main.lv_legal_withdraw_history.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/24
 */
class LegalWithdrawHistoryAdapter(mContext: Context) : NBaseAda<LegalWithdrawHistoryBean, LegalWithdrawHistoryAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_legal_withdraw_history

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: LegalWithdrawHistoryBean, position: Int) {
        vh.nameTv.text = "提现记录"
        vh.statusTv.text = item.getStatus()
        vh.timeValueTv.text = TimeTool.format(TimeTool.PATTERN2, item.pickfundCreateTime)
        vh.amountValueTv.text = "-${item.pickfundApplyamt}"
        vh.balanceValueTv.text = "${item.pickfundAbleamount + item.pickfundFronzenamount}"
    }


    class ViewHolder(view: View) {
        val nameTv: TextView = view.tv_name_llwh
        val statusTv: TextView = view.tv_status_llwh
        val timeValueTv: TextView = view.tv_time_value_llwh
        val amountValueTv: TextView = view.tv_amount_value_llwh
        val balanceValueTv: TextView = view.tv_balance_value_llwh
    }
}