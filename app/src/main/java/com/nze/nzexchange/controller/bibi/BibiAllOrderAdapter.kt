package com.nze.nzexchange.controller.bibi

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.SoketOrderBean
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.extend.formatForCurrency
import com.nze.nzexchange.extend.formatForPrice
import com.nze.nzexchange.extend.setTxtColor
import com.nze.nzexchange.tools.TimeTool
import kotlinx.android.synthetic.main.lv_bibi_current_order.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明: 当前委托列表
 * @创建时间：2018/12/4
 */
class BibiAllOrderAdapter(mContext: Context) : NBaseAda<SoketOrderBean, BibiAllOrderAdapter.ViewHolder>(mContext) {
    var mainCurrency: String? = null
    var currency: String? = null

    var cancelClick: ((position: Int, item: SoketOrderBean) -> Unit)? = null

    override fun setLayout(): Int = R.layout.lv_bibi_current_order

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: SoketOrderBean, position: Int) {
        if (item.side == 1) {//；卖出
            vh.statusTv.setTxtColor(R.color.color_FFFF4A5F)
            vh.statusTv.text = "卖出"
        } else {//买入
            vh.statusTv.setTxtColor(R.color.color_FF019D81)
            vh.statusTv.text = "买入"
        }
        vh.pairTv.text = "${item.currency}/${item.mainCurrency}"
//        val split = item.market.split("/")
//        val currency = split[0]
//        val mainCurrency = split[1]
        vh.timeTv.text = TimeTool.format(TimeTool.PATTERN2, (item.ctime * 1000).toLong())

        vh.costKeyTv.text = "价格(${item.mainCurrency})"
        if (item.type == 1) {
            vh.costValueTv.text = item.price.formatForPrice()
            vh.amountKeyTv.text = "数量(${item.currency})"
        } else {
            vh.costValueTv.text = "市价"
            if (item.side == 2) {
                vh.amountKeyTv.text = "数量(${item.mainCurrency})"
            } else {
                vh.amountKeyTv.text = "数量(${item.currency})"
            }
        }


        vh.amountValueTv.text = item.amount.formatForCurrency()

        vh.realAmountKeyTv.text = "实际成交(${item.currency})"
        vh.realAmountValueTv.text = item.deal_stock.formatForCurrency()

//        when (item.status) {
//            OrderStatus.COMPLETED -> {
//                vh.cancelTv.text = "已完成"
//                vh.cancelTv.setTextColor(getNColor(R.color.color_common))
//                vh.cancelTv.setBackgroundColor(getNColor(R.color.transparent))
//                vh.cancelTv.isClickable = false
//            }
//            OrderStatus.RESCINDED -> {
//                vh.cancelTv.text = "已撤销"
//                vh.cancelTv.setTextColor(getNColor(R.color.color_common))
//                vh.cancelTv.setBackgroundColor(getNColor(R.color.transparent))
//                vh.cancelTv.isClickable = false
//            }
//            OrderStatus.NO_COMPLETE -> {
//                vh.cancelTv.text = "撤销"
//                vh.cancelTv.setTextColor(getNColor(R.color.color_main))
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    vh.cancelTv.background = ContextCompat.getDrawable(mContext, R.drawable.shape_radius_2536_bg2)
//                } else {
//                    vh.cancelTv.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.shape_radius_2536_bg2))
//                }
//                vh.cancelTv.isClickable = true
//            }
//        }
        if (item.left == 0.0) {
            vh.cancelTv.visibility = View.GONE
        } else {
            vh.cancelTv.visibility = View.VISIBLE
        }
        vh.cancelTv.setOnClickListener {
            cancelClick?.invoke(position, item)
        }


    }

    class ViewHolder(val view: View) {
        val timeTv: TextView = view.tv_time_lbco
        val pairTv: TextView = view.tv_pair_lbco
        val statusTv: TextView = view.tv_status_lbco
        val cancelTv: TextView = view.tv_cancel_lbco
        val costKeyTv: TextView = view.tv_cost_key_lbco
        val costValueTv: TextView = view.tv_cost_value_lbco
        val amountKeyTv: TextView = view.tv_amount_key_lbco
        val amountValueTv: TextView = view.tv_amount_value_lbco
        val realAmountKeyTv: TextView = view.tv_real_amount_key_lbco
        val realAmountValueTv: TextView = view.tv_real_amount_value_lbco
    }
}