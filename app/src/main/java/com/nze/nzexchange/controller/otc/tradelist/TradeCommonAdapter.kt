package com.nze.nzexchange.controller.otc.tradelist

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.OtcOrder
import com.nze.nzexchange.bean.SubOrderInfoBean
import com.nze.nzexchange.config.CurrencyTool
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.tools.TimeTool
import kotlinx.android.synthetic.main.lv_trade_common.view.*

class TradeCommonAdapter(mContext: Context, var type: Int, var fragment: NBaseFragment) : NBaseAda<SubOrderInfoBean, TradeCommonAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_trade_common

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: SubOrderInfoBean) {
        item.run {
            val type = if (transactionType == SubOrderInfoBean.TRANSACTIONTYPE_BUY) "买入" else "卖出"
            vh.typeTv.text = "${type}${CurrencyTool.getCurrency(tokenId)}"
            vh.timeTv.text = TimeTool.format(TimeTool.PATTERN2, suborderCreateTime)
            vh.statusTv.text = SubOrderInfoBean.getStatus(suborderStatus)
            vh.priceTv.text = suborderPrice.toString()
            vh.numTv.text = "数量: $suborderNum"
            vh.moneyTv.text = "总额: $suborderAmount"
        }

    }

    class ViewHolder(view: View) {
        val typeTv: TextView = view.tv_type_ltc
        val timeTv: TextView = view.tv_time_ltc
        val statusTv: TextView = view.tv_status_ltc
        val priceTv: TextView = view.tv_price_ltc
        val numTv: TextView = view.tv_num_ltc
        val moneyTv: TextView = view.tv_money_ltc

    }
}