package com.nze.nzexchange.config

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/5/7
 */
class SubOrderStatus {

    companion object {
        val PENDING_PAYMENT = 1001//待付款
        val PENDING_COIN = 1002//待放币
        val COMPLETE = 1003//已完成
        val ACTIVE_CANCELLATION = 1004//主动取消
        val OVERTIME_CANCELLATION = 1005//超时取消


    }
}