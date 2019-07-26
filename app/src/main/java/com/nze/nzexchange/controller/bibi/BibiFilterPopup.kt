package com.nze.nzexchange.controller.bibi

import android.app.Activity
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.*
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxCheckedTextView
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import com.nze.nzeframework.widget.basepopup.BasePopupWindow
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.AllOrderFilterBean
import com.nze.nzexchange.bean.BibiFilterBean
import com.nze.nzexchange.controller.base.BaseAda
import com.nze.nzexchange.database.dao.impl.SoketPairDaoImpl
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.tools.dp2px
import io.reactivex.Flowable
import io.reactivex.Observable
import kotlinx.android.synthetic.main.gv_bibi_popup_filter.view.*
import kotlinx.android.synthetic.main.notification_template_lines_media.view.*
import java.util.function.Consumer

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/5
 */
class BibiFilterPopup(context: Activity?) : BasePopupWindow(context) {
    val pairTv: TextView = findViewById(R.id.tv_pair_pbf) as TextView
    val pairLayout: LinearLayout = findViewById(R.id.layout_pair_pbf) as LinearLayout
    val buyCurrencyEt: EditText = findViewById(R.id.et_buy_currency_pbf) as EditText
    val unitCurrencyTv: TextView = findViewById(R.id.tv_unit_currency_pbf) as TextView
    val unitGv: GridView = findViewById(R.id.gv_pbf) as GridView
    val buyCb: CheckBox = findViewById(R.id.cb_buy_pbf) as CheckBox
    val saleCb: CheckBox = findViewById(R.id.cb_sale_pbf) as CheckBox
    val completeCb: CheckBox = findViewById(R.id.cb_complete_pbf) as CheckBox
    val noCompleteCb: CheckBox = findViewById(R.id.cb_no_complete_pbf) as CheckBox
    val cancelCb: CheckBox = findViewById(R.id.cb_cancel_pbf) as CheckBox
    val resetBtn: Button = findViewById(R.id.btn_reset_pbf) as Button
    val confirmBtn: Button = findViewById(R.id.btn_confirm_pbf) as Button

    val unitAdapter: UnitGvAdapter by lazy { UnitGvAdapter(context!!) }

    var onFilterClick: ((bean: AllOrderFilterBean) -> Unit)? = null
    val statusList: Array<CheckBox> = arrayOf(completeCb, noCompleteCb, cancelCb)
    val styleList: Array<CheckBox> = arrayOf(buyCb, saleCb)
    val mSoketPairDao by lazy { SoketPairDaoImpl() }

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
        val list = mSoketPairDao.getMainCurrency()
        val filterList = mutableListOf<BibiFilterBean>()
        list.forEach {
            filterList.add(BibiFilterBean(it))
        }
        unitGv.adapter = unitAdapter
        unitAdapter.group = filterList
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
        statusList.forEach { cb ->
            RxCompoundButton.checkedChanges(cb)
                    .subscribe { isCheck ->
                        if (isCheck)
                            statusList.filter { cb != it }
                                    .forEach {
                                        it.isChecked = false
                                    }
                    }

        }

        styleList.forEach { cb ->
            RxCompoundButton.checkedChanges(cb)
                    .subscribe { isCheck ->
                        if (isCheck)
                            styleList.filter { cb != it }
                                    .forEach {
                                        it.isChecked = false
                                    }
                    }
        }
    }

    fun reset() {
        buyCurrencyEt.text.clear()
        unitCurrencyTv.text = ""
        unitAdapter.group = unitAdapter.group.map {
            it.isChoice = false
            it
        }.toMutableList()
        Flowable.concat(Flowable.fromArray(styleList), Flowable.fromArray(statusList))
                .subscribe {
                    it.forEach {
                        it.isChecked = false
                    }
                }

    }

    fun confirm() {
        val bean: AllOrderFilterBean = AllOrderFilterBean()
        val currency = buyCurrencyEt.getContent()
        val mainCurrency = unitCurrencyTv.text.toString()
        if (!currency.isNullOrEmpty())
            bean.currency = currency
        if (!mainCurrency.isNullOrEmpty())
            bean.mainCurrency = mainCurrency

        statusList.forEach {
            if (it.isChecked) {
                bean.orderStatus = when (it.text) {
                    "已完成" -> 1003
                    "未完成" -> 1002
                    "已撤销" -> 1004
                    else -> null
                }
                return@forEach
            }
        }
        styleList.forEach {
            if (it.isChecked) {
                bean.tradeType = if (it.text == "买入") {
                    2
                } else if (it.text == "卖出") {
                    1
                } else {
                    0
                }
            }
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


    fun showPair(isShow: Boolean) {
        pairTv.visibility = if (isShow) View.VISIBLE else View.GONE
        pairLayout.visibility = if (isShow) View.VISIBLE else View.GONE
    }

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