package com.nze.nzexchange.controller.market

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.GridView
import android.widget.TextView
import com.nze.nzeframework.widget.basepopup.BasePopupWindow
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.BaseAda
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.extend.setTxtColor
import com.nze.nzexchange.tools.dp2px

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/1/22
 */
class FenshiPopup(context: Activity?) : BasePopupWindow(context) {
    val gridView: GridView = findViewById(R.id.gv_pf) as GridView
    val data = listOf<String>("分时", "1分", "5分", "15分", "30分", "1小时", "2小时", "4小时", "6小时", "12小时", "日线", "周线")
    val fenshiAdapter: FenshiAdapter by lazy { FenshiAdapter(context!!) }
    var currentIndex = 0
    var onItemClick: ((position: Int) -> Unit)? = null

    init {
        gridView.adapter = fenshiAdapter
        fenshiAdapter.group = data.toMutableList()
        gridView.setOnItemClickListener { parent, view, position, id ->
            currentIndex = position
            fenshiAdapter.currentIndex = currentIndex
            fenshiAdapter.notifyDataSetChanged()
            onItemClick?.invoke(position)
            dismiss()
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

    override fun onCreatePopupView(): View = createPopupById(R.layout.popup_fenshi)

    override fun initAnimaView(): View = findViewById(R.id.gv_pf)

    class FenshiAdapter(mContext: Context) : BaseAda<String>(mContext) {
        var currentIndex = 0;

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val cView = mInflater.inflate(R.layout.gv_fenshi, parent, false)
            val tv = cView as TextView
            if (currentIndex == position) {
                tv.setTxtColor(R.color.color_main)
            } else {
                tv.setTxtColor(R.color.color_common)
            }
            tv.text = getItem(position)
            return cView
        }

    }
}