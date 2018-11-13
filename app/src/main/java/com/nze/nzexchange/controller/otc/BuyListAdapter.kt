package com.nze.nzexchange.controller.otc

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.OtcBean
import com.nze.nzexchange.controller.base.BaseAda

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
        return cView!!
    }

    private class ViewHolder(view: View) {

    }
}