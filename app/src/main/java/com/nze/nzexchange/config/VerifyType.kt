package com.nze.nzexchange.config

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/17
 */
class VerifyType {

    companion object {
        const val TYPE_RESIGER_BY_PHONE = "checkCode_sms_regMemb_sendMemb"
        const val TYPE_RESIGER_BY_EMAIL = "checkCode_mail_regMemb_sendMemb"
        const val TYPE_FIND_PWD_BY_PHONE = "checkCode_sms_resetPw_sendMemb"
        const val TYPE_FIND_PWD_BY_EMAIL = "checkCode_mail_resetPw_sendMemb"

    }
}