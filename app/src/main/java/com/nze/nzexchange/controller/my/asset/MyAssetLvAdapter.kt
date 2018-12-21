package com.nze.nzexchange.controller.my.asset

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.BibiAssetBean
import com.nze.nzexchange.controller.base.NBaseAda
import kotlinx.android.synthetic.main.lv_my_asset.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/21
 */
class MyAssetLvAdapter(mContext: Context) : NBaseAda<BibiAssetBean, MyAssetLvAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_my_asset

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: BibiAssetBean, position: Int) {
        vh.currencyNameTv.text = item.currency
        vh.availableValueTv.text = item.available.toString()
        vh.freezwValueTv.text = item.freeze.toString()
    }

    class ViewHolder(view: View) {
        val currencyNameTv: TextView by lazy { view.tv_currency_name_lma }
        val availableKeyTv: TextView by lazy { view.tv_available_key_lma }
        val availableValueTv: TextView by lazy { view.tv_available_value_lma }
        val freezwKeyTv: TextView by lazy { view.tv_freeze_key_lma }
        val freezwValueTv: TextView by lazy { view.tv_freeze_value_lma }
        val moneyKeyTv: TextView by lazy { view.tv_money_key_lma }
        val moneyValueTv: TextView by lazy { view.tv_money_value_lma }
    }
}