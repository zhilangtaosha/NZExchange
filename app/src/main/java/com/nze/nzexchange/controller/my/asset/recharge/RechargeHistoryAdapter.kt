package com.nze.nzexchange.controller.my.asset.recharge

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransactionListBean
import com.nze.nzexchange.bean2.RechargeHistoryBean
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.controller.my.asset.withdraw.WithdrawHistoryAdapter
import com.nze.nzexchange.extend.formatForCurrency
import com.nze.nzexchange.tools.TimeTool
import kotlinx.android.synthetic.main.lv_withdraw_history.view.*
import kotlinx.android.synthetic.main.rcv_recharge_history_content.view.*
import kotlinx.android.synthetic.main.rcv_recharge_history_title.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/25
 */
class RechargeHistoryAdapter(mContext: Context) : NBaseAda<TransactionListBean, RechargeHistoryAdapter.ViewHolder>(mContext) {

    override fun setLayout(): Int = R.layout.lv_withdraw_history

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: TransactionListBean, position: Int) {
        vh.amountTv.text = item.amount.formatForCurrency()
        when (item.status) {
            2001 -> {//提币成功
                vh.statusTv.text = "审核中"
            }
            2002 -> {//提币审核
                vh.statusTv.text = "已完成"
            }
            2004 -> {//提币失败
                vh.statusTv.text = "充币失败"
            }
        }

        vh.dateTv.text = TimeTool.format(TimeTool.PATTERN2, item.createTime)
    }


    class ViewHolder(view: View) {
        val amountTv: TextView by lazy { view.tv_amount_lwh }
        val statusTv: TextView by lazy { view.tv_status_lwh }
        val dateTv: TextView by lazy { view.tv_date_lwh }
    }
}