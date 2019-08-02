package com.nze.nzexchange.controller.bibi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.SoketOrderBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.divByFloor
import com.nze.nzexchange.extend.formatForCurrency
import com.nze.nzexchange.extend.removeE
import com.nze.nzexchange.extend.setTxtColor
import com.nze.nzexchange.tools.TimeTool
import kotlinx.android.synthetic.main.activity_bibi_history_detail.*

class BibiHistoryDetailActivity : NBaseActivity() {

    val statusTv: TextView by lazy { tv_status_lbco }
    val transactionTotalKey by lazy { tv_transaction_total_key_lbco }
    val transactionTotalValue by lazy { tv_transaction_total_value_lbco }
    val transactionPriceKey by lazy { tv_transaction_price_key_lbco }
    val transactionPriceValue by lazy { tv_transaction_price_value_lbco }
    val transactionAmountKey by lazy { tv_transaction_amount_key_lbco }
    val transactionAmountValue by lazy { tv_transaction_amount_value_lbco }
    val feeKey by lazy { tv_fee_key_lbco }
    val feeValue by lazy { tv_fee_value_lbco }
    val pointCardKey by lazy { tv_point_card_key_lbco }
    val dateTv by lazy { tv_date_abhd }
    val dealPriceTv by lazy { tv_deal_price_abhd }
    val dealAmountTv by lazy { tv_deal_amount_abhd }
    val feeValue2 by lazy { tv_fee2_abhd }


    var orderBean: SoketOrderBean? = null

    companion object {
        fun skip(context: Context, bean: SoketOrderBean) {
            context.startActivity(Intent(context, BibiHistoryDetailActivity::class.java)
                    .putExtra(IntentConstant.PARAM_ORDER_POOL, bean))
        }
    }

    override fun getRootView(): Int = R.layout.activity_bibi_history_detail

    override fun initView() {
        intent?.let {
            orderBean = it.getParcelableExtra(IntentConstant.PARAM_ORDER_POOL)
        }

        orderBean?.let {
            if (it.side == 1) {//；卖出
                statusTv.setTxtColor(R.color.color_FFFF4A5F)
                statusTv.text = "卖出"
                feeValue2.text = "${it.deal_fee.formatForCurrency()} ${it.mainCurrency}"
                feeKey.text = "手续费(${it.mainCurrency})"
            } else {//买入
                statusTv.setTxtColor(R.color.color_FF019D81)
                statusTv.text = "买入"
                feeValue2.text = "${it.deal_fee.formatForCurrency()} ${it.currency}"
                feeKey.text = "手续费(${it.currency})"
            }

            transactionTotalKey.text = "成交总额(${it.mainCurrency})"
            transactionPriceKey.text = "成交均价(${it.mainCurrency})"
            transactionAmountKey.text = "成交量(${it.currency})"
            transactionTotalValue.text = it.deal_money.formatForCurrency()
            if (it.deal_stock != 0.0) {//成交均价
                transactionPriceValue.text = it.deal_money.divByFloor(it.deal_stock, 8).formatForCurrency()
                dealPriceTv.text = "${it.deal_money.divByFloor(it.deal_stock, 8).formatForCurrency()} ${it.mainCurrency}"
            } else {
                transactionPriceValue.text = "0"
                dealPriceTv.text = "0"
            }
            transactionAmountValue.text = it.deal_stock.formatForCurrency()
            dealAmountTv.text = "${it.deal_stock.formatForCurrency()} ${it.currency}"

            dateTv.text = TimeTool.format(TimeTool.PATTERN2, (it.ctime * 1000).toLong())
            feeValue.text = "${it.deal_fee.formatForCurrency()}"


        }
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
    }

    override fun getOverridePendingTransitionMode(): TransitionMode = TransitionMode.DEFAULT

    override fun isBindEventBusHere(): Boolean = false

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkDisConnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContainerTargetView(): View? = null

}
