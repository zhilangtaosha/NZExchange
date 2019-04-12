package com.nze.nzexchange.controller.market

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.controller.base.NBaseAda
import kotlinx.android.synthetic.main.lv_pair_search.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/12
 */
class PairSearchAdapter(mContext: Context) : NBaseAda<TransactionPairsBean, PairSearchAdapter.ViewHolder>(mContext) {


    var onSelfClick: ((item: TransactionPairsBean, position: Int) -> Unit)? = null
    override fun setLayout(): Int = R.layout.lv_pair_search

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: TransactionPairsBean, position: Int) {
        vh.pairTv.text = "${item.currency}/${item.mainCurrency}"
        vh.choiceCb.isSelected = item.optional == 1

        vh.choiceCb.setOnClickListener {
            onSelfClick?.invoke(item, position)
        }
    }

    fun setSelfClick(f: (item: TransactionPairsBean, position: Int) -> Unit) {
        onSelfClick = f
    }

    class ViewHolder(val view: View) {
        val pairTv: TextView = view.tv_pair_lps
        val choiceCb: TextView = view.cb_choice_lps

    }
}