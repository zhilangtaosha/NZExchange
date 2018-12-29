package com.nze.nzexchange.controller.my.setting

import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.RelativeLayout
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.repository.sp.SettingSp
import kotlinx.android.synthetic.main.activity_color_mode_set.*

class ColorModeSetActivity : NBaseActivity() {
    val greenUpLayout: RelativeLayout by lazy { layout_green_up_acms }
    val greenUpIv: ImageView by lazy { iv_green_up_acms }
    val redUpLayout: RelativeLayout by lazy { layout_red_up_acms }
    val redUpIv: ImageView by lazy { iv_red_up_acms }
    val sp: SettingSp by lazy { SettingSp(this) }
    val TYPE_GREEN_UP = 0
    val TYPE_RED_UP = 1


    override fun getRootView(): Int = R.layout.activity_color_mode_set

    override fun initView() {
        var type = sp.getInt(SettingSp.KEY_COLOR_MODE, TYPE_GREEN_UP)
        select(type)

        greenUpLayout.setOnClickListener {
            sp.put(SettingSp.KEY_COLOR_MODE, TYPE_GREEN_UP)
            select(TYPE_GREEN_UP)
        }
        redUpLayout.setOnClickListener {
            sp.put(SettingSp.KEY_COLOR_MODE, TYPE_RED_UP)
            select(TYPE_RED_UP)
        }
    }

    fun select(type: Int) {
        if (type == TYPE_GREEN_UP) {
            greenUpIv.visibility = View.VISIBLE
            redUpIv.visibility = View.GONE
        } else {
            greenUpIv.visibility = View.GONE
            redUpIv.visibility = View.VISIBLE
        }
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
