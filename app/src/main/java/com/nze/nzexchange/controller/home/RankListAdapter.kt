package com.nze.nzexchange.controller.home

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransactionPairBean
import com.nze.nzexchange.controller.base.BaseAda
import kotlinx.android.synthetic.main.lv_rank_home.view.*

class RankListAdapter(mContext: Context) : BaseAda<TransactionPairBean>(mContext) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var vh: ViewHolder? = null
        var cView: View? = null
        if (convertView == null) {
            cView = mInflater.inflate(R.layout.lv_rank_home, parent, false)
            vh = ViewHolder(cView)
            cView.tag = vh
        } else {
            cView = convertView
            vh = cView.tag as ViewHolder
        }
        return cView!!
    }

    private class ViewHolder(view: View) {
        val orderNumTv: TextView = view.tv_order_num
        val transactionPairTv = view.tv_transaction_pair
        val exchangeTv = view.tv_exchange
        val total24Tv = view.tv_total24
        val costTv = view.tv_cost
    }
}