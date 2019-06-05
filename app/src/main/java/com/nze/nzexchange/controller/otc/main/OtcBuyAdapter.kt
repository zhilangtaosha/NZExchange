package com.nze.nzexchange.controller.otc.main

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.OrderPoolBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.BaseAda
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.CheckPermission
import com.nze.nzexchange.controller.otc.BuyActivity
import com.nze.nzexchange.controller.otc.PublishActivity
import com.nze.nzexchange.extend.setBg
import com.nze.nzexchange.extend.setTxtColor
import com.nze.nzexchange.tools.ViewFactory
import kotlinx.android.synthetic.main.lv_buy_otc.view.*

class OtcBuyAdapter(mContext: Context, val type: Int) : BaseAda<OrderPoolBean>(mContext) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var vh: ViewHolder? = null
        var cView: View? = null
        if (convertView == null) {
            cView = mInflater.inflate(R.layout.lv_buy_otc, parent, false)
            vh = ViewHolder(cView)
            cView.tag = vh
        } else {
            cView = convertView
            vh = cView.tag as ViewHolder
        }

        if (type == OtcContentFragment.TYPE_BUY) {
            vh.btn.setBg(R.drawable.selector_green_btn_bg)
            vh.btn.text = "买入"
            vh.priceTv.setTxtColor(R.color.color_FF069F83)
        } else {
            vh.btn.setBg(R.drawable.selector_red_btn_bg)
            vh.btn.text = "卖出"
            vh.priceTv.setTxtColor(R.color.color_FFE05760)
        }
        val item = getItem(position)
        vh.payLayout.removeAllViews()
        item?.let {

            vh.orderNumTv.text = "${it.totalOrder}单"
            vh.limitTv.text = "限额：${it.poolMinamount}-${it.poolMaxamount}CNY"
            vh.priceTv.text = "¥${it.poolPrice}"
            vh.totalNumTv.text = "数量：${it.poolLeftamount}"

            it.accmoney
        }?.run {
            vh.nameTv.text = "${trueName?.substring(0, trueName.length - 1)}*"
            if (accmoneyWeixinurl != null && accmoneyWeixinurl.isNotEmpty())
                vh.payLayout.addView(ViewFactory.createLeftPayMethod(R.mipmap.wechat_icon))
            if (accmoneyZfburl != null && accmoneyZfburl.isNotEmpty())
                vh.payLayout.addView(ViewFactory.createLeftPayMethod(R.mipmap.zhifubao_icon))
            if (accmoneyBankcard != null && accmoneyBankcard.isNotEmpty())
                vh.payLayout.addView(ViewFactory.createLeftPayMethod(R.mipmap.card_icon))
        }

        vh.btn.setOnClickListener {
            if (UserBean.isLogin(mContext)) {
                if (UserBean.loadFromApp()?.userId != item?.userId) {
                    if (type == OtcContentFragment.TYPE_BUY) {
                        mContext.startActivity(Intent(mContext, BuyActivity::class.java)
                                .putExtra(OtcContentFragment.PARAM_TYPE, type)
                                .putExtra(IntentConstant.PARAM_ORDER_POOL, item))
                    } else {
                        CheckPermission.getInstance()
                                .commonCheck(mContext as NBaseActivity, CheckPermission.OTC_COMM_TRADE_BUY, "OTC购买需要完成以下设置，请检查", onPass = {
                                    mContext.startActivity(Intent(mContext, BuyActivity::class.java)
                                            .putExtra(OtcContentFragment.PARAM_TYPE, type)
                                            .putExtra(IntentConstant.PARAM_ORDER_POOL, item))
                                })
                    }

                } else {
                    (mContext as NBaseActivity).showToast("不能与自己交易哦~")
                }
            }
        }
        return cView!!
    }

    private class ViewHolder(view: View) {
        var nameTv: TextView = view.tv_name_lbo
        var limitTv: TextView = view.tv_limit_lbo
        var totalNumTv: TextView = view.tv_total_num_lbo
        var payLayout: LinearLayout = view.layout_pay_lbo
        var orderNumTv: TextView = view.tv_order_num_lbo
        var priceTv: TextView = view.tv_price_lbo
        var btn: Button = view.btn_lbo

    }
}