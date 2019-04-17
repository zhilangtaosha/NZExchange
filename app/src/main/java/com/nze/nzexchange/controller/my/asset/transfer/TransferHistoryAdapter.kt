package com.nze.nzexchange.controller.my.asset.transfer

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseAda
import kotlinx.android.synthetic.main.lv_transfer_history.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/17
 */
class TransferHistoryAdapter(mContext: Context) : NBaseAda<Any, TransferHistoryAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_transfer_history

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: Any, position: Int) {

    }


    class ViewHolder(view: View) {
        val currencyTv: TextView = view.tv_currency_lth
        val dateTv: TextView = view.tv_date_lth
        val amountValueTv: TextView = view.tv_amount_value_lth
        val typeValueTv: TextView = view.tv_type_value_lth
    }
}