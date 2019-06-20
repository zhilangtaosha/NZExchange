package com.nze.nzexchange.controller.my.asset.withdraw

import android.content.Context
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import com.jakewharton.rxbinding2.widget.RxTextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.CurrenyWithdrawAddressBean
import com.nze.nzexchange.bean2.AddressBean
import com.nze.nzexchange.controller.base.NBaseAda
import kotlinx.android.synthetic.main.lv_select_address.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/19
 */
class SelectCurrencyAddressAdapter(mContext: Context) : NBaseAda<CurrenyWithdrawAddressBean, SelectCurrencyAddressAdapter.ViewHolder>(mContext) {

    var onSelectListener: ((position: Int) -> Unit)? = null

    override fun setLayout(): Int = R.layout.lv_select_address

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: CurrenyWithdrawAddressBean, position: Int) {
        vh.descTv.text = item.title
        vh.currencyTv.text = item.tokenName
        vh.addressTv.text = item.address
        vh.deleteCb.isSelected = item.isSelect

        vh.deleteCb.setOnClickListener {
            onSelectListener?.invoke(position)
        }

    }


    class ViewHolder(view: View) {
        val descTv: TextView = view.tv_desc_lsa
        val currencyTv: TextView = view.tv_currency_type_lsa
        val addressTv: TextView = view.tv_address_lsa
        val deleteCb: TextView = view.cb_delete_lsa

    }
}