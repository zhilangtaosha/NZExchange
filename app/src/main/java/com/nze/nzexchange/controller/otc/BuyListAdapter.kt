package com.nze.nzexchange.controller.otc

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.OtcBean
import com.nze.nzexchange.controller.base.BaseAda
import com.nze.nzexchange.controller.otc.buy.BuyActivity
import com.nze.nzexchange.widget.LinearLayoutAsListView
import kotlinx.android.synthetic.main.lv_buy_otc.view.*

class BuyListAdapter(mContext: Context) : BaseAda<OtcBean>(mContext) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var vh: ViewHolder? = null
        var cView: View? = null
        if (convertView == null) {
            cView = mInflater.inflate(R.layout.lv_buy_otc, parent, false)
            vh = ViewHolder(cView)
            cView.tag = vh
        } else {
            cView = convertView
            vh = cView.tag as ViewHolder
        }

        vh.buyBtn.setOnClickListener {
            mContext.startActivity(Intent(mContext, BuyActivity::class.java))
        }
        return cView!!
    }

    private class ViewHolder(view: View) {
        var nameTv: TextView = view.tv_name_lbo
        var limitTv: TextView = view.tv_limit_lbo
        var totalNumTv: TextView = view.tv_total_num_lbo
        var payLav: LinearLayoutAsListView = view.lav_pay_lbo as LinearLayoutAsListView
        var orderNumTv: TextView = view.tv_order_num_lbo
        var priceTv: TextView = view.tv_price_lbo
        var buyBtn: Button = view.btn_buy_lbo

    }
}