package com.nze.nzexchange.bean

import com.nze.nzexchange.tools.selectlanguage.LanguageType

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/7/22
 */
data class SelectLanguageBean(
        val type: Int,
        var isChoose: Boolean
) {
    companion object {
        fun getList() = mutableListOf<SelectLanguageBean>().apply {
            add(SelectLanguageBean(LanguageType.LANGUAGE_FOLLOW_SYSTEM, false))
            add(SelectLanguageBean(LanguageType.LANGUAGE_CHINESE_SIMPLIFIED, false))
            add(SelectLanguageBean(LanguageType.LANGUAGE_EN, false))
        }
    }
}