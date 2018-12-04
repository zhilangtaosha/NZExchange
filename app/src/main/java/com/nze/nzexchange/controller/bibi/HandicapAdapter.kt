package com.nze.nzexchange.controller.bibi

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.HandicapBean
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.extend.setTxtColor
import kotlinx.android.synthetic.main.lv_bibi_handicap.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/4
 */
class HandicapAdapter(mContext: Context, var type: Int) : NBaseAda<HandicapBean, HandicapAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_bibi_handicap

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: HandicapBean) {

        if (type == SALE) {
            vh.costTv.setTxtColor(R.color.color_FFFF4A5F)
        } else {
            vh.costTv.setTxtColor(R.color.color_FF019D81)
        }
    }

    class ViewHolder(val view: View) {
        val handicapTv: TextView = view.tv_handicap_lbh
        val costTv: TextView = view.tv_cost_lbh
        val amountTv: TextView = view.tv_amount_lbh
    }


    companion object {
        const val SALE = 0x001
        const val BUY = 0x002

    }
}