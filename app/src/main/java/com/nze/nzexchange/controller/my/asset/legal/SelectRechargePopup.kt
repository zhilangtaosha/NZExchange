package com.nze.nzexchange.controller.my.asset.legal

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.nze.nzeframework.widget.basepopup.BasePopupWindow
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.SetPayMethodBean
import com.nze.nzexchange.bean2.RechargeModeBean
import com.nze.nzexchange.controller.my.paymethod.BindRechargeActivity
import com.nze.nzexchange.tools.dp2px

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/19
 */
class SelectRechargePopup(context: Activity) : BasePopupWindow(context) {
    val closeIv: ImageView by lazy { findViewById(R.id.iv_close_dsr) as ImageView }
    val listView: ListView by lazy { findViewById(R.id.lv_dsr) as ListView }
    val addTv: TextView by lazy { findViewById(R.id.tv_add_dsr) as TextView }

    val rechargeAdapter: SelectRechargeAdapter by lazy { SelectRechargeAdapter(getContext()) }
    var onItemClick: ((RechargeModeBean) -> Unit)? = null

    init {
        listView.adapter = rechargeAdapter
        closeIv.setOnClickListener {
            dismiss()
        }

        addTv.setOnClickListener {
            context.startActivity(Intent(context, BindRechargeActivity::class.java))
            dismiss()
        }

        listView.setOnItemClickListener { parent, view, position, id ->
            onItemClick?.invoke(rechargeAdapter.getItem(position)!!)
            dismiss()
        }
    }

    override fun initAnimaView(): View = findViewById(R.id.layout_dsr)


    override fun getClickToDismissView(): View = popupWindowView

    override fun onCreatePopupView(): View = createPopupById(R.layout.dialog_select_recharge)

    override fun initShowAnimation(): Animation = TranslateAnimation(0f, 0f, dp2px(350F, context).toFloat(), 0f).apply {
        duration = 200
    }

    override fun initExitAnimation(): Animation = TranslateAnimation(0f, 0f, 0f, dp2px(350f, context).toFloat()).apply {
        duration = 200
    }

    fun setRecharge(list: MutableList<RechargeModeBean>) {
        rechargeAdapter.group = list
    }
}