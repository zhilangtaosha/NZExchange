package com.nze.nzexchange.controller.bibi

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.HandicapBean
import com.nze.nzexchange.bean.OrderPendBean
import com.nze.nzexchange.config.OrderStatus
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.extend.setTxtColor
import com.nze.nzexchange.tools.TimeTool
import com.nze.nzexchange.tools.getNColor
import kotlinx.android.synthetic.main.lv_bibi_current_order.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明: 当前委托列表
 * @创建时间：2018/12/4
 */
class BibiCurentOrderAdapter(mContext: Context) : NBaseAda<OrderPendBean, BibiCurentOrderAdapter.ViewHolder>(mContext) {
    var mainCurrency: String? = null
    var currency: String? = null

    var cancelClick: ((position: Int, item: OrderPendBean) -> Unit)? = null

    override fun setLayout(): Int = R.layout.lv_bibi_current_order

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: OrderPendBean, position: Int) {
        if (item.side == 1) {//；卖出
            vh.statusTv.setTxtColor(R.color.color_FFFF4A5F)
            vh.statusTv.text = "卖出"
        } else {//买入
            vh.statusTv.setTxtColor(R.color.color_FF019D81)
            vh.statusTv.text = "买入"
        }
        val split = item.market.split("/")
        val currency = split[0]
        val mainCurrency = split[1]
        vh.timeTv.text = TimeTool.format(TimeTool.PATTERN2, (item.ftime * 1000).toLong())

        vh.costKeyTv.text = "价格(${mainCurrency})"
        vh.costValueTv.text = item.price

        vh.amountKeyTv.text = "数量(${currency})"
        vh.amountValueTv.text = item.amount

        vh.realAmountKeyTv.text = "实际成交(${currency})"
        vh.realAmountValueTv.text = item.deal_money

        when (item.status) {
            OrderStatus.COMPLETED -> {
                vh.cancelTv.text = "已完成"
                vh.cancelTv.setTextColor(getNColor(R.color.color_common))
                vh.cancelTv.setBackgroundColor(getNColor(R.color.transparent))
                vh.cancelTv.isClickable=false
            }
            OrderStatus.RESCINDED -> {
                vh.cancelTv.text = "已撤销"
                vh.cancelTv.setTextColor(getNColor(R.color.color_common))
                vh.cancelTv.setBackgroundColor(getNColor(R.color.transparent))
                vh.cancelTv.isClickable=false
            }
            OrderStatus.NO_COMPLETE -> {
                vh.cancelTv.text = "撤销"
                vh.cancelTv.setTextColor(getNColor(R.color.color_main))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    vh.cancelTv.background=ContextCompat.getDrawable(mContext,R.drawable.shape_radius_2536_bg2)
                }else{
                    vh.cancelTv.setBackgroundDrawable(ContextCompat.getDrawable(mContext,R.drawable.shape_radius_2536_bg2))
                }
                vh.cancelTv.isClickable=true
                vh.cancelTv.setOnClickListener {
                    cancelClick?.invoke(position, item)
                }
            }
        }


    }

    class ViewHolder(val view: View) {
        val timeTv: TextView = view.tv_time_lbco
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