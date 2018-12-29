package com.nze.nzexchange.controller.my.setting

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean2.ExchangeRateBean
import com.nze.nzexchange.controller.base.NBaseAda
import kotlinx.android.synthetic.main.lv_exchange_rate.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/29
 */
class ExchangeRateAdapter(mContext: Context) : NBaseAda<ExchangeRateBean, ExchangeRateAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_exchange_rate

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: ExchangeRateBean, position: Int) {

    }

    class ViewHolder(view: View) {
        val dateTv: TextView by lazy { view.tv_date_ler }
        val rateTv: TextView by lazy { view.tv_rate_ler }
        val averageTv: TextView by lazy { view.tv_average_ler }

    }
}