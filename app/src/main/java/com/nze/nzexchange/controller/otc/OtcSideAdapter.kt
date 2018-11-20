package com.nze.nzexchange.controller.otc

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.AssetBean
import com.nze.nzexchange.controller.base.BaseAda

class OtcSideAdapter(mContext: Context) : BaseAda<AssetBean>(mContext) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val cView: View = mInflater.inflate(R.layout.lv_side_otc, parent, false)
        val tv: TextView = cView as TextView
        tv.setText(getItem(position)?.currency)
        return cView
    }
}