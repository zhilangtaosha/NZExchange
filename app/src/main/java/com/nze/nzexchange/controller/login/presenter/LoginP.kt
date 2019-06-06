package com.nze.nzexchange.controller.login.presenter

import com.nze.nzeframework.ui.BaseActivityP
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.OnErrorRs
import com.nze.nzexchange.extend.OnSuccessRs
import com.nze.nzexchange.http.CRetrofit
import com.nze.nzexchange.tools.RegularTool

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/6/6
 */
class LoginP(activity: NBaseActivity) : BaseActivityP(activity) {

    /**
     * 判断手机或者邮箱是否已经注册过，是否存在
     */
    fun checkAccount(account: String, onSuccessRs: OnSuccessRs<Any>, onErrorRs: OnErrorRs) {
        var phone: String? = null
        var email: String? = null
        if (RegularTool.isEmail(account)) {
            email = account
        } else {
            phone = account
        }
        CRetrofit.instance
                .userService()
                .checkAccount(phone, email)
                .compose(activity.netTfWithDialog())
                .subscribe(onSuccessRs, onErrorRs)
    }
}