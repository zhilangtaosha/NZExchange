package com.nze.nzexchange.controller.my.asset

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseAda
import kotlinx.android.synthetic.main.lv_select_currency.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/19
 */
class SelectCurrencyAdapter(mContext: Context) : NBaseAda<String, SelectCurrencyAdapter.ViewHolder>(mContext) {

    override fun setLayout(): Int = R.layout.lv_select_currency

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: String, position: Int) {
        vh.currencyTv.text = item
    }


    class ViewHolder(view: View) {
        val currencyTv: TextView = view.tv_currency_lsc
    }
}