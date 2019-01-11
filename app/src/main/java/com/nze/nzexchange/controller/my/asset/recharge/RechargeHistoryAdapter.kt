package com.nze.nzexchange.controller.my.asset.recharge

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean2.RechargeHistoryBean
import kotlinx.android.synthetic.main.rcv_recharge_history_content.view.*
import kotlinx.android.synthetic.main.rcv_recharge_history_title.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/25
 */
class RechargeHistoryAdapter(var context: Context) : Adapter<RecyclerView.ViewHolder>() {
    val data: MutableList<RechargeHistoryBean> = mutableListOf<RechargeHistoryBean>()
    val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }
    val TYPE_TITLE = 0
    val TYPE_CONTENT = 1

    private var onDetailClick: ((position:Int,item:RechargeHistoryBean) -> Unit)? = null

    fun setDetailClick(click: (position:Int,item:RechargeHistoryBean) -> Unit) {
        onDetailClick = click
    }

    fun setData(list: List<RechargeHistoryBean>) {
        if (data.size > 0)
            data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    fun addAllItem(list: List<RechargeHistoryBean>){
        data.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
            if (data[position].isTitle) {
                TYPE_TITLE
            } else {
                TYPE_CONTENT
            }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_TITLE) {
            val titleView = inflater.inflate(R.layout.rcv_recharge_history_title, viewGroup, false)
            RechargeHistoryViewHolder.Title(titleView)
        } else {
            val contentView = inflater.inflate(R.layout.rcv_recharge_history_content, viewGroup, false)
            RechargeHistoryViewHolder.Content(contentView)
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        when (viewHolder) {
            is RechargeHistoryViewHolder.Title -> {
                viewHolder.titleTv.text = item.title
            }
            is RechargeHistoryViewHolder.Content -> {
                viewHolder.monthTv.text = item.month
                viewHolder.timeTv.text = item.time
                viewHolder.rechargeTypeTv.text = item.type
                viewHolder.rechargeAmountTv.text = item.rechargeAmount
                viewHolder.itemView.setOnClickListener {
                    onDetailClick?.invoke(position,item)
                }
            }
        }
    }

    sealed class RechargeHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        class Title(itemView: View) : RechargeHistoryViewHolder(itemView) {
            val titleTv: TextView = itemView.tv_title_rrht
        }

        class Content(itemView: View) : RechargeHistoryViewHolder(itemView) {
            val monthTv: TextView = itemView.tv_month_rrhc
            val timeTv: TextView = itemView.tv_time_rrhc
            val detailIv: ImageView = itemView.iv_detail_rrhc
            val rechargeAmountTv: TextView = itemView.tv_amount_recharge_rrhc
//            val totalAmountTv: TextView = itemView.tv_total_amount_rrhc
            val rechargeTypeTv: TextView = itemView.tv_recharge_type_rrhc
        }
    }

}