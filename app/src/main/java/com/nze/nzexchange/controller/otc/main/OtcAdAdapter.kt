package com.nze.nzexchange.controller.otc.main

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.OtcBean
import com.nze.nzexchange.controller.base.NBaseAda
import kotlinx.android.synthetic.main.lv_ad_otc.view.*

class OtcAdAdapter(mContext: Context) : NBaseAda<OtcBean, OtcAdAdapter.ViewHolder>(mContext) {
    override fun initView(vh: ViewHolder, item: OtcBean) {
        vh.nameTv.text = item.currency
        vh.timeTv.text = item.time
        vh.priceTv.text = item.price.toString()
        vh.totalNumTv.text = item.totalNum.toString()
        vh.transactionNumTv.text = item.transactionNum.toString()

    }

    override fun setLayout(): Int = R.layout.lv_ad_otc

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)


    class ViewHolder(val v: View) {
        val nameTv: TextView = v.tv_name_lao
        val timeTv: TextView = v.tv_time_lao
        val cancelBtn: Button = v.btn_cancel_lao
        val priceTv: TextView = v.tv_price_lao
        val totalNumTv: TextView = v.tv_total_num_lao
        val transactionNumTv: TextView = v.tv_transaction_num_lao
    }
}


