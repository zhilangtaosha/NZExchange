package com.nze.nzexchange.http

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/5/15
 */
class HttpConfig {

    companion object {
        var isLoop = true//是否需要轮询
        val LOOP_NUM = 100//轮询次数
        val LOOP_INTERVAL_TIME = 5L//间隔时间
    }
}