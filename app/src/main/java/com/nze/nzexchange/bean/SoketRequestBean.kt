package com.nze.nzexchange.bean

import com.nze.nzexchange.config.KLineParam

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/6/12
 */
class SoketRequestBean(
        var method: String,
        var params: MutableList<Any>,
        var id: Int
) {
    companion object {
        fun create(method: String, id: Int = ++KLineParam.socketId): SoketRequestBean {
            return SoketRequestBean(method, mutableListOf<Any>(), id)
        }
    }
}