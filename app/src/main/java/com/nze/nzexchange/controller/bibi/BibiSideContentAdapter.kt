package com.nze.nzexchange.controller.bibi

import android.content.Context
import android.view.View
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransactionPairBean
import com.nze.nzexchange.controller.base.NBaseAda
import kotlinx.android.synthetic.main.lv_bibi_side_content.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/4
 */
class BibiSideContentAdapter(mContext: Context) : NBaseAda<TransactionPairBean, BibiSideContentAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_bibi_side_content

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: TransactionPairBean) {

    }


    class ViewHolder(var view: View) {
        val choiceCb = view.cb_choice_lbsc
        val transactionTv = view.tv_transaction_lbsc
        val costTv = view.tv_cost_lbsc
        val changeTv = view.tv_change_lbsc
    }
}