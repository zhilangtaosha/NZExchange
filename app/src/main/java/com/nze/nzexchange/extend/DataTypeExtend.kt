package com.nze.nzexchange.extend

import com.nze.nzexchange.bean.Result

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/5/27
 */

typealias OnSuccessRs<T> = (Result<T>) -> Unit
typealias OnErrorRs = (Throwable) -> Unit
typealias NString = String?