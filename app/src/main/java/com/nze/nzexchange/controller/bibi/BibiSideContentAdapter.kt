package com.nze.nzexchange.controller.bibi

import android.content.Context
import android.view.TextureView
import android.view.View
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.SoketRankBean
import com.nze.nzexchange.bean.TransactionPairBean
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.extend.formatForCurrency
import com.nze.nzexchange.extend.getCheckListener
import com.nze.nzexchange.extend.setTextFromHtml
import com.nze.nzexchange.extend.setTxtColor
import kotlinx.android.synthetic.main.lv_bibi_side_content.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/4
 */
class BibiSideContentAdapter(mContext: Context) : NBaseAda<SoketRankBean, BibiSideContentAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_bibi_side_content

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: SoketRankBean, position: Int) {
        vh.currencyTv.text = item.getCurrency()
        vh.mainCurrencyTv.text = "/${item.getMainCurrency()}"
        vh.costTv.text = item.last.formatForCurrency()

        vh.choiceCb.isSelected = item.optional == 1
        if (item.change > 0) {
            vh.changeTv.setTxtColor(R.color.color_up)
            vh.changeTv.text = "+${item.change}"
        } else if (item.change == 0.0) {
            vh.changeTv.setTxtColor(R.color.color_up)
            vh.changeTv.text = "${item.change}"
        } else {
            vh.changeTv.setTxtColor(R.color.color_down)
            vh.changeTv.text = "${item.change}"
        }

//        vh.rootLayout.setOnClickListener {
//            onItemClick?.itemClick(item)
//        }
        vh.choiceCb.tag = position
        vh.choiceCb.setOnClickListener {
            val p: Int = it.tag as Int
            if (UserBean.isLogin(mContext))
                onItemClick?.selftSelect(item, position)

        }


    }

    var onItemClick: OnItemClickListener? = null

    interface OnItemClickListener {
        //        fun itemClick(item: TransactionPairsBean)
        fun selftSelect(item: SoketRankBean, position: Int)
    }

    class ViewHolder(var view: View) {
        val rootLayout = view.layout_root_lbsc
        val choiceCb = view.cb_choice_lbsc
        val currencyTv = view.tv_currency_lbsc
        val mainCurrencyTv: TextView = view.tv_main_currecy_lbsc
        val costTv = view.tv_cost_lbsc
        val changeTv = view.tv_change_lbsc
    }
}