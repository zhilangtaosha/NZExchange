package com.nze.nzexchange.controller.otc

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.OtcBean
import com.nze.nzexchange.controller.base.BaseAda
import com.nze.nzexchange.Extend.setBg
import com.nze.nzexchange.Extend.setTxtColor
import com.nze.nzexchange.widget.LinearLayoutAsListView
import kotlinx.android.synthetic.main.lv_buy_otc.view.*

class OtcBuyAdapter(mContext: Context, val type: Int) : BaseAda<OtcBean>(mContext) {
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

        if (type == OtcContentFragment.TYPE_BUY) {
            vh.btn.setBg(R.drawable.selector_green_btn_bg)
            vh.priceTv.setTxtColor(R.color.color_FF069F83)
        } else {
            vh.btn.setBg(R.drawable.selector_red_btn_bg)
            vh.priceTv.setTxtColor(R.color.color_FFE05760)
        }

        vh.btn.setOnClickListener {
            mContext.startActivity(Intent(mContext, BuyActivity::class.java).putExtra(OtcContentFragment.PARAM_TYPE, type))
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
        var btn: Button = view.btn_lbo

    }
}