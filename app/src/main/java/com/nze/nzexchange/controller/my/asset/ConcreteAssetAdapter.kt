package com.nze.nzexchange.controller.my.asset

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.FinancialRecordBean
import com.nze.nzexchange.bean2.ConcreteAssetBean
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.extend.formatForCurrency
import kotlinx.android.synthetic.main.lv_concrete_currency_asset.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/1/10
 */
class ConcreteAssetAdapter(mContext: Context) : NBaseAda<FinancialRecordBean, ConcreteAssetAdapter.ViewHolder>(mContext) {

    override fun setLayout(): Int = R.layout.lv_concrete_currency_asset

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: FinancialRecordBean, position: Int) {
        vh.amountTv.text = item.amount.formatForCurrency()

    }

    class ViewHolder(view: View) {
        val amountTv: TextView by lazy { view.tv_amount_lcca }
        val statusTv: TextView by lazy { view.tv_status_lcca }
        val dateTv: TextView by lazy { view.tv_date_lcca }
    }
}