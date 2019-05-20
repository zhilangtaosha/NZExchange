package com.nze.nzexchange.controller.my.asset.legal.presenter

import com.nze.nzeframework.ui.BaseActivityP
import com.nze.nzeframework.ui.OnErrorRs
import com.nze.nzeframework.ui.OnSuccessRs
import com.nze.nzexchange.bean.LegalRechargeHistoryBean
import com.nze.nzexchange.bean.LegalWithdrawHistoryBean
import com.nze.nzexchange.bean.RealNameAuthenticationBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.http.CRetrofit

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/5/20
 */
class LegalP(activity: NBaseActivity) : BaseActivityP(activity) {

    fun getRechargeHistory(
            userBean: UserBean,
            page: Int,
            pageSize: Int,
            onSuccess: OnSuccessRs<MutableList<LegalRechargeHistoryBean>>,
            onError: OnErrorRs
    ) {
        CRetrofit.instance
                .userService()
                .getLegalRechargeHistory(userBean.tokenReqVo.tokenUserId, userBean.tokenReqVo.tokenUserKey, page, pageSize)
                .compose(activity.netTf())
                .subscribe(onSuccess, onError)
    }


    fun getWithdrawHistory(
            userBean: UserBean,
            page: Int,
            pageSize: Int,
            onSuccess: OnSuccessRs<MutableList<LegalWithdrawHistoryBean>>,
            onError: OnErrorRs
    ) {
        CRetrofit.instance
                .userService()
                .getLegalWithdrawHistory(userBean.tokenReqVo.tokenUserId, userBean.tokenReqVo.tokenUserKey, page, pageSize)
                .compose(activity.netTf())
                .subscribe(onSuccess, onError)
    }
}