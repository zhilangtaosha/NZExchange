package com.nze.nzexchange.config

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/5/15
 */
class HttpConfig {

    companion object {
        var isLoop = false//是否需要轮询
        val LOOP_NUM = 1000//轮询次数
        val LOOP_INTERVAL_TIME = 5L//间隔时间
    }
}