package com.nze.nzexchange.controller.market

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.controller.base.NBaseAda
import kotlinx.android.synthetic.main.lv_optional_edit.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/22
 */
class OptionalEditAdapter(mContext: Context) : NBaseAda<TransactionPairsBean, OptionalEditAdapter.ViewHolder>(mContext) {

    var onRemoveListener: ((item: TransactionPairsBean, position: Int) -> Unit)? = null

    override fun setLayout(): Int = R.layout.lv_optional_edit

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: TransactionPairsBean, position: Int) {
        vh.currencyTv.text = item.transactionPair
        vh.removeIv.setOnClickListener {
            onRemoveListener?.invoke(item, position)
        }
    }

    class ViewHolder(view: View) {
        val removeIv: ImageView = view.iv_remove_loe
        val currencyTv: TextView = view.tv_currency_loe
        val dragIv: ImageView = view.iv_drag_loe
    }
}