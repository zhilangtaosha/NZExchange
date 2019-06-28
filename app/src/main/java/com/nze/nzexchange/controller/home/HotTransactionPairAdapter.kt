package com.nze.nzexchange.controller.home

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.MarketPopularBean
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.presenter.CommonBibiP
import com.nze.nzexchange.controller.market.KLineActivity
import com.nze.nzexchange.extend.formatForLegal
import com.nze.nzexchange.extend.setTxtColor
import com.nze.nzexchange.widget.recyclerview.ViewType
import kotlinx.android.synthetic.main.recyclerview_hot_transaction_pair.view.*

/**
 * 热门交易对adapter
 */
class HotTransactionPairAdapter(var context: Context, var datas: MutableList<TransactionPairsBean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        datas.add(0, TransactionPairsBean())
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            ViewType.HEADER.value
        } else {
            ViewType.CONTENT.value
        }

    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == ViewType.HEADER.value) {

        } else {
            holder as HTPViewHolder
            val pairBean = datas[position]
            if (pairBean.gain > 0) {
                holder.tvChange.setTxtColor(R.color.color_up)
                holder.tvChange.text = "+${pairBean.gain}%"
            } else if (pairBean.gain == 0.0) {
                holder.tvChange.setTxtColor(R.color.color_up)
                holder.tvChange.text = "${pairBean.gain}%"
            } else {
                holder.tvChange.setTxtColor(R.color.color_down)
                holder.tvChange.text = "${pairBean.gain}%"
            }

            holder.tvName.text = pairBean.transactionPair
            holder.tvExchange.text = pairBean.exchangeRate.toString()
            holder.tvCost.text = (pairBean.exchangeRate * 500).toString()
            holder.tvCost.tag = pairBean.currency
            holder.tvCost.text = "≈${pairBean.cny.formatForLegal()} CNY"
//            CommonBibiP.getInstance(context as NBaseActivity)
//                    .currencyToLegal(pairBean.currency, 1.0, {
//                        if (it.success) {
//                            holder.tvCost.text = "≈${it.result.formatForLegal()} CNY"
//                        } else {
//                            holder.tvCost.text = "≈0 CNY"
//                        }
//                    }, {
//                        holder.tvCost.text = "≈0 CNY"
//                    })

            holder.rootLayout.setOnClickListener {
                KLineActivity.skip(context, pairBean)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            HeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.recyclerview_empty_header, parent, false))
        } else {
            HTPViewHolder(LayoutInflater.from(context).inflate(R.layout.recyclerview_hot_transaction_pair, parent, false))
        }

    }

    override fun getItemCount(): Int = datas.size


    class HTPViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rootLayout: LinearLayout = itemView.layout_root_rhtp
        var tvChange: TextView = itemView.tv_change_rhtp
        var tvName: TextView = itemView.tv_name_rhtp
        var tvExchange: TextView = itemView.tv_exchange_rhtp
        var tvCost: TextView = itemView.tv_cost_rhtp

    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}