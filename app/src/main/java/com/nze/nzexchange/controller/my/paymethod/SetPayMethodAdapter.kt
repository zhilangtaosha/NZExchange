package com.nze.nzexchange.controller.my.paymethod

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.PayMethodBean
import com.nze.nzexchange.controller.base.NBaseAda
import kotlinx.android.synthetic.main.lv_set_pay.view.*

class SetPayMethodAdapter(mContext: Context) : NBaseAda<PayMethodBean, SetPayMethodAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_set_pay

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: PayMethodBean,position: Int) {
        vh.iconIv.setImageResource(item.iconId)
        vh.nameTv.text = item.method
        vh.statusTv.text = item.status
    }

    class ViewHolder(val view: View) {
        val iconIv: ImageView = view.iv_lsp
        val nameTv: TextView = view.tv_name_lsp
        val statusTv: TextView = view.tv_status_lsp
    }
}