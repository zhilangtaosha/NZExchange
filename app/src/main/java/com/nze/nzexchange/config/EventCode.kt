package com.nze.nzexchange.config

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/11/22
 */
class EventCode {
    companion object {
        const val CODE_PULISH = 0x001
        const val CODE_CONFIRM_PAY = 0x002
        const val CODE_REFRESH_ASSET = 0x003
        const val CODE_GET_MAIN_CURRENCY = 0x004
        const val CODE_SELECT_TRANSACTIONPAIR = 0x005
        const val CODE_SELF_SELECT = 0x006//添加删除自选
        const val CODE_LOGIN_SUCCUSS = 0x007// 登录成功
        const val CODE_LOGOUT_SUCCESS = 0x008//退出登录
    }
}