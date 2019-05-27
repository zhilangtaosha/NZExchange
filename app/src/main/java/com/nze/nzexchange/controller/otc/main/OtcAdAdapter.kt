package com.nze.nzexchange.controller.otc.main

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.FindSellBean
import com.nze.nzexchange.bean.OtcBean
import com.nze.nzexchange.config.CurrencyTool
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.extend.formatForCurrency
import com.nze.nzexchange.extend.formatForLegal
import com.nze.nzexchange.extend.setTxtColor
import com.nze.nzexchange.tools.TimeTool
import kotlinx.android.synthetic.main.lv_ad_otc.view.*

class OtcAdAdapter(mContext: Context) : NBaseAda<FindSellBean, OtcAdAdapter.ViewHolder>(mContext) {
    var onClick: ((String, String, Int) -> Unit)? = null
    override fun initView(vh: ViewHolder, item: FindSellBean, position: Int) {
        item.run {
            val tip = if (transactionType == FindSellBean.TRANSACTIONTYPE_BUY) {
                vh.nameTv.setTxtColor(R.color.color_buy)
                "购买"
            } else {
                vh.nameTv.setTxtColor(R.color.color_sale)
                "出售"
            }
            vh.nameTv.text = "${tip}${CurrencyTool.getCurrency(tokenId)}"
            vh.timeTv.text = TimeTool.format(TimeTool.PATTERN2, poolCreateTime)
            vh.priceTv.text = poolPrice.formatForLegal()
            if (item.poolStatus == 1004) {
                vh.totalNumTv.text = item.poolLockamount.formatForCurrency()
            } else {
                vh.totalNumTv.text = poolAllCount.formatForCurrency()

            }
            vh.transactionNumTv.text = poolSuccessamount.formatForCurrency()

        }

        vh.cancelBtn.setOnClickListener {
            onClick?.invoke(item.poolId, item.userId, item.transactionType)
        }
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


