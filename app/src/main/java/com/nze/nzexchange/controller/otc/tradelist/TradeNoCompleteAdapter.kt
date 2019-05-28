package com.nze.nzexchange.controller.otc.tradelist

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.SubOrderInfoBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.CurrencyTool
import com.nze.nzexchange.controller.base.BaseAda
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.extend.formatForCurrency
import com.nze.nzexchange.extend.formatForLegal
import com.nze.nzexchange.tools.TimeTool
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.lv_trade_no_complete.view.*
import java.util.concurrent.TimeUnit

class TradeNoCompleteAdapter(mContext: Context, var fragment: NBaseFragment) : NBaseAda<SubOrderInfoBean, TradeNoCompleteAdapter.ViewHolder>(mContext) {

    override fun setLayout(): Int = R.layout.lv_trade_no_complete

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: SubOrderInfoBean, position: Int) {
        item.run {
            var type = ""
            if (UserBean.loadFromApp()?.userId == item.userIdSell) {
                type = if (transactionType == SubOrderInfoBean.TRANSACTIONTYPE_BUY) "卖出" else "买入"
            } else {
                type = if (transactionType == SubOrderInfoBean.TRANSACTIONTYPE_BUY) "买入" else "卖出"
            }

            vh.typeTv.text = "${type}${CurrencyTool.getCurrency(tokenId)}"
            vh.timeTv.text = TimeTool.format(TimeTool.PATTERN2, suborderCreateTime)
            vh.statusTv.text = SubOrderInfoBean.getStatus(suborderStatus)
            vh.priceTv.text = suborderPrice.toString()
            vh.numTv.text = "数量: ${suborderNum.formatForCurrency()}"
            vh.moneyTv.text = "总额: ${suborderAmount.formatForLegal()}"

            if (suborderStatus == SubOrderInfoBean.SUBORDERSTATUS_WAIT_PAY) {
                val time = (System.currentTimeMillis() - phoneTime) / 1000
                vh.countdownTv.text = "(${TimeTool.formatTime(suborderOverTime - time)})"
            } else {
                vh.countdownTv.visibility = View.INVISIBLE
            }


        }

    }

    class ViewHolder(val view: View) {
        val typeTv: TextView = view.tv_type_ltnc
        val timeTv: TextView = view.tv_time_ltnc
        val statusTv: TextView = view.tv_status_ltnc
        val priceTv: TextView = view.tv_price_ltnc
        val numTv: TextView = view.tv_num_ltnc
        val moneyTv: TextView = view.tv_money_ltnc
        val countdownTv: TextView = view.tv_countdown_ltnc
    }
}