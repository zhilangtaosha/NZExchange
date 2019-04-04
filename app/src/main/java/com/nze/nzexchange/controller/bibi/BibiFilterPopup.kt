package com.nze.nzexchange.controller.bibi

import android.app.Activity
import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.*
import com.nze.nzeframework.widget.basepopup.BasePopupWindow
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.AllOrderFilterBean
import com.nze.nzexchange.bean.BibiFilterBean
import com.nze.nzexchange.controller.base.BaseAda
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.tools.dp2px
import kotlinx.android.synthetic.main.gv_bibi_popup_filter.view.*
import kotlinx.android.synthetic.main.notification_template_lines_media.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/5
 */
class BibiFilterPopup(context: Activity?) : BasePopupWindow(context) {
    val buyCurrencyEt: EditText = findViewById(R.id.et_buy_currency_pbf) as EditText
    val unitCurrencyTv: TextView = findViewById(R.id.tv_unit_currency_pbf) as TextView
    val unitGv: GridView = findViewById(R.id.gv_pbf) as GridView
    val buyCb: CheckBox = findViewById(R.id.cb_buy_pbf) as CheckBox
    val saleCb: CheckBox = findViewById(R.id.cb_sale_pbf) as CheckBox
    val completeCb: CheckBox = findViewById(R.id.cb_complete_pbf) as CheckBox
    val cancelCb: CheckBox = findViewById(R.id.cb_cancel_pbf) as CheckBox
    val cbs = listOf<CheckBox>(buyCb, saleCb, completeCb, cancelCb)
    val resetBtn: Button = findViewById(R.id.btn_reset_pbf) as Button
    val confirmBtn: Button = findViewById(R.id.btn_confirm_pbf) as Button

    val unitAdapter: UnitGvAdapter by lazy { UnitGvAdapter(context!!) }

    var onFilterClick: ((bean: AllOrderFilterBean) -> Unit)? = null

    init {
        unitCurrencyTv.setOnClickListener {
            if (unitGv.visibility == View.GONE) {
                unitGv.visibility = View.VISIBLE
                unitCurrencyTv.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context!!, R.mipmap.close_icon2), null)
            } else {
                unitGv.visibility = View.GONE
                unitCurrencyTv.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context!!, R.mipmap.open_icon2), null)
            }
            buyCurrencyEt.clearFocus()
        }

        unitGv.adapter = unitAdapter
        unitAdapter.group = BibiFilterBean.getList()
        unitGv.setOnItemClickListener { parent, view, position, id ->
            unitAdapter.group = unitAdapter.group.mapIndexed { index, bibiFilterBean ->
                bibiFilterBean.isChoice = index == position
                bibiFilterBean
            }.toMutableList()
            unitCurrencyTv.text = unitAdapter.getItem(position)?.currency
            unitGv.visibility = View.GONE
        }

        resetBtn.setOnClickListener { reset() }
        confirmBtn.setOnClickListener { confirm() }
    }

    fun reset() {
        buyCurrencyEt.text.clear()
        unitCurrencyTv.text = ""
        unitAdapter.group = unitAdapter.group.map {
            it.isChoice = false
            it
        }.toMutableList()
        cbs.forEach { it.isChecked = false }

    }

    fun confirm() {
        val bean: AllOrderFilterBean = AllOrderFilterBean()
        val currency = buyCurrencyEt.getContent()
        val mainCurrency = unitCurrencyTv.text.toString()
        if (currency.isNullOrEmpty())
            bean.currency = currency
        if (mainCurrency.isNullOrEmpty())
            bean.mainCurrency = mainCurrency
        if (!(buyCb.isChecked && saleCb.isChecked)) {
            if (buyCb.isChecked)
                bean.tradeType = 1
            if (saleCb.isChecked)
                bean.tradeType = 0
        }

        if (!(completeCb.isChecked && cancelCb.isChecked)) {
            if (completeCb.isChecked)
                bean.orderStatus = 1003
            if (cancelCb.isChecked)
                bean.orderStatus = 1004
        }
        onFilterClick?.invoke(bean)
    }

    override fun initShowAnimation(): Animation {
        val translateAnimation = TranslateAnimation(0f, 0f, (-dp2px(900f)).toFloat(), 0f)
        translateAnimation.duration = 450
        return translateAnimation
    }

    override fun initExitAnimation(): Animation {

        val translateAnimation = TranslateAnimation(0f, 0f, 0f, (-dp2px(900f)).toFloat())
        translateAnimation.duration = 450
        return translateAnimation
    }


    override fun getClickToDismissView(): View = popupWindowView

    override fun onCreatePopupView(): View = createPopupById(R.layout.popup_bibi_filter)

    override fun initAnimaView(): View = findViewById(R.id.layout_anim)


    class UnitGvAdapter(mContext: Context) : BaseAda<BibiFilterBean>(mContext) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val cView = mInflater.inflate(R.layout.gv_bibi_popup_filter, parent, false)
            val filterBean = getItem(position)
            val tv = cView.tv_gbpf
            filterBean?.run {
                tv.text = currency
                tv.isSelected = isChoice
            }


            return cView
        }

    }
}