package com.nze.nzexchange.controller.bibi

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzeframework.widget.basepopup.BasePopupWindow
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.otc.OtcIndicatorAdapter
import com.nze.nzexchange.tools.dp2px
import com.nze.nzexchange.tools.getNColor
import com.nze.nzexchange.widget.indicator.indicator.IndicatorViewPager
import com.nze.nzexchange.widget.indicator.indicator.ScrollIndicatorView
import com.nze.nzexchange.widget.indicator.indicator.slidebar.ColorBar
import com.nze.nzexchange.widget.indicator.indicator.transition.OnTransitionTextListener
import com.nze.nzexchange.widget.indicator.viewpager.SViewPager
import kotlinx.android.synthetic.main.notification_template_lines_media.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/3
 */
class BibiSidePopup(var contxt: BaseActivity, var fragmentManager: FragmentManager) : BasePopupWindow(contxt) {

    override fun initShowAnimation(): Animation {
        return AnimationUtils.loadAnimation(context, R.anim.in_lefttoright)
    }

    override fun initExitAnimation(): Animation {
        return AnimationUtils.loadAnimation(context, R.anim.in_righttoleft)
    }

    override fun getClickToDismissView(): View = popupWindowView

    override fun onCreatePopupView(): View = createPopupById(R.layout.popup_bibi_side)

    override fun initAnimaView(): View = findViewById(R.id.layout_main)
}