package com.nze.nzexchange.controller.common

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.nze.nzeframework.widget.basepopup.BasePopupWindow
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.BaseAda
import com.nze.nzexchange.tools.dp2px
import kotlinx.android.synthetic.main.notification_template_lines_media.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/6
 */
class CommonListPopup(context: Activity?, var type: Int = 0) : BasePopupWindow(context) {
    private val comAdapter: CommonListPopupAdapter by lazy { CommonListPopupAdapter(context!!) }
    private val listView: ListView  by lazy {
        (findViewById(R.id.lv_popup) as ListView).apply {
            setOnItemClickListener { parent, view, position, id ->
                onItemClick?.clickItem(position, comAdapter.group[position])
                this@CommonListPopup.dismiss()
            }
        }
    }
    private val cancelBtn: Button = (findViewById(R.id.btn_cancel_popup) as Button).apply {
        setOnClickListener {
            this@CommonListPopup.dismiss()
        }
    }
    var onItemClick: OnListPopupItemClick? = null
    private val itemList: MutableList<String> by lazy {
        mutableListOf<String>()
    }

    fun addItem(item: String) {
        comAdapter.addItem(item)
    }

    fun addAllItem(list: MutableList<String>) {
        comAdapter.addItems(list)
    }

    init {
        listView.adapter = comAdapter
        comAdapter.group = itemList
    }

    override fun initShowAnimation(): Animation = TranslateAnimation(0f, 0f, dp2px(350F, context).toFloat(), 0f).apply {
        duration = 200
    }

    override fun initExitAnimation(): Animation = TranslateAnimation(0f, 0f, 0f, dp2px(350f, context).toFloat()).apply {
        duration = 200
    }

    override fun getClickToDismissView(): View = popupWindowView

    override fun onCreatePopupView(): View = createPopupById(R.layout.popup_common_list)

    override fun initAnimaView(): View = findViewById(R.id.layout_anim_popup)

    class CommonListPopupAdapter(mContext: Context) : BaseAda<String>(mContext) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val cView = mInflater.inflate(R.layout.lv_common_popup, parent, false)
            cView as TextView
            cView.text = getItem(position)
            return cView
        }
    }

    interface OnListPopupItemClick {
        fun clickItem(position: Int, item: String)
    }
}