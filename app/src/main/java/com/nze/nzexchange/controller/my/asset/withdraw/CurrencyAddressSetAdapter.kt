package com.nze.nzexchange.controller.my.asset.withdraw

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean2.CoinAddressBean
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.tools.getNColor
import kotlinx.android.synthetic.main.lv_coin_address_set.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/19
 */
class CurrencyAddressSetAdapter(mContext: Context) : NBaseAda<CoinAddressBean, CurrencyAddressSetAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_coin_address_set

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: CoinAddressBean, position: Int) {
        vh.nameTv.text = item.coinName
        var isSet = false
        if (item.coinAddress.size>0) {
            vh.setTv.setTextColor(getNColor(R.color.color_FF676C7A))
            vh.setTv.text = "已设置"
            isSet = true
        } else {
            vh.setTv.setTextColor(getNColor(R.color.color_FF6D87A8))
            vh.setTv.text = "未设置"
            isSet = false
        }

        vh.setTv.setOnClickListener {
            onSetClickListener?.invoke(isSet)
        }
    }

    var onSetClickListener: ((isSet: Boolean) -> Unit)? = null
    fun setClickListener(click: (isSet: Boolean) -> Unit) {
        onSetClickListener = click
    }

    class ViewHolder(view: View) {
        val nameTv: TextView = view.tv_name_lcas
        val setTv: TextView = view.tv_set_lcas
    }
}