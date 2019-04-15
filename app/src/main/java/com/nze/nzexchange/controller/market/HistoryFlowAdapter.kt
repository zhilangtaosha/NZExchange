package com.nze.nzexchange.controller.market

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.controller.base.NBaseAda
import kotlinx.android.synthetic.main.flow_search_history.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/15
 */
class HistoryFlowAdapter(mContext: Context) : NBaseAda<TransactionPairsBean, HistoryFlowAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.flow_search_history

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: TransactionPairsBean, position: Int) {
        vh.historyTv.text = item.transactionPair
    }


    class ViewHolder(view: View) {
        val historyTv: TextView = view.tv_history

    }
}