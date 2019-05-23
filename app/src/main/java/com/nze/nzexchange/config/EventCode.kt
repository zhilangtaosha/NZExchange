package com.nze.nzexchange.config

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/11/22
 */
class EventCode {
    companion object {
        const val CODE_PULISH = 0x001//OTC发布广告通知
        const val CODE_CONFIRM_PAY = 0x002//OTC确定付款
        const val CODE_REFRESH_ASSET = 0x003
        const val CODE_GET_MAIN_CURRENCY = 0x004
        const val CODE_SELECT_TRANSACTIONPAIR = 0x005
        const val CODE_SELF_SELECT = 0x006//添加删除自选
        const val CODE_LOGIN_SUCCUSS = 0x007// 登录成功
        const val CODE_LOGOUT_SUCCESS = 0x008//退出登录
        const val CODE_REFRESH_MAIN_ACT = 0x009//通知刷新主页面
        const val CODE_REFRESH_USERBEAN = 0x010//通知刷新NzeApp的UserBean
        const val CODE_TRADE_BIBI = 0x011//k线页面通知币币页面买入卖出操作
        const val CODE_APP_TO_FRONT = 0x012//app从后台返回前台
        const val CODE_NO_LOGIN = 0x013//取消登录
        const val CODE_OTC_TRADE_SUCCESS = 0x014//OTC交易成功
        const val CODE_LOGIN_FAIL = 0x015//登录失效
        const val CODE_CHANGE_OTC_CURRENCY = 0x016//切换OTC交易交易币种
        const val CODE_REFRESH_OTC_ORDER = 0x017//刷新OTC订单列表
    }
}