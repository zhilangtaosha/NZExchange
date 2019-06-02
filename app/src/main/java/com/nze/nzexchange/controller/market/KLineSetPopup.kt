package com.nze.nzexchange.controller.market

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.CheckBox
import android.widget.TextView
import com.nze.nzeframework.widget.basepopup.BasePopupWindow
import com.nze.nzexchange.R
import com.nze.nzexchange.config.KLineParam
import com.nze.nzexchange.config.Preferences
import com.nze.nzexchange.tools.dp2px
import net.grandcentrix.tray.AppPreferences

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/6/2
 */
class KLineSetPopup(context: Activity?) : BasePopupWindow(context), View.OnClickListener {

    val maTv: TextView = findViewById(R.id.tv_ma_pks) as TextView
    val bollTv: TextView = findViewById(R.id.tv_boll_pks) as TextView
    val mainCb: TextView = findViewById(R.id.cb_main_pks) as TextView
    val macdTv: TextView = findViewById(R.id.tv_macd_pks) as TextView
    val kdjTv: TextView = findViewById(R.id.tv_kdj_pks) as TextView
    val rsiTv: TextView = findViewById(R.id.tv_rsi_pks) as TextView
    val wrTv: TextView = findViewById(R.id.tv_wr_pks) as TextView
    val subCb: TextView = findViewById(R.id.cb_sub_pks) as TextView
    val setTv: TextView = findViewById(R.id.tv_set_pks) as TextView
    var mainImage: String = KLineParam.STATUS_MAIN_EMPTY
    var subImage: String = KLineParam.STATUS_SUB_EMPTY
    val appPreferences: AppPreferences by lazy { AppPreferences(context) }
    val mainList = arrayListOf<TextView>(maTv, bollTv)
    val subList = arrayListOf<TextView>(macdTv, kdjTv, rsiTv, wrTv)
    var type = ""

    init {
        maTv.setOnClickListener(this)
        bollTv.setOnClickListener(this)
        macdTv.setOnClickListener(this)
        kdjTv.setOnClickListener(this)
        rsiTv.setOnClickListener(this)
        wrTv.setOnClickListener(this)
        setTv.setOnClickListener(this)
        mainCb.setOnClickListener(this)
        subCb.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_ma_pks -> {
                mainImage = KLineParam.STATUS_MA
                type = mainImage
            }
            R.id.tv_boll_pks -> {
                mainImage = KLineParam.STATUS_BOLL
                type = mainImage
            }
            R.id.tv_macd_pks -> {
                subImage = KLineParam.STATUS_MACD
                type = subImage
            }
            R.id.tv_kdj_pks -> {
                subImage = KLineParam.STATUS_KDJ
                type = subImage
            }
            R.id.tv_rsi_pks -> {
                subImage = KLineParam.STATUS_RSI
                type = subImage
            }
            R.id.tv_wr_pks -> {
                subImage = KLineParam.STATUS_WR
                type = subImage
            }
            R.id.tv_set_pks -> {
                context.startActivity(Intent(context, KLineTargetActivity::class.java))
            }
            R.id.cb_main_pks -> {
                mainImage = KLineParam.STATUS_MAIN_EMPTY
                type = mainImage
            }
            R.id.cb_sub_pks -> {
                subImage = KLineParam.STATUS_SUB_EMPTY
                type = subImage
            }
        }
        appPreferences.put(Preferences.KLINE_MAIN_IMAGE, mainImage)
        appPreferences.put(Preferences.KLINE_SUB_IMAGE, subImage)
        dismiss()
    }

    override fun showPopupWindow(v: View?) {
        super.showPopupWindow(v)
        mainImage = appPreferences.getString(Preferences.KLINE_MAIN_IMAGE, KLineParam.STATUS_MAIN_EMPTY)!!
        subImage = appPreferences.getString(Preferences.KLINE_SUB_IMAGE, KLineParam.STATUS_SUB_EMPTY)!!
        mainList.forEach {
            it.isSelected = false
        }
        subList.forEach {
            it.isSelected = false
        }
        if (mainImage == KLineParam.STATUS_MAIN_EMPTY) {
            mainCb.isSelected = false
        } else {
            mainCb.isSelected = true
            when (mainImage) {
                KLineParam.STATUS_MA -> {
                    maTv.isSelected = true
                }
                KLineParam.STATUS_BOLL -> {
                    bollTv.isSelected = true
                }
            }
        }
        if (subImage == KLineParam.STATUS_MAIN_EMPTY) {
            subCb.isSelected = false
        } else {
            subCb.isSelected = true
            when (subImage) {
                KLineParam.STATUS_MACD -> {
                    macdTv.isSelected = true
                }
                KLineParam.STATUS_KDJ -> {
                    kdjTv.isSelected = true
                }
                KLineParam.STATUS_RSI -> {
                    rsiTv.isSelected = true
                }
                KLineParam.STATUS_WR -> {
                    wrTv.isSelected = true
                }
            }
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

    override fun onCreatePopupView(): View = createPopupById(R.layout.popup_kline_set)

    override fun initAnimaView(): View = findViewById(R.id.layout_root_pks)
}