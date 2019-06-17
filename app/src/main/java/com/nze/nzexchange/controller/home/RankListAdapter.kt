package com.nze.nzexchange.controller.home

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.MarketPopularBean
import com.nze.nzexchange.bean.TransactionPairBean
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.controller.base.BaseAda
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.presenter.CommonBibiP
import com.nze.nzexchange.controller.market.KLineActivity
import com.nze.nzexchange.extend.formatForCurrency
import com.nze.nzexchange.extend.formatForLegal
import com.nze.nzexchange.extend.setTxtColor
import kotlinx.android.synthetic.main.abc_alert_dialog_material.view.*
import kotlinx.android.synthetic.main.lv_rank_home.view.*

class RankListAdapter(mContext: Context) : BaseAda<TransactionPairsBean>(mContext) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var vh: ViewHolder? = null
        var cView: View? = null
        if (convertView == null) {
            cView = mInflater.inflate(R.layout.lv_rank_home, parent, false)
            vh = ViewHolder(cView)
            cView.tag = vh
        } else {
            cView = convertView
            vh = cView.tag as ViewHolder
        }
        when (position) {
            0 -> {
                vh.orderNumTv.setBackgroundResource(R.color.color_up)
                vh.orderNumTv.background.mutate().alpha = 255
                vh.orderNumTv.setTxtColor(R.color.white)
            }
            1 -> {
                vh.orderNumTv.setBackgroundResource(R.color.color_up)
                vh.orderNumTv.background.mutate().alpha = (255 * 0.8).toInt()
                vh.orderNumTv.setTxtColor(R.color.white)
            }
            2 -> {
                vh.orderNumTv.setBackgroundResource(R.color.color_up)
                vh.orderNumTv.background.mutate().alpha = (255 * 0.6).toInt()
                vh.orderNumTv.setTxtColor(R.color.white)
            }
            3 -> {
                vh.orderNumTv.setBackgroundResource(R.color.color_up)
                vh.orderNumTv.background.mutate().alpha = (255 * 0.5).toInt()
                vh.orderNumTv.setTxtColor(R.color.white)
            }
            4 -> {
                vh.orderNumTv.setBackgroundResource(R.color.color_up)
                vh.orderNumTv.background.mutate().alpha = (255 * 0.3).toInt()
                vh.orderNumTv.setTxtColor(R.color.white)
            }
            5 -> {
                vh.orderNumTv.setBackgroundResource(R.color.color_FF202833)
                vh.orderNumTv.background.mutate().alpha = 255
                vh.orderNumTv.setTxtColor(R.color.color_head)
            }
            in 6..20 -> {
                vh.orderNumTv.setBackgroundResource(R.color.color_FF202833)
                vh.orderNumTv.setTxtColor(R.color.color_head)
            }
        }
        vh.orderNumTv.text = (position + 1).toString()
        val bean = getItem(position)!!
        vh.transactionPairTv.text = bean.transactionPair
        vh.exchangeTv.text = bean.exchangeRate.formatForCurrency()
        vh.total24Tv.text = "24h量 ${bean.volume}"
        if (bean.gain > 0) {
            vh.changeTv.setBackgroundResource(R.drawable.shape_radius_up_bg)
            vh.changeTv.text = "+${bean.gain}%"
        } else if (bean.gain == 0.0) {
            vh.changeTv.setBackgroundResource(R.drawable.shape_radius_up_bg)
            vh.changeTv.text = "${bean.gain}%"
        } else {
            vh.changeTv.setBackgroundResource(R.drawable.shape_radius_down_bg)
            vh.changeTv.text = "${bean.gain}%"
        }

        CommonBibiP.getInstance(mContext as NBaseActivity)
                .currencyToLegal(bean.currency, 1.0, {
                    if (it.success) {
                        vh.costTv.text = "≈${it.result.formatForLegal()} CNY"
                    } else {
                        vh.costTv.text = "≈0 CNY"
                    }
                }, {
                    vh.costTv.text = "≈0 CNY"
                })

        cView!!.setOnClickListener {
            KLineActivity.skip(mContext, bean)
        }
        return cView!!
    }

    private class ViewHolder(view: View) {
        val orderNumTv: TextView = view.tv_order_num
        val transactionPairTv = view.tv_transaction_pair
        val exchangeTv = view.tv_exchange
        val total24Tv = view.tv_total24
        val costTv = view.tv_cost
        val changeTv = view.tv_change
    }
}