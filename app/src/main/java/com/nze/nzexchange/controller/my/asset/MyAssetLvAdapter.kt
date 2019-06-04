package com.nze.nzexchange.controller.my.asset

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.BibiAssetBean
import com.nze.nzexchange.bean.UserAssetBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.controller.common.presenter.CommonBibiP
import com.nze.nzexchange.extend.add
import com.nze.nzexchange.extend.formatForCurrency
import com.nze.nzexchange.extend.formatForLegal
import com.nze.nzexchange.extend.mul
import kotlinx.android.synthetic.main.lv_my_asset.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/21
 */
class MyAssetLvAdapter(mContext: Context) : NBaseAda<UserAssetBean, MyAssetLvAdapter.ViewHolder>(mContext) {

    var onAssetCallBack: ((m: Double) -> Unit)? = null
    var onAssetItemClick: ((position: Int, item: UserAssetBean) -> Unit)? = null
    var total: Double = 0.0
    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
        total = 0.0
    }

    override fun setLayout(): Int = R.layout.lv_my_asset

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: UserAssetBean, position: Int) {
        vh.currencyNameTv.text = item.currency
        vh.availableValueTv.text = item.available.formatForCurrency()
        vh.freezwValueTv.text = item.freeze.formatForCurrency()

        vh.view.setOnClickListener {
            onAssetItemClick?.invoke(position, item)
        }

        CommonBibiP.getInstance(mContext as NBaseActivity)
                .currencyToLegal(item.currency, 1.0, {
                    if (it.success) {
                        val t = item.amount!!.mul(it.result)
                        total += t
                        vh.moneyValueTv.text = "${t.formatForLegal()}"
                    } else {
                        vh.moneyValueTv.text = "0"
                    }
                    NLog.i("position>>$position  count>>>$count")
                    if (position == count - 1)
                        onAssetCallBack?.invoke(total)
                }, {
                    vh.moneyValueTv.text = "0"
                    if (position == count - 1)
                        onAssetCallBack?.invoke(total)
                })
    }

    class ViewHolder(val view: View) {
        val currencyNameTv: TextView by lazy { view.tv_currency_name_lma }
        val availableKeyTv: TextView by lazy { view.tv_available_key_lma }
        val availableValueTv: TextView by lazy { view.tv_available_value_lma }
        val freezwKeyTv: TextView by lazy { view.tv_freeze_key_lma }
        val freezwValueTv: TextView by lazy { view.tv_freeze_value_lma }
        val moneyKeyTv: TextView by lazy { view.tv_money_key_lma }
        val moneyValueTv: TextView by lazy { view.tv_money_value_lma }
    }
}