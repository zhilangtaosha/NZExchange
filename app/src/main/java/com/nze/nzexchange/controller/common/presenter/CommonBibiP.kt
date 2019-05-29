package com.nze.nzexchange.controller.common.presenter

import com.nze.nzeframework.ui.BaseActivityP
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.OnErrorRs
import com.nze.nzexchange.extend.OnSuccessRs
import com.nze.nzexchange.http.CRetrofit

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:币币操作相关的通用presenter
 * @创建时间：2019/5/29
 */
class CommonBibiP(activity: NBaseActivity) : BaseActivityP(activity) {

    companion object {
        fun getInstance(activity: NBaseActivity): CommonBibiP {
            return CommonBibiP(activity)
        }
    }

    fun currencyToLegal(currery: String, amt: Double, onSuccessRs: OnSuccessRs<Double>, onErrorRs: OnErrorRs) {
        CRetrofit.instance
                .userService()
                .currencyToLegal(currery, amt)
                .compose(activity.netTfWithDialog())
                .subscribe(onSuccessRs, onErrorRs)

    }
}