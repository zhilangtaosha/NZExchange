package com.nze.nzexchange.controller.my.setting

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean2.MoneyTypeBean
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.repository.sp.SettingSp
import kotlinx.android.synthetic.main.lv_money_type.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/28
 */
class MoneyTypeAdapter(mContext: Context) : NBaseAda<MoneyTypeBean, MoneyTypeAdapter.ViewHolder>(mContext) {
    val sp: SettingSp by lazy { SettingSp.getInstance(mContext) }

    override fun setLayout(): Int = R.layout.lv_money_type

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: MoneyTypeBean, position: Int) {
        vh.moneyTv.text = "${item.moneyName}(${item.moneySign})"
        if (sp.getString(SettingSp.KEY_MONEY_TYPE) == item.moneySign) {
            vh.moneyIv.visibility = View.VISIBLE
        } else {
            vh.moneyIv.visibility = View.GONE
        }
    }


    class ViewHolder(view: View) {
        val moneyTv: TextView by lazy { view.tv_money_lmt }
        val moneyIv: ImageView by lazy { view.iv_money_lmt }

    }
}