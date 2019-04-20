package com.nze.nzexchange.controller.my.asset.legal

import android.content.Context
import android.view.View
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean2.RechargeModeBean
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.extend.setDrawables
import kotlinx.android.synthetic.main.lv_select_recharge_popup.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/20
 */
class SelectRechargeAdapter(mContext: Context) : NBaseAda<RechargeModeBean, SelectRechargeAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_select_recharge_popup

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: RechargeModeBean, position: Int) {
        when (item.mode) {
            RechargeModeBean.BANK -> {
                vh.rechargeTv.setDrawables(R.mipmap.bank_icon, null, null, null)
            }
            RechargeModeBean.OSKO -> {
                vh.rechargeTv.setDrawables(R.mipmap.osko_icon, null, null, null)
            }
            RechargeModeBean.BPAY -> {
                vh.rechargeTv.setDrawables(R.mipmap.bpay_icon, null, null, null)
            }
        }
        vh.rechargeTv.text = item.rechargeName
    }

    class ViewHolder(view: View) {
        val rechargeTv: TextView = view.tv_lsrp
    }
}