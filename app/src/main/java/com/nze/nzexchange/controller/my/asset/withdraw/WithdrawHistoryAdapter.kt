package com.nze.nzexchange.controller.my.asset.withdraw

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TickRecordBean
import com.nze.nzexchange.bean2.WithdrawHistoryBean
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.tools.TimeTool
import kotlinx.android.synthetic.main.lv_withdraw_history.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/26
 */
class WithdrawHistoryAdapter(mContext: Context) : NBaseAda<TickRecordBean, WithdrawHistoryAdapter.ViewHolder>(mContext) {

    override fun setLayout(): Int = R.layout.lv_withdraw_history

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: TickRecordBean, position: Int) {
        vh.amountTv.text = item.number
        vh.statusTv.text = "已完成"
        vh.dateTv.text = TimeTool.format(TimeTool.PATTERN2, item.datetime)
    }


    class ViewHolder(view: View) {
        val amountTv: TextView by lazy { view.tv_amount_lwh }
        val statusTv: TextView by lazy { view.tv_status_lwh }
        val dateTv: TextView by lazy { view.tv_date_lwh }
    }
}