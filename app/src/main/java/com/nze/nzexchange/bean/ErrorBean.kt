package com.nze.nzexchange.bean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/5/13
 */
data class ErrorBean(
    val errorArgs: List<Any>,
    val errorCode: String,
    val errorMsg: String
)