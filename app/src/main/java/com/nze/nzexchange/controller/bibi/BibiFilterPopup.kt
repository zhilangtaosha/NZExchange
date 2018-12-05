package com.nze.nzexchange.controller.bibi

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.*
import com.nze.nzeframework.widget.basepopup.BasePopupWindow
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.BibiFilterBean
import com.nze.nzexchange.controller.base.BaseAda
import com.nze.nzexchange.tools.dp2px

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/5
 */
class BibiFilterPopup(context: Activity?) : BasePopupWindow(context) {
    val buyCurrencyEt: EditText = findViewById(R.id.et_buy_currency_pbf) as EditText
    val unitCurrencyEt: EditText = findViewById(R.id.et_unit_currency_pbf) as EditText
    val unitGv: GridView = findViewById(R.id.gv_pbf) as GridView
    val buyCb: CheckBox = findViewById(R.id.cb_buy_pbf) as CheckBox
    val saleCb: CheckBox = findViewById(R.id.cb_sale_pbf) as CheckBox
    val completeCb: CheckBox = findViewById(R.id.cb_complete_pbf) as CheckBox
    val cancelCb: CheckBox = findViewById(R.id.cb_cancel_pbf) as CheckBox
    val resetBtn: Button = findViewById(R.id.btn_reset_pbf) as Button
    val confirmBtn: Button = findViewById(R.id.btn_confirm_pbf) as Button

    val unitAdapter: UnitGvAdapter by lazy { UnitGvAdapter(context!!) }


    init {
        unitCurrencyEt.setOnClickListener {
            if (unitGv.visibility == View.GONE) {
                unitGv.visibility = View.VISIBLE
            } else {
                unitGv.visibility = View.GONE
            }
        }

        unitGv.adapter = unitAdapter
        unitAdapter.group = BibiFilterBean.getList()
        unitGv.setOnItemClickListener { parent, view, position, id ->
            val list = unitAdapter.group.mapIndexed { index, bibiFilterBean ->
                bibiFilterBean.isChoice = index == position
                bibiFilterBean
            }
            unitAdapter.group = list.toMutableList()
        }

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
            cView as TextView
            filterBean?.run {
                cView.text = currency
                cView.isSelected = isChoice
            }
            return cView
        }

    }
}