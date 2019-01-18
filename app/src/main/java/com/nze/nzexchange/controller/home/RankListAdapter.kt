package com.nze.nzexchange.controller.home

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransactionPairBean
import com.nze.nzexchange.controller.base.BaseAda
import com.nze.nzexchange.extend.setTxtColor
import kotlinx.android.synthetic.main.lv_rank_home.view.*

class RankListAdapter(mContext: Context) : BaseAda<TransactionPairBean>(mContext) {
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
            6 -> {
                vh.orderNumTv.setBackgroundResource(R.color.color_FF202833)
                vh.orderNumTv.background.mutate().alpha = (255 * 0.8).toInt()
                vh.orderNumTv.setTxtColor(R.color.color_head)
            }
            7 -> {
                vh.orderNumTv.setBackgroundResource(R.color.color_FF202833)
                vh.orderNumTv.background.mutate().alpha = (255 * 0.6).toInt()
                vh.orderNumTv.setTxtColor(R.color.color_head)
            }
            8 -> {
                vh.orderNumTv.setBackgroundResource(R.color.color_FF202833)
                vh.orderNumTv.background.mutate().alpha = (255 * 0.5).toInt()
                vh.orderNumTv.setTxtColor(R.color.color_head)
            }
            9 -> {
                vh.orderNumTv.setBackgroundResource(R.color.color_FF202833)
                vh.orderNumTv.background.mutate().alpha = (255 * 0.3).toInt()
                vh.orderNumTv.setTxtColor(R.color.color_head)
            }
        }
        vh.orderNumTv.text = position.toString()

        return cView!!
    }

    private class ViewHolder(view: View) {
        val orderNumTv: TextView = view.tv_order_num
        val transactionPairTv = view.tv_transaction_pair
        val exchangeTv = view.tv_exchange
        val total24Tv = view.tv_total24
        val costTv = view.tv_cost
    }
}