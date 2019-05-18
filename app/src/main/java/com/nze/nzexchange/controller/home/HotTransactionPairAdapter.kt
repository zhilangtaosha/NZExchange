package com.nze.nzexchange.controller.home

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.MarketPopularBean
import com.nze.nzexchange.bean.TransactionPairBean
import com.nze.nzexchange.extend.setTxtColor
import com.nze.nzexchange.widget.recyclerview.ViewType
import kotlinx.android.synthetic.main.recyclerview_hot_transaction_pair.view.*

/**
 * 热门交易对adapter
 */
class HotTransactionPairAdapter(var context: Context, var datas: MutableList<MarketPopularBean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        datas.add(0, MarketPopularBean(1, "", 1.0, 1.0, "", "", 1, "", 1, "", 0, 0.0))
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
            holder.tvChange.text = "${pairBean.gain}%"
            if (pairBean.gain > 0) {
                holder.tvChange.setTxtColor(R.color.color_up)
            } else {
                holder.tvChange.setTxtColor(R.color.color_down)
            }

            holder.tvName.text = pairBean.transactionPair
            holder.tvExchange.text = pairBean.exchangeRate.toString()
            holder.tvCost.text = (pairBean.exchangeRate * 500).toString()
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
        var tvChange: TextView
        var tvName: TextView
        var tvExchange: TextView
        var tvCost: TextView

        init {
            tvChange = itemView.tv_change_rhtp
            tvName = itemView.tv_name_rhtp
            tvExchange = itemView.tv_exchange_rhtp
            tvCost = itemView.tv_cost_rhtp
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}