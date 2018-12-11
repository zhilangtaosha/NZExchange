package com.nze.nzexchange.controller.bibi

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.HandicapBean
import com.nze.nzexchange.controller.base.NBaseAda
import kotlinx.android.synthetic.main.lv_bibi_current_order.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/4
 */
class BibiCurentOrderAdapter(mContext: Context) : NBaseAda<HandicapBean, BibiCurentOrderAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_bibi_current_order

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: HandicapBean,position: Int) {


    }

    class ViewHolder(val view: View) {
        val cancelTv: TextView = view.tv_cancel_lbco
        val costValueTv: TextView = view.tv_cost_value_lbco
        val amountValueTv: TextView = view.tv_amount_value_lbco
        val realAmountValueTv: TextView = view.tv_real_amount_value_lbco
    }
}