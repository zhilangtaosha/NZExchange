package com.nze.nzexchange.repository.sp

import android.content.Context
import com.nze.nzeframework.tool.NLog
import net.grandcentrix.tray.TrayPreferences

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/28
 */
class SettingSp(context: Context, module: String = "setting", version: Int = 1) : TrayPreferences(context, module, version) {

    companion object {
        const val KEY_MONEY_TYPE = "moneyTypeKey"
        const val KEY_COLOR_MODE = "colorModeKey"

        fun getInstance(context: Context): SettingSp {
            return SettingSp(context)
        }
    }


}